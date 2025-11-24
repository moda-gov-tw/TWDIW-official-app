package gov.moda.dw.verifier.oidvp.presentationExchange.schema;

import gov.moda.dw.verifier.oidvp.presentationExchange.presentationDefinition.SubmissionRequirement;
import gov.moda.dw.verifier.oidvp.presentationExchange.presentationDefinition.SubmissionRequirement.SubmissionRequirementRule;
import java.util.List;

public class SubmissionRequirementsValidator extends AbstractValidator<List<SubmissionRequirement>> {

    @Override
    public ValidateResult validate(List<SubmissionRequirement> submissionRequirements) {
        return validateSubmissionRequirement(submissionRequirements);
    }

    private ValidateResult validateSubmissionRequirement(List<SubmissionRequirement> submissionRequirements) {
        ValidateResult validateResult;
        for (SubmissionRequirement sr : submissionRequirements) {
            if (sr.getFrom() != null) {
                validateResult = validateSubmissionRequirement(sr);
            } else if (sr.getFromNested() != null && !sr.getFromNested().isEmpty()) {
                validateResult = validateSubmissionRequirement(sr.getFromNested());
            } else {
                return new ValidateResult(false, "submission_requirement must have 'from' or 'from_nested' property");
            }

            if (!validateResult.isValid()) {
                return validateResult;
            }
        }
        return new ValidateResult(true, "submission_requirement is valid");
    }

    private ValidateResult validateSubmissionRequirement(SubmissionRequirement sr) {
        SubmissionRequirementRule rule = sr.getRule();
//        if (rule == null) {
//            return new ValidateResult(false, "invalid submission_requirement: 'rule' must be present");
//        }
//
//        if (rule.equals(SubmissionRequirementRule.PICK) && sr.getCount() != null && sr.getCount() <= 0) {
//            return new ValidateResult(false, "invalid submission_requirement: 'count' must greater than 0");
//        }
//        if (rule.equals(SubmissionRequirementRule.PICK) && sr.getMin() != null && sr.getMin() < 0) {
//            return new ValidateResult(false, "invalid submission_requirement: 'min' must be positive");
//        }
//        if (rule.equals(SubmissionRequirementRule.PICK) && sr.getMax() != null && sr.getMax() <= 0) {
//            return new ValidateResult(false, "invalid submission_requirement: 'max' must greater than 0");
//        }

        if (rule.equals(SubmissionRequirementRule.PICK) &&
            sr.getCount() == null && sr.getMin() == null && sr.getMax() == null
        ) {
            return new ValidateResult(false, "invalid submission_requirement: 'rule.pick' should have property of 'count','min','max'");
        }

        return new ValidateResult(true, "submission_requirement is valid");
    }
}
