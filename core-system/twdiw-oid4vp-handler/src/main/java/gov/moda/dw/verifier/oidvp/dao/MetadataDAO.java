package gov.moda.dw.verifier.oidvp.dao;

import gov.moda.dw.verifier.oidvp.domain.MetadataJpa;
import gov.moda.dw.verifier.oidvp.repository.MetadataRepository;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MetadataDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetadataDAO.class);

    @Autowired
    private MetadataRepository metadataRepository;


    public List<MetadataJpa> getAllFields() throws SQLException {
        try {
            return metadataRepository.findAll();
        } catch (Exception e) {
            LOGGER.error("load metadata fields error", e);
            throw new SQLException("load metadata fields error", e);
        }
    }

    public MetadataJpa saveField(String fieldName, String value) throws SQLException {
        try {
            MetadataJpa metadataJpa = new MetadataJpa(fieldName, value);
            return metadataRepository.saveAndFlush(metadataJpa);
        } catch (Exception e) {
            LOGGER.error("save metadata field error", e);
            throw new SQLException("save metadata field error", e);
        }
    }

    public List<MetadataJpa> saveAll(List<MetadataJpa> metadataJpaList) throws SQLException {
        if (metadataJpaList == null || metadataJpaList.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return metadataRepository.saveAllAndFlush(metadataJpaList);
        } catch (Exception e) {
            LOGGER.error("save metadata fields error", e);
            throw new SQLException("save metadata fields error", e);
        }
    }

    public void deleteField(String fieldName) throws SQLException {
        if (fieldName == null) {
            return;
        }
        try {
            metadataRepository.deleteById(fieldName);
        } catch (Exception e) {
            LOGGER.error("delete metadata field error", e);
            throw new SQLException("delete metadata field error", e);
        }
    }

    public void deleteFields(List<String> fields) throws SQLException {
        if (fields == null || fields.isEmpty()) {
            return;
        }
        try {
            metadataRepository.deleteAllById(fields);
        } catch (Exception e) {
            LOGGER.error("delete metadata fields error", e);
            throw new SQLException("delete metadata fields error", e);
        }
    }
}
