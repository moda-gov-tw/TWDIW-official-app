package gov.moda.dw.verifier.oidvp.presentationExchange.error;

public record CountErrorInfo(String group, CountType type, int expected, int satisfied) {

    public enum CountType {
        ALL,
        COUNT,
        MIN,
        MAX
    }
}
