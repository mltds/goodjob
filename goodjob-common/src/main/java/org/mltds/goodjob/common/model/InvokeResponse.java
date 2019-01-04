package org.mltds.goodjob.common.model;

/**
 * Invoke 请求的返回结果
 */
public class InvokeResponse extends Response {
    /**
     * 是否 invoke 成功，成功返回true，失败返回false
     */
    private boolean isSuccess;

    /**
     * invoke失败返回的错误信息
     */
    private String errorMessage;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
