package gov.moda.dw.verifier.oidvp.dao;

import gov.moda.dw.verifier.oidvp.domain.OidvpPropertyJpa;
import gov.moda.dw.verifier.oidvp.model.errors.BadOidvpParamException;
import gov.moda.dw.verifier.oidvp.repository.OidvpPropertyRepository;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;

@Service
public class OidvpPropertyDAO {

    private final OidvpPropertyRepository oidvpPropertyRepository;

    public OidvpPropertyDAO(OidvpPropertyRepository oidvpPropertyRepository) {
        this.oidvpPropertyRepository = oidvpPropertyRepository;
    }

    public List<OidvpPropertyJpa> getProperties() throws SQLException {
        try {
            return oidvpPropertyRepository.findAll();
        } catch (Exception e) {
            throw new SQLException("find OidvpProperties error", e);
        }
    }


    public OidvpPropertyJpa saveProperty(String key, String value) throws SQLException {
        if (key == null) {
            throw new BadOidvpParamException("OidvpProperty 'key' must not be null");
        }
        try {
            OidvpPropertyJpa oidvpPropertyJpa = new OidvpPropertyJpa().setKey(key).setValue(value);
            return oidvpPropertyRepository.saveAndFlush(oidvpPropertyJpa);
        } catch (Exception e) {
            throw new SQLException("save OidvpProperty error", e);
        }
    }

    public OidvpPropertyJpa getPropertyByKey(String key) throws SQLException {
        OidvpPropertyJpa findById;
        try {
            findById = oidvpPropertyRepository.findById(key).orElse(null);
        } catch (Exception e) {
            throw new SQLException("get OidvpProperty error", e);
        }
        if (findById == null) {
            throw new NoSuchElementException("OidvpProperty not found, key=" + key);
        }
        return findById;
    }

    public void deletePropertyByKey(String key) throws SQLException {
        try {
            oidvpPropertyRepository.deleteById(key);
        } catch (Exception e) {
            throw new SQLException("delete OidvpProperties error", e);
        }
    }
}
