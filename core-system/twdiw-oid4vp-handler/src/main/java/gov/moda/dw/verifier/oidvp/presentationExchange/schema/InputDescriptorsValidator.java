package gov.moda.dw.verifier.oidvp.presentationExchange.schema;

import gov.moda.dw.verifier.oidvp.presentationExchange.inputDescriptor.Constraints;
import gov.moda.dw.verifier.oidvp.presentationExchange.inputDescriptor.Field;
import gov.moda.dw.verifier.oidvp.presentationExchange.inputDescriptor.InputDescriptor;
import java.util.HashSet;
import java.util.List;

public class InputDescriptorsValidator extends AbstractValidator<List<InputDescriptor>> {

    private final ConstrainsValidator constrainsValidator;

    public InputDescriptorsValidator() {
        constrainsValidator = new ConstrainsValidator();
    }

    @Override
    public ValidateResult validate(List<InputDescriptor> inputDescriptors) {
        ValidateResult validateResult = validateInputDescriptors(inputDescriptors);
        if (!validateResult.isValid()) {
            return validateResult;
        }

        for (InputDescriptor inputDescriptor : inputDescriptors) {
            if (isEmptyId(inputDescriptor)) {
                return new ValidateResult(false, "input_descriptors 'id' must not be empty");
            }
            validateResult = constrainsValidator.validate(inputDescriptor.getConstraints());
            if (!validateResult.isValid()) {
                return validateResult;
            }
        }

        return new ValidateResult(true, "input_descriptors is valid");
    }

    private ValidateResult validateInputDescriptors(List<InputDescriptor> inputDescriptors) {
        if (inputDescriptors == null || inputDescriptors.isEmpty()) {
            return new ValidateResult(false, "input_descriptors should not be empty");
        }
        if (!isUniqueInputDescriptorsId(inputDescriptors)) {
            return new ValidateResult(false, "input_descriptors 'id' is not unique");
        }
        if (!isUniqueFieldId(inputDescriptors)) {
            return new ValidateResult(false, "field 'id' is not unique");
        }

        return new ValidateResult(true, "input_descriptors is valid");
    }

    private boolean isEmptyId(InputDescriptor inputDescriptor) {
        return inputDescriptor.getId() == null || inputDescriptor.getId().isBlank();
    }

    private boolean isUniqueInputDescriptorsId(List<InputDescriptor> inputDescriptors) {
        int inputDescriptorsLen = inputDescriptors.size();
        int _inputDescriptorsLen = inputDescriptors.stream().map(InputDescriptor::getId).distinct().toList().size();
        return inputDescriptorsLen == _inputDescriptorsLen;
    }

    private boolean isUniqueFieldId(List<InputDescriptor> inputDescriptors) {
        List<String> fieldIdList = inputDescriptors.stream()
                                                   .map(InputDescriptor::getConstraints)
                                                   .map(Constraints::getFields)
                                                   .flatMap(fields -> fields.stream()
                                                                            .filter(field -> field.getId() != null))
                                                   .map(Field::getId)
                                                   .toList();
        HashSet<String> idSet = new HashSet<>(fieldIdList);
        return fieldIdList.size() == idSet.size();
    }
}
