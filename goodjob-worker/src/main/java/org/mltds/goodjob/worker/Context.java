package org.mltds.goodjob.worker;

/**
 * @author sunyi 2018/11/7.
 */
public class Context {

    private volatile static Integer networkPort;


    public static Integer getNetworkPort() {
        return networkPort;
    }

    protected static void setNetworkPort(Integer networkPort) {
        Context.networkPort = networkPort;
    }
}
