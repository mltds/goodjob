package org.mltds.goodjob.common.model;

/**
 * 用于测试结果返回
 */
public class TestResponse extends Response {

    /**
     * 成功true，失败false
     */
    private boolean isSuccess;

    /**
     * 成功、失败，返回的结果
     */
    private String errorMessage;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}