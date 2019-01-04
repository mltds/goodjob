package org.mltds.goodjob.common.model;

/**
 * 服务端调用 Terminal 请求返回的结果
 */
public class TerminalResponse extends Response {

    /**
     * 是否停止通知成功，成功返回true，失败返回false
     */
    private boolean isSuccess;

    /**
     * 失败返回的错误信息
     */
    private String errorMessage;

    /**
     * 停止内容详情
     */
    private String terminalDetail;

    

}