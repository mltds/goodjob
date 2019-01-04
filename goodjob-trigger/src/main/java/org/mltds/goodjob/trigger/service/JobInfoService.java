package org.mltds.goodjob.trigger.service;

import org.mltds.commons.lang.Result;
import org.mltds.goodjob.common.dataobject.dataobject.JobInfo;

import java.util.List;

/**
 * 
 * @author chen.jie
 *
 */
public interface JobInfoService {

	/**
	 * 新增一个任务。如果处于激活状态，同时会往Scheduler里面添加一个Job
	 * 
	 * <p>成功：</p>
	 * <pre>
	 * result.isSuccess() = true;
	 * V = result.getData();
	 * </pre>
	 * 
	 * <p>失败：</p>
	 * <pre>
	 * result.isSuccess() = false;
	 * errorMsg = result.getErrorMsg();
	 * </pre>
	 * 
	 * @param jobInfo
	 * @return Result<Long>
	 * 
	 */
	Result<Long> addJobInfo(JobInfo jobInfo);

	/**
	 * 删除一个任务。同时会往历史表里插入一条数据，以及从Scheduler里面删除对应的Job
	 * 
	 * <p>成功：</p>
	 * <pre>
	 * result.isSuccess() = true;
	 * V = result.getData();
	 * </pre>
	 * 
	 * <p>失败：</p>
	 * <pre>
	 * result.isSuccess() = false;
	 * errorMsg = result.getErrorMsg();
	 * </pre>
	 * 
	 * @return Result<Boolean>
	 * 
	 */
	Result<Boolean> deleteJobInfoById(long id);

	/**
	 * 更新一个任务。
	 * <li>如果不激活，删除对应的Job
	 * <li>如果激活：
	 * 			1.Scheduler中不包含对应的Job，创建Job
	 * 			2.Scheduler中包含对应的Job：
	 * 				2.1.cron表达式变更，生成新的Trigger，重新调度Job
	 * 				2.2.cron表达式没有变更，不做任何操作
	 * 
	 * 
	 * <p>成功：</p>
	 * <pre>
	 * result.isSuccess() = true;
	 * V = result.getData();
	 * </pre>
	 * 
	 * <p>失败：</p>
	 * <pre>
	 * result.isSuccess() = false;
	 * errorMsg = result.getErrorMsg();
	 * </pre>
	 * 
	 * @param jobInfo
	 * @return Result<Boolean>
	 * 
	 */
	Result<Long> updateJobInfo(JobInfo jobInfo);

	Result<JobInfo> selectJobInfoById(long id);

	/**
	 * 根据name和group查询,默认展示任务列表也会用到, name 或 group 可以为 null，
	 * 
	 * 
	 * <p>成功：</p>
	 * <pre>
	 * result.isSuccess() = true;
	 * V = result.getData();
	 * </pre>
	 * 
	 * <p>失败：</p>
	 * <pre>
	 * result.isSuccess() = false;
	 * errorMsg = result.getErrorMsg();
	 * </pre>
	 * 
	 * @param name
	 * @param group
	 * @return
	 */
	Result<List<JobInfo>> selectListByNameAndGroup(String name,String group);

	/**
	 * 测试目标服务器是否可用.
	 * @param urls 如果是多条,均需要测试.
	 * @param classPath
	 * @return
	 */
	Result<Boolean> testUrlsAndClassPath(String urls, String classPath);

	/**
	 * 将这个JOB执行一次
	 * @param jobInfo
	 * @return
	 */
	Result<Void> execute(JobInfo jobInfo);



}
