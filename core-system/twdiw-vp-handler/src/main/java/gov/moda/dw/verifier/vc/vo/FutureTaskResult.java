package gov.moda.dw.verifier.vc.vo;

public class FutureTaskResult<T> {

    private boolean success;

    private int code;

    private String message;

    private T resultData;


    public FutureTaskResult(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public FutureTaskResult<T> setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public int getCode() {
        return code;
    }

    public FutureTaskResult<T> setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public FutureTaskResult<T> setMessage(String message) {
        this.message = message;
        return this;
    }

    public T getResultData() {
        return resultData;
    }

    public FutureTaskResult<T> setResultData(T resultData) {
        this.resultData = resultData;
        return this;
    }
}
