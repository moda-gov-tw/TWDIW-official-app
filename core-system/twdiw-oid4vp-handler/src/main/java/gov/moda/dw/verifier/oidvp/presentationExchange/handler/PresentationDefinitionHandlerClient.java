package gov.moda.dw.verifier.oidvp.presentationExchange.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import gov.moda.dw.verifier.oidvp.model.errors.PresentationEvaluationException;
import gov.moda.dw.verifier.oidvp.model.errors.VcValidatedException;
import gov.moda.dw.verifier.oidvp.presentationExchange.PresentationDefinitionEvaluation.SubmissionInfo;
import gov.moda.dw.verifier.oidvp.presentationExchange.presentationDefinition.PresentationDefinition;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PresentationDefinitionHandlerClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(PresentationDefinitionHandlerClient.class);

    private static final ObjectMapper objectMapper;
    private final PresentationDefinitionHandler handler;
    private final Map<String, ClaimInfo> claimInfoMap = new HashMap<>();

    static {
        objectMapper = new ObjectMapper();
    }

    public PresentationDefinitionHandlerClient() {
        handler = initHandler();
    }

    public PresentationDefinitionHandlerResult evaluate(PresentationDefinition pd, List<SubmissionInfo> submissionInfos)
        throws PresentationEvaluationException, VcValidatedException
    {
        PresentationDefinitionHandler _handler = handler;
        for (; ; ) {
            LOGGER.info("evaluating...{}", _handler.getName());
            _handler.handle(pd, submissionInfos);
            if (!_handler.hasNext()) {
                break;
            }
            _handler = _handler.getNextHandler();
        }
        return new PresentationDefinitionHandlerResult(new ArrayList<>(claimInfoMap.values()));
    }

    protected ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    protected Map<String, ClaimInfo> getClaimInfoMap() {
        return claimInfoMap;
    }

    private PresentationDefinitionHandler initHandler() {
        CredentialFormatHandler handler = new CredentialFormatHandler(this);
        handler.setNextHandler(new InputDescriptorFieldHandler(this))
               .setNextHandler(new LimitDisclosureHandler(this));

        return handler;
    }

    public record ClaimInfo(List<String> type, ObjectNode claims) {}

    public static class PresentationDefinitionHandlerResult {

        private final List<ClaimInfo> claimInfos;
        // tbd...

        public PresentationDefinitionHandlerResult(List<ClaimInfo> claimInfos) {
            this.claimInfos = claimInfos;
        }

        public List<ClaimInfo> getClaimInfos() {
            return claimInfos;
        }
    }
}
