package org.mltds.goodjob.trigger.dao;

import java.util.List;

import org.mltds.goodjob.common.dataobject.JobInfo;
import org.mltds.goodjob.trigger.dao.query.JobInfoQuery;

public interface JobInfoDAO {

    List<JobInfo> findByParam(JobInfoQuery jobInfoQuery);

    /**
     * 忽略 offset 和 rows
     */
    int countByParam(JobInfoQuery jobInfoQuery);

    /**
     * 找到唯一的一个任务，因为 name 和 group 是联合唯一索引
     * <p/>
     * 两个参数都必须传，
     *
     * @param name
     * @param group
     * @return
     */
    JobInfo findByNameAndGroup(String name, String group);

    int insert(JobInfo jobInfo);

    int updateById(JobInfo jobInfo);

    int deleteById(long id);

    JobInfo findById(long id);

    JobInfo findByIdForUpdate(long id);
}
