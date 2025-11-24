package gov.moda.dw.verifier.vc.task;

import gov.moda.dw.verifier.vc.util.JsonUtils;
import gov.moda.dw.verifier.vc.util.SchemaUtils;
import gov.moda.dw.verifier.vc.vo.VpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class SchemaCheckTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchemaCheckTask.class);

    public boolean validate(Map<String, Object> claims, String schema) throws VpException {

        boolean vcSchemaOK;

        try {
            String claimJson = JsonUtils.mapToJs(claims);
            vcSchemaOK = SchemaUtils.validateBySchema(schema, claimJson);
            if (vcSchemaOK) {
                LOGGER.info("[check from schema]: PASS");
            } else {
                LOGGER.info("[check from schema]: FAIL");
            }

        }  catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new VpException(VpException.ERR_CRED_VALIDATE_VC_SCHEMA_ERROR, "fail to validate vc schema");
        }

        return vcSchemaOK;
    }
}
