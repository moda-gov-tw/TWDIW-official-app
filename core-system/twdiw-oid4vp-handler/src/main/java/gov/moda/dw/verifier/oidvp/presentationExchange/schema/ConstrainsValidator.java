package gov.moda.dw.verifier.oidvp.presentationExchange.schema;

import gov.moda.dw.verifier.oidvp.presentationExchange.inputDescriptor.Constraints;
import gov.moda.dw.verifier.oidvp.presentationExchange.inputDescriptor.Field;

public class ConstrainsValidator extends AbstractValidator<Constraints> {

    private final FieldValidator fieldValidator;

    public ConstrainsValidator() {
        fieldValidator = new FieldValidator();
    }

    @Override
    public ValidateResult validate(Constraints constraints) {
        if (constraints == null) {
            return new ValidateResult(false, "constraints must not be null");
        }
        if (constraints.getFields() != null && !constraints.getFields().isEmpty()) {
            ValidateResult validateResult;
            for (Field field : constraints.getFields()) {
                validateResult = fieldValidator.validate(field);
                if (!validateResult.isValid()) {
                    return validateResult;
                }
            }
        }

        return new ValidateResult(true, "constraints is valid");
    }
}
