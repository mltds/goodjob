package org.mltds.goodjob.common.model.enums;

/**
 * worker 执行的方法的标记
 */
public enum RequestMethod {


    /**
     * 执行JOB
     */
    INVOKE,
    /**
     * 观察JOB执行情况
     */
    OBSERVE,
    /**
     * 终止这个JOB
     */
    TERMINATE,
    /***
     * 测试网络情况、并验证JOB是否存在
     */
    TEST



}
