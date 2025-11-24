package gov.moda.dw.verifier.oidvp.presentationExchange.error;

public class ClaimErrorMessage extends PresentationError {

    public static final String EVALUATION_MESSAGE_FORMAT = "(claim_name=%s, input_descriptor_id=%s, type=%s)";

    private final ErrorType type;
    private final String vcType;
    private final String requiredClaimName;
    private final String inputDescriptorId;

    public ClaimErrorMessage(ErrorType type, String requiredClaimName, String inputDescriptorId, String vcType) {
        this.type = type;
        this.requiredClaimName = requiredClaimName;
        this.inputDescriptorId = inputDescriptorId;
        this.vcType = vcType;
    }

    @Override
    public String getEvaluationErrorMessage() {
        return String.format(EVALUATION_MESSAGE_FORMAT, requiredClaimName, inputDescriptorId, vcType);
    }

    @Override
    public String toString() {
        return getEvaluationErrorMessage();
    }

    public enum ErrorType {
        CLAIM_NOT_FOUND,
        FILTER
        // else ...
    }
}
