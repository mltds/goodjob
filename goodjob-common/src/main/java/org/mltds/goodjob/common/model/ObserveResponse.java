package org.mltds.goodjob.common.model;

import org.mltds.goodjob.common.model.enums.JobStatus;

/**
 * @author sunyi 2018/11/21.
 */
public class ObserveResponse {

    /**
     * 标识job执行状态
     */
    private JobStatus jobStatus;

    /**
     * 执行完成后返回的结果
     */
    private JobResult jobResult;


}
