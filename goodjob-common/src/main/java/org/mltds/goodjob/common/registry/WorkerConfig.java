package org.mltds.goodjob.common.registry;

import java.net.InetSocketAddress;

/**
 * @author sunyi 2018/11/21.
 */
public class WorkerConfig {

    /**
     * 进程ID
     */
    private Integer pid;

    /**
     * 服务器IP + 端口
     */
    private InetSocketAddress serverAddress;

    /**
     * Java Worker 的 Job 类
     */
    private Class JobClass;

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public InetSocketAddress getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(InetSocketAddress serverAddress) {
        this.serverAddress = serverAddress;
    }

    public Class getJobClass() {
        return JobClass;
    }

    public void setJobClass(Class jobClass) {
        JobClass = jobClass;
    }
}
