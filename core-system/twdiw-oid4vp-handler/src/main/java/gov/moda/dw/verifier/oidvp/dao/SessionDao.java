package gov.moda.dw.verifier.oidvp.dao;

import gov.moda.dw.verifier.oidvp.common.OidvpErrorCode.OidvpError;
import gov.moda.dw.verifier.oidvp.domain.SessionJpa;
import gov.moda.dw.verifier.oidvp.model.errors.InvalidParameterException;
import gov.moda.dw.verifier.oidvp.model.errors.OidvpRuntimeException;
import gov.moda.dw.verifier.oidvp.repository.SessionRepository;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class SessionDao {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SessionDao.class);

    @Autowired SessionRepository sessionRepository;

    public SessionJpa getSessionByTransactionId(String transactionId) throws SQLException {
        SessionJpa sessionJPA;
        try {
            sessionJPA = sessionRepository.findById(transactionId).orElse(null);
        } catch (Exception e) {
            LOGGER.error("getSessionByTransactionId error", e);
            throw new SQLException("get session error", e);
        }
        if (sessionJPA == null) {
            throw new NoSuchElementException("request session is not exist");
        }
        LocalDateTime now = LocalDateTime.now();
        if (sessionJPA.getExpiredTime().isBefore(now)) {
            deleteSession(transactionId);
            LOGGER.warn("session is expired, transactionId=[{}]", transactionId);
            throw new OidvpRuntimeException(OidvpError.INVALID_SESSION, "session is expired ");
        } else {
            return sessionJPA;
        }
    }

    public SessionJpa getSessionByStateNotUsed(String state) throws SQLException {
        SessionJpa session = getSessionByState(state);
        if (session.getValidated() == SessionJpa.NOT_VALIDATED) {
            return session;
        } else {
            throw new OidvpRuntimeException(OidvpError.INVALID_SESSION, "this session has been validated");
        }
    }

    public SessionJpa getSessionByState(String state) throws SQLException {
        return getSessionByState(state, true);
    }

    public void createSession(SessionJpa sessionJpa, int expiredMinute) throws SQLException {
        LocalDateTime expiredTime = LocalDateTime.now().plusMinutes(expiredMinute);
        sessionJpa.setExpiredTime(expiredTime);
        createSession(sessionJpa);
    }

    public int deleteSession(String requestID) throws SQLException {
        try {
            return sessionRepository.deleteSessionById(requestID);
        } catch (Exception e) {
            LOGGER.error("deleteSession error", e);
            throw new SQLException("delete session error", e);
        }
    }

    public int updateValidated(String state) throws SQLException {
        try {
            return sessionRepository.updateValidated(state);
        } catch (Exception e) {
            LOGGER.error("updateValidated error", e);
            throw new SQLException("update session validated error", e);
        }
    }

    private SessionJpa getSessionByState(String state, boolean isExpired) throws SQLException {
        SessionJpa sessionJPA;
        try {
            sessionJPA = sessionRepository.findByState(state).orElse(null);
        } catch (Exception e) {
            LOGGER.error("getSessionByState error", e);
            throw new SQLException("get session error", e);
        }
        if (sessionJPA == null) {
            throw new NoSuchElementException("request session is not exist");
        }
        if (isExpired) {
            LocalDateTime now = LocalDateTime.now();
            if (sessionJPA.getExpiredTime().isBefore(now)) {
                deleteSession(sessionJPA.getTransactionId());
                LOGGER.warn("session is expired, state=[{}]", state);
                throw new OidvpRuntimeException(OidvpError.INVALID_SESSION, "request session is expired");
            } else {
                return sessionJPA;
            }
        }
        return sessionJPA;
    }

    public void createSession(SessionJpa sessionJPA) throws SQLException {
        try {
            sessionRepository.saveAndFlush(sessionJPA);
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().toLowerCase().contains("duplicate")) {
                throw new InvalidParameterException("the transaction_id for the session is already exist");
            } else {
                throw new SQLException("create session error", e);
            }
        } catch (Exception e) {
            LOGGER.error("createSession error", e);
            throw new SQLException("create session error", e);
        }
    }
}
