package gov.moda.dw.verifier.oidvp.presentationExchange.handler;

import gov.moda.dw.verifier.oidvp.common.OidvpErrorCode.OidvpError;
import gov.moda.dw.verifier.oidvp.model.errors.VcValidatedException;
import gov.moda.dw.verifier.oidvp.presentationExchange.PresentationDefinitionEvaluation.SubmissionInfo;
import gov.moda.dw.verifier.oidvp.presentationExchange.inputDescriptor.FormatDescription;
import gov.moda.dw.verifier.oidvp.presentationExchange.inputDescriptor.FormatRegistry;
import gov.moda.dw.verifier.oidvp.presentationExchange.inputDescriptor.FormatRegistry.FormatRegistryType;
import gov.moda.dw.verifier.oidvp.presentationExchange.inputDescriptor.InputDescriptor;
import gov.moda.dw.verifier.oidvp.presentationExchange.presentationDefinition.PresentationDefinition;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CredentialFormatHandler extends PresentationDefinitionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CredentialFormatHandler.class);


    public CredentialFormatHandler(PresentationDefinitionHandlerClient client) {
        super("CredentialFormatHandler", client);
    }

    @Override
    public void handle(PresentationDefinition pd, List<SubmissionInfo> submissionInfos) throws VcValidatedException {
        checkFormat(pd, submissionInfos);
    }

    private void checkFormat(PresentationDefinition pd, List<SubmissionInfo> submissionInfos)
        throws VcValidatedException
    {
        Map<FormatRegistry, FormatDescription> acceptedFormat = pd.getFormat();
        for (SubmissionInfo submissionInfo : submissionInfos) {
            FormatRegistry vpFormat = submissionInfo.vpFormat();
            FormatRegistry vcFormat = submissionInfo.decodedVc().getFormat();
            // presentation_definition
            if (acceptedFormat != null) {
                checkIsAcceptedFormat(acceptedFormat, vpFormat, vcFormat);
            }
            // input_descriptor
            checkFormat(submissionInfo.inputDescriptor(), vpFormat, vcFormat);
        }
        LOGGER.info("[check is vc/vp format accepted(presentation_definition)]: PASS");
    }

    private void checkFormat(InputDescriptor inputDescriptor, FormatRegistry vpFormat, FormatRegistry vcFormat)
        throws VcValidatedException
    {
        Map<FormatRegistry, FormatDescription> acceptedFormat = inputDescriptor.getFormat();
        if (acceptedFormat != null) {
            checkIsAcceptedFormat(acceptedFormat, vpFormat, vcFormat);
        }
        LOGGER.info("[check is vc/vp format accepted(input_descriptor)]: PASS");
    }

    private void checkIsAcceptedFormat(Map<FormatRegistry, FormatDescription> acceptedFormat, FormatRegistry vpFormat, FormatRegistry vcFormat)
        throws VcValidatedException
    {
        if (acceptedFormat == null || acceptedFormat.isEmpty()) {
            // no format constrain, do nothing
            return;
        }

        FormatCheckResult formatCheckResult = checkIsFormatMatched(acceptedFormat, vpFormat, vcFormat);
        if (!formatCheckResult.vcMatched()) {
            String message =
                "vc format(" + vcFormat.getValue() + ") is not match the requirement of presentation_definition";
            throw new VcValidatedException(OidvpError.INVALID_VC_FORMAT, message);
        }
        if (!formatCheckResult.vpMatched()) {
            String message =
                "vp format(" + vpFormat.getValue() + ") is not match the requirement of presentation_definition";
            throw new VcValidatedException(OidvpError.INVALID_VC_FORMAT, message);
        }
    }

    private FormatCheckResult checkIsFormatMatched(Map<FormatRegistry, FormatDescription> acceptedFormat, FormatRegistry vpFormat, FormatRegistry vcFormat) {
        boolean isConstrainContainVc = false;
        boolean isConstrainContainVp = false;
        boolean vpMatched = false;
        boolean vcMatched = false;
        for (FormatRegistry format : acceptedFormat.keySet()) {
            FormatRegistryType type = format.getType();
            switch (type) {
                case VP, SD -> {
                    isConstrainContainVp = true;
                    if (format.equals(vpFormat)) {
                        vpMatched = true;
                    }
                }
                case VC -> {
                    isConstrainContainVc = true;
                    if (format.equals(vcFormat)) {
                        vcMatched = true;
                    }
                }
            }
        }
        return new FormatCheckResult(!isConstrainContainVp || vpMatched, !isConstrainContainVc || vcMatched);
    }

    private record FormatCheckResult(boolean vpMatched, boolean vcMatched) {}
}
