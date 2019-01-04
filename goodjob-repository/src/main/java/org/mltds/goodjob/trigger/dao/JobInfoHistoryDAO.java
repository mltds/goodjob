package org.mltds.goodjob.trigger.dao;

import org.mltds.goodjob.common.dataobject.dataobject.JobInfoHistory;

import java.util.List;

/**
 * 
 * @author chen.jie
 *
 */
public interface JobInfoHistoryDAO {

	int insertJobInfoHistory(JobInfoHistory jobInfoHistory);
	
	/**
	 * 根据name、group查询,默认展示任务列表也会用到
	 */
	List<JobInfoHistory> getListByNameAndGroup(String name, String group, int limit);
	
	JobInfoHistory findJobInfoHistoryById(Long id);
}
