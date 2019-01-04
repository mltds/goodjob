package org.mltds.goodjob.common.network;

/**
 * 负责启动服务，接收请求，序列化、反序列化报文。
 * <p/>
 */
public interface NetworkServer {

    /**
     * server port
     *
     * @return
     */
    Integer getPort();



    /**
     * close the server
     */
    void shutdown();

}
