package gov.moda.dw.manager.config;

import jakarta.annotation.PostConstruct;
import liquibase.database.Database;
import liquibase.exception.CustomPreconditionErrorException;
import liquibase.exception.CustomPreconditionFailedException;
import liquibase.precondition.CustomPrecondition;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LiquibaseLoadConfiguration implements CustomPrecondition {
    private final Logger log = LoggerFactory.getLogger(LiquibaseLoadConfiguration.class);

    @Setter
    @Getter
    private String type;

    @Override
    public void check(Database database) throws CustomPreconditionFailedException, CustomPreconditionErrorException {
        String loadType = System.getProperty("custom.liquibase.loadType");
        log.info("loadType.indexOf(inputType):"+loadType.indexOf(type));
        if(loadType.indexOf(type)<0){
            throw new CustomPreconditionFailedException("not fit type");
        }
    }
}
