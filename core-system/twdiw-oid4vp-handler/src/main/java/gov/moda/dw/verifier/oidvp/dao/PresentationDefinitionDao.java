package gov.moda.dw.verifier.oidvp.dao;

import gov.moda.dw.verifier.oidvp.domain.PresentationDefinitionJpa;
import gov.moda.dw.verifier.oidvp.domain.PresentationDefinitionJpa.PresentationDefinitionJpaId;
import gov.moda.dw.verifier.oidvp.domain.VpItemJpa;
import gov.moda.dw.verifier.oidvp.model.errors.InvalidParameterException;
import gov.moda.dw.verifier.oidvp.repository.PDRepository;
import gov.moda.dw.verifier.oidvp.repository.PresentationDefinitionRepository;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PresentationDefinitionDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(PresentationDefinitionDao.class);

    @Autowired PresentationDefinitionRepository presentationDefinitionRepository;
    @Autowired PDRepository pdRepository;

    public String getPresentationDefinition(String ref) throws SQLException {
        // ref format -> <business_id>_<serial_no>
        String[] split = ref.split("_", 2);
        if (split.length != 2) {
            throw new InvalidParameterException("invalid ref format");
        }
        String businessId = split[0];
        String serialNumber = split[1];
        VpItemJpa vpItemJpa;
        try {
            vpItemJpa = presentationDefinitionRepository.getPresentationDefinition(businessId, serialNumber)
                                                        .orElse(null);
        } catch (Exception e) {
            LOGGER.error("get PresentationDefinition error", e);
            throw new SQLException("get PresentationDefinition error", e);
        }
        if (vpItemJpa == null) {
            throw new NoSuchElementException("PresentationDefinition item is not exist");
        }
        return vpItemJpa.getPresentationDefinition();
    }

    public void savePresentationDefinition(String businessId, String serialNo, String presentationDefinition) throws SQLException {
        try {
            PresentationDefinitionJpa presentationDefinitionJpa =
                new PresentationDefinitionJpa().setBusinessId(businessId)
                                               .setSerialNo(serialNo)
                                               .setPresentationDefinition(presentationDefinition);
            pdRepository.saveAndFlush(presentationDefinitionJpa);
        } catch (Exception e) {
            LOGGER.error("save PresentationDefinition error", e);
            throw new SQLException("save PresentationDefinition error", e);
        }
    }

    public String getPresentationDefinitionByRef(String ref) throws SQLException {
        // ref format -> <business_id>_<serial_no>
        String[] split = ref.split("_", 2);
        if (split.length != 2) {
            throw new InvalidParameterException("invalid ref format");
        }
        String businessId = split[0];
        String serialNumber = split[1];
        return getPresentationDefinition(businessId, serialNumber);
    }

    public String getPresentationDefinition(String businessId, String serialNo) throws SQLException {
        PresentationDefinitionJpa presentationDefinitionJpa;
        try {
            PresentationDefinitionJpaId id = new PresentationDefinitionJpaId(businessId, serialNo);
            presentationDefinitionJpa = pdRepository.findById(id).orElse(null);
        } catch (Exception e) {
            LOGGER.error("get PresentationDefinition error", e);
            throw new SQLException("get PresentationDefinition error", e);
        }
        if (presentationDefinitionJpa == null) {
            throw new NoSuchElementException("PresentationDefinition is not exist");
        }
        return presentationDefinitionJpa.getPresentationDefinition();
    }

    public void deletePresentationDefinitionById(String businessId, String serialNo) throws SQLException {
        try {
            pdRepository.delete(new PresentationDefinitionJpa().setBusinessId(businessId).setSerialNo(serialNo));
        } catch (Exception e) {
            LOGGER.error("delete PresentationDefinition error", e);
            throw new SQLException("delete PresentationDefinition error", e);
        }
    }
}
