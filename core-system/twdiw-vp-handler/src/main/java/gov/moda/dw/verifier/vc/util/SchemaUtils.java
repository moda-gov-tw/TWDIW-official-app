package gov.moda.dw.verifier.vc.util;

import net.jimblackler.jsonschemafriend.Schema;
import net.jimblackler.jsonschemafriend.SchemaStore;
import net.jimblackler.jsonschemafriend.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchemaUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchemaUtils.class);

    public static boolean validateBySchema(String schemaJson, String inputJson) {

        boolean result = false;

        try {
            // load schema
            SchemaStore schemaStore = new SchemaStore();
            Schema schema = schemaStore.loadSchemaJson(schemaJson);

            // validation error will throw exception
            Validator validator = new Validator();
            validator.validateJson(schema, inputJson);

            return true;

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        return result;
    }
}
