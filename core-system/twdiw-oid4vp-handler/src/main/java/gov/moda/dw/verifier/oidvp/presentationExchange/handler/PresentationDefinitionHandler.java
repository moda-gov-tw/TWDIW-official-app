package gov.moda.dw.verifier.oidvp.presentationExchange.handler;

import gov.moda.dw.verifier.oidvp.model.errors.PresentationEvaluationException;
import gov.moda.dw.verifier.oidvp.model.errors.VcValidatedException;
import gov.moda.dw.verifier.oidvp.presentationExchange.PresentationDefinitionEvaluation.SubmissionInfo;
import gov.moda.dw.verifier.oidvp.presentationExchange.presentationDefinition.PresentationDefinition;
import java.util.List;

public abstract class PresentationDefinitionHandler {

    private PresentationDefinitionHandler nextHandler;
    private final String name;
    private final PresentationDefinitionHandlerClient client;

    public PresentationDefinitionHandler(String name, PresentationDefinitionHandlerClient client) {
        this.name = name;
        this.client = client;
    }

    public abstract void handle(PresentationDefinition pd, List<SubmissionInfo> submissionInfos)
        throws PresentationEvaluationException, VcValidatedException;

    public String getName() {
        return name;
    }

    public PresentationDefinitionHandler setNextHandler(PresentationDefinitionHandler nextHandler) {
        this.nextHandler = nextHandler;
        return nextHandler;
    }

    public boolean hasNext() {
        return nextHandler != null;
    }

    public PresentationDefinitionHandler getNextHandler() {
        return nextHandler;
    }

    protected PresentationDefinitionHandlerClient getClient() {
        return client;
    }
}
