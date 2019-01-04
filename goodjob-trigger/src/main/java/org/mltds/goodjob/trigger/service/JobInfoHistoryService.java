package org.mltds.goodjob.trigger.service;

import org.mltds.commons.lang.Result;
import org.mltds.goodjob.common.dataobject.dataobject.JobInfoHistory;

import java.util.List;

/**
 * 
 * @author chen.jie
 * 
 */
public interface JobInfoHistoryService {

	/**
	 * 根据name、group查询,默认展示任务列表也会用到
	 * 
	 * @param name
	 * @param group
	 * @return
	 */
	Result<List<JobInfoHistory>> selectListByNameAndGroup(String name, String group, int limit);
	
	Result<JobInfoHistory> selectJobInfoHistoryById(long id);

}
