package org.mltds.goodjob.trigger.dao;

import java.util.Date;
import java.util.List;

import org.mltds.goodjob.common.dataobject.JobSnapshot;
import org.mltds.goodjob.common.dataobject.enums.JobSnapshotStatusEnum;
import org.mltds.goodjob.trigger.dao.query.JobSnapshotQuery;

public interface JobSnapshotDAO {

    JobSnapshot findById(Long id);

    JobSnapshot findByIdForUpdate(Long id);

    void insert(JobSnapshot jobSnapshot);

    /**
     * 更新不为 null 的字段，id 不能为空，modifyTime 默认为系统当前时间
     */
    int updateById(JobSnapshot jobSnapshot);

    /**
     * 更新不为 null 的字段，id 不能为空，modifyTime 默认为系统当前时间<br/>
     * PS: <b>detail字段不是覆盖更新而是追加</b>，提供这个方法的原因是detail字段追加更新的情况比较多<br/>
     * 其他功能与 {@link JobSnapshotDAO#updateById(JobSnapshot)} 方法相同
     */
    int updateByIdAndConcatDetail(JobSnapshot jobSnapshot);

    /**
     * {@link JobSnapshotStatusEnum#EXECUTING} 状态的.
     */
    List<JobSnapshot> findExecutingList();

    List<JobSnapshot> selectJobSnapshotList(JobSnapshot jobSnapshot);

    /**
     * 根据name、group或status查询,默认展示任务列表也会用到
     *
     * @param name
     * @param group
     * @param status
     * @return
     */
    List<JobSnapshot> getListByNameAndGroupAndStatus(String name, String group, String status);

    /**
     * 根据name、group或status查询,默认展示任务列表也会用到
     *
     * @param name
     * @param group
     * @param status
     * @return
     */
    List<JobSnapshot> getListByNameAndGroupAndStatus(String name, String group, String status, Integer limit);

    /**
     * 查询 createTime 之前的记录,并插入到 job_snapshot_history 表.
     */
    void findAndInsertIntoHistoryBeforeCreateTime(Date createTime);

    /**
     * 删除 createTime 之前的记录.
     */
    void deleteBeforeCreateTime(Date createTime);

    List<JobSnapshot> findByParam(JobSnapshotQuery param);

}
