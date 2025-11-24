package gov.moda.dw.verifier.oidvp.presentationExchange.schema;

public abstract class AbstractValidator<T> {

    public abstract ValidateResult validate(T t);

    public record ValidateResult(boolean isValid, String message) {}
}
