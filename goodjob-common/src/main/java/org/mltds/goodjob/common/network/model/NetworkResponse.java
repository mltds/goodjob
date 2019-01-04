package org.mltds.goodjob.common.network.model;

import org.mltds.goodjob.common.GoodjobException;

/**
 * @author sunyi 2018/11/29.
 */
public class NetworkResponse {

    /**
     * 网络请求的响应状态
     */
    private NetworkStatus networkStatus;

    /**
     * 网络通讯有问题时，{@link #networkStatus } 等于 {@link NetworkStatus#ERROR} 时， 对应的异常信息
     */
    private GoodjobException networkException;

    private Object responseBody;

    public Object getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(Object responseBody) {
        this.responseBody = responseBody;
    }

    public NetworkStatus getNetworkStatus() {
        return networkStatus;
    }

    public void setNetworkStatus(NetworkStatus networkStatus) {
        this.networkStatus = networkStatus;
    }

    public GoodjobException getNetworkException() {
        return networkException;
    }

    public void setNetworkException(GoodjobException networkException) {
        this.networkException = networkException;
    }
}