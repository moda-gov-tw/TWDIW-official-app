package gov.moda.dw.verifier.oidvp.dao;

import gov.moda.dw.verifier.oidvp.common.OidvpErrorCode.OidvpError;
import gov.moda.dw.verifier.oidvp.domain.SessionJpa;
import gov.moda.dw.verifier.oidvp.domain.VerifyResultJpa;
import gov.moda.dw.verifier.oidvp.model.VerifyResult;
import gov.moda.dw.verifier.oidvp.model.errors.OidvpRuntimeException;
import gov.moda.dw.verifier.oidvp.repository.VerifyResultRepository;
import gov.moda.dw.verifier.oidvp.service.oidvp.customData.CustomData;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VerifyResultDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(VerifyResultDao.class);

    @Autowired VerifyResultRepository verifyResultRepository;
    @Autowired SessionDao sessionDao;

    public VerifyResultJpa getVerifyResultNotExpired(String transactionId, int aliveTime) throws SQLException {
        VerifyResultJpa resultJpa = getVerifyResultByTransaction(transactionId);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiredTime = resultJpa.getResponseTime().plusMinutes(aliveTime);
        if (now.isBefore(expiredTime)) {
            return resultJpa;
        } else {
            LOGGER.warn("verifyResult transaction is expired, transaction_id=[{}]", transactionId);
            deleteVerifyResult(transactionId);
            throw new OidvpRuntimeException(OidvpError.INVALID_SESSION, "verifyResult transaction is expired");
        }
    }

    public VerifyResultJpa getVerifyResultNotExpiredByResponseCode(String responseCode, int aliveTime) throws SQLException {
        VerifyResultJpa resultJpa = getVerifyResultByResponseCode(responseCode);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiredTime = resultJpa.getResponseTime().plusMinutes(aliveTime);
        String transactionId = resultJpa.getTransactionId();
        if (now.isBefore(expiredTime)) {
            return resultJpa;
        } else {
            LOGGER.warn("verifyResult transaction is expired, transaction_id=[{}]", transactionId);
            deleteVerifyResult(transactionId);
            throw new OidvpRuntimeException(OidvpError.INVALID_SESSION, "verifyResult transaction is expired");
        }
    }

    public VerifyResultJpa getVerifyResultByResponseCode(String code) throws SQLException {
        VerifyResultJpa verifyResultJpa;
        try {
            verifyResultJpa = verifyResultRepository.findByResponseCode(code).orElse(null);
        } catch (Exception e) {
            LOGGER.error("getVerifyResultByResponseCode error", e);
            throw new SQLException("get VerifyResult error", e);
        }
        if (verifyResultJpa == null) {
            throw new NoSuchElementException("verify result not found");
        }
        return verifyResultJpa;
    }

    public VerifyResultJpa getVerifyResultByTransaction(String id) throws SQLException {
        VerifyResultJpa verifyResultJpa;
        try {
            verifyResultJpa = verifyResultRepository.findById(id).orElse(null);
        } catch (Exception e) {
            LOGGER.error("getVerifyResultByTransaction error", e);
            throw new SQLException("get VerifyResult error", e);
        }
        if (verifyResultJpa == null) {
            throw new NoSuchElementException("verify result not found");
        }
        return verifyResultJpa;
    }

    @Transactional(rollbackFor = Exception.class)
    public VerifyResultJpa updateSessionAndSaveResult(SessionJpa sessionJpa, String responseCode, VerifyResult verifyResult) throws SQLException {
        updateSession(sessionJpa.getState());
        return saveVerifyResult(sessionJpa, responseCode, verifyResult);
    }

    public void deleteVerifyResult(String transactionId) throws SQLException {
        try {
            verifyResultRepository.deleteVerifyResultById(transactionId);
        } catch (Exception e) {
            LOGGER.error("deleteVerifyResult error", e);
            throw new SQLException("delete VerifyResult error", e);
        }
    }

    public void updateSession(String state) throws SQLException {
        int updated = sessionDao.updateValidated(state);
        if (updated == 0) {
            throw new IllegalStateException("this session has been validated");
        }
    }

    private VerifyResultJpa saveVerifyResult(SessionJpa session, String responseCode, VerifyResult verifyResult) throws SQLException {
        VerifyResultJpa verifyResultJpa = new VerifyResultJpa();
        Boolean isSuccess = verifyResult.getVerifyResult();

        verifyResultJpa.setResponseCode(responseCode);
        verifyResultJpa.setTransactionId(session.getTransactionId());
        verifyResultJpa.setVerifyResult(isSuccess);
        verifyResultJpa.setRequestTime(session.getCreateTime());
        verifyResultJpa.setResultDescription(verifyResult.getResultDescription());
        verifyResultJpa.setErrorCode(verifyResult.getErrorCode().getCode());
        verifyResultJpa.setHolderDid(verifyResult.getHolderDid());
        if (isSuccess) {
            verifyResultJpa.setVcClaim(verifyResult.getVcClaims());
            CustomData customData = verifyResult.getCustomData();
            if (customData != null) {
                verifyResultJpa.setCustomData(customData.getJsonValue());
            }
        }

        try {
            return verifyResultRepository.saveAndFlush(verifyResultJpa);
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().toLowerCase().contains("duplicate")) {
                LOGGER.error("duplicate!!", e);
                throw new OidvpRuntimeException(OidvpError.INVALID_SESSION, "the session has been validated");
            } else {
                throw new SQLException("saveVerifyResult error", e);
            }
        } catch (Exception e) {
            LOGGER.error("saveVerifyResult error", e);
            throw new SQLException("create verify result error", e);
        }
    }
}
