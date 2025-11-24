package gov.moda.dw.verifier.oidvp.presentationExchange;

import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SchemaValidatorsConfig;
import com.networknt.schema.SpecVersion.VersionFlag;
import gov.moda.dw.verifier.oidvp.util.JsonUtils;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class JsonSchemaLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonSchemaLoader.class);
    private static final String PRESENTATION_DEFINITION_SCHEMA;
    private static final String PRESENTATION_SUBMISSION_SCHEMA;
    private static final JsonSchemaFactory JSON_SCHEMA_FACTORY_V7;
    private static final SchemaValidatorsConfig VALIDATORS_CONFIG;

    static {
        try {
            PRESENTATION_DEFINITION_SCHEMA = JsonUtils.getJsonStringFromFile(new ClassPathResource("schema/presentation_definition_schema.json").getInputStream());
            LOGGER.info("Loading presentation_definition_schema.json ... OK");
            PRESENTATION_SUBMISSION_SCHEMA = JsonUtils.getJsonStringFromFile(new ClassPathResource("schema/presentation_submission_schema.json").getInputStream());
            LOGGER.info("Loading presentation_submission_schema.json ... OK");

            JSON_SCHEMA_FACTORY_V7 = JsonSchemaFactory.getInstance(VersionFlag.V7);
            VALIDATORS_CONFIG = SchemaValidatorsConfig.builder().cacheRefs(true).failFast(false).build();
        } catch (IOException e) {
            LOGGER.info("Loading presentation_definition_schema.json/presentation_submission_schema.json ... Fail");
            throw new RuntimeException(e);
        }
    }

    @Bean(name = "presentationDefinitionSchema")
    public JsonSchema getPresentationDefinitionSchema() {
        JsonSchema schema = JSON_SCHEMA_FACTORY_V7.getSchema(PRESENTATION_DEFINITION_SCHEMA, VALIDATORS_CONFIG);
        LOGGER.info("Loading presentationDefinitionSchema ... OK");
        return schema;
    }

    @Bean(name = "presentationSubmissionSchema")
    public JsonSchema getPresentationSubmissionSchema() {
        JsonSchema schema = JSON_SCHEMA_FACTORY_V7.getSchema(PRESENTATION_SUBMISSION_SCHEMA, VALIDATORS_CONFIG);
        LOGGER.info("Loading presentationSubmissionSchema ... OK");
        return schema;
    }
}
