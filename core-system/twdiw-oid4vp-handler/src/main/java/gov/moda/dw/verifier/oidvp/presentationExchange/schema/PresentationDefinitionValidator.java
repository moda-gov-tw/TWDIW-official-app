package gov.moda.dw.verifier.oidvp.presentationExchange.schema;

import gov.moda.dw.verifier.oidvp.presentationExchange.inputDescriptor.InputDescriptor;
import gov.moda.dw.verifier.oidvp.presentationExchange.presentationDefinition.PresentationDefinition;
import gov.moda.dw.verifier.oidvp.presentationExchange.presentationDefinition.SubmissionRequirement;
import java.util.HashSet;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PresentationDefinitionValidator extends AbstractValidator<PresentationDefinition> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PresentationDefinitionValidator.class);
    private static final int VALID = 0;
    private static final int INPUT_DESCRIPTORS_NOT_GROUPED = -1;
    private static final int GROUP_AND_FROM_NOT_MATCH = -2;


    private final InputDescriptorsValidator inputDescriptorsValidator;
    private final SubmissionRequirementsValidator submissionRequirementsValidator;

    public PresentationDefinitionValidator() {
        inputDescriptorsValidator = new InputDescriptorsValidator();
        submissionRequirementsValidator = new SubmissionRequirementsValidator();
    }

    @Override
    public ValidateResult validate(PresentationDefinition pd) {
        ValidateResult validateResult = validatePD(pd);
        if (!validateResult.isValid()) {
            return validateResult;
        }
        validateResult = inputDescriptorsValidator.validate(pd.getInputDescriptors());
        if (!validateResult.isValid()) {
            return validateResult;
        }
        if (isUseSubmissionRequirement(pd)) {
            validateResult = submissionRequirementsValidator.validate(pd.getSubmissionRequirements());
            if (!validateResult.isValid()) {
                return validateResult;
            }
        }

        return new ValidateResult(true, "presentation_definition is valid");
    }

    private ValidateResult validatePD(PresentationDefinition pd) {
        if (pd == null) {
            return new ValidateResult(false, "PresentationDefinition must not be null");
        }
        if (pd.getId() == null || pd.getId().isEmpty()) {
            return new ValidateResult(false, "presentation_definition id should not be empty");
        }
        if (pd.getInputDescriptors() == null || pd.getInputDescriptors().isEmpty()) {
            return new ValidateResult(false, "input_descriptors should not be empty");
        }
        Result result = isGroupMatchSubmissionRequirement(pd);
        if (!result.isValid()) {
            return new ValidateResult(false, getMessage(result));
        }

        return new ValidateResult(true, "presentation_definition is valid");
    }

    private boolean isUseSubmissionRequirement(PresentationDefinition pd) {
        return pd.getSubmissionRequirements() != null && !pd.getSubmissionRequirements().isEmpty();
    }

    private Result isGroupMatchSubmissionRequirement(PresentationDefinition pd) {
        if (isUseSubmissionRequirement(pd)) {
            return checkGroup(pd);
        }
        return new Result(true, VALID);
    }

    private Result checkGroup(PresentationDefinition pd) {
        HashSet<String> from = getFrom(pd);
        HashSet<String> group = new HashSet<>();
        for (InputDescriptor inputDescriptor : pd.getInputDescriptors()) {
            List<String> _group = inputDescriptor.getGroup();
            if (_group == null || _group.isEmpty()) {
                LOGGER.error("input_descriptor is not grouped");
                return new Result(false, INPUT_DESCRIPTORS_NOT_GROUPED);
            }
            group.addAll(_group);
        }

        if (from.equals(group)) {
            return new Result(true, VALID);
        } else {
            LOGGER.error("invalid pd schema: the value of 'group' must match one of the strings listed in the 'from' of submission_requirements object");
            return new Result(false, GROUP_AND_FROM_NOT_MATCH);
        }
    }

    private HashSet<String> getFrom(PresentationDefinition pd) {
        return getFrom(pd.getSubmissionRequirements());
    }

    private HashSet<String> getFrom(List<SubmissionRequirement> srs) {
        HashSet<String> set = new HashSet<>();
        for (SubmissionRequirement sr : srs) {
            if (sr.getFrom() != null) {
                set.add(sr.getFrom());
            } else if (sr.getFromNested() != null) {
                HashSet<String> _set = getFrom(sr.getFromNested());
                set.addAll(_set);
            }
        }
        return set;
    }

    private String getMessage(Result result) {
        String message = "success";
        switch (result.errorCode) {
            case INPUT_DESCRIPTORS_NOT_GROUPED ->
                message = "invalid pd schema: when submission_requirement is present input_descriptor must be grouped";
            case GROUP_AND_FROM_NOT_MATCH ->
                message = "invalid pd schema: the value of 'group' must match one of the strings listed in the 'from' of submission_requirements object";
        }
        return message;
    }

    private record Result(boolean isValid, int errorCode) {}
}
