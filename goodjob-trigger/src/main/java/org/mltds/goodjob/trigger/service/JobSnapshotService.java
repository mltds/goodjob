package org.mltds.goodjob.trigger.service;

import java.util.List;

import org.mltds.commons.lang.Result;
import org.mltds.goodjob.worker.model.JobStopResponse;
import org.mltds.goodjob.common.dataobject.dataobject.JobSnapshot;

/**
 * 
 * @author chen.jie
 * 
 */
public interface JobSnapshotService {

	/**
	 * 查找符合搜索条件的JobSnapshot集合
	 * 
	 * @param jobSnapshot
	 * @return
	 */
	Result<List<JobSnapshot>> selectJobSnapshotList(JobSnapshot jobSnapshot);

	/**
	 * 根据name、group或status查询,默认展示任务列表也会用到
	 * 
	 * @param name
	 * @param group
	 * @param status
	 * @return
	 */
	Result<List<JobSnapshot>> selectListByNameAndGroupAndStatus(String name, String group, String status);

	/**
	 * 根据name、group或status查询,默认展示任务列表也会用到, 并对结果数量做一个限制.
	 * 
	 * @param name
	 * @param group
	 * @param status
	 * @param limit
	 *            如果 <= 0, 设为默认 100
	 * @return
	 */
	Result<List<JobSnapshot>> selectListByNameAndGroupAndStatus(String name, String group, String status, int limit);

	Result<JobSnapshot> selectJobSnapshotById(long id);

	/**
	 * 清除一个月以前的数据.<br>
	 * 假如本月 15 号执行, 会保留上个月16号至今的数据.
	 */
	Result<Boolean> dataCleanOneMonthAgo();

	/**
	 * 清除一周以前的数据.<br>
	 */
	Result<Boolean> dataCleanOneWeekAgo();

	
	/**
	 * 获取停止操作任务结果
	 * @param jobSnapshotId
	 * @return
	 */
	public JobStopResponse execStopAndGetResult(Long jobSnapshotId);
}
