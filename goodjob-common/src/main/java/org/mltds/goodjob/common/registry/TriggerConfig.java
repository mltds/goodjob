package org.mltds.goodjob.common.registry;

/**
 * @author sunyi 2018/11/21.
 */
public class TriggerConfig {

    /**
     * 进程ID
     */
    private Integer pid;

    /**
     * 服务器IP
     */
    private String ip;

    /**
     * Trigger Group
     */
    private String group;

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
