package org.mltds.goodjob.trigger.component.job;

import org.mltds.goodjob.trigger.component.JobInvokeComponent;
import org.mltds.goodjob.trigger.component.JobStartComponent;
import org.mltds.goodjob.trigger.component.SpringApplicationContextAware;
import org.mltds.goodjob.trigger.component.execute.ExecutingJobHolder;
import org.mltds.goodjob.common.dataobject.dataobject.JobSnapshot;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

public class CoreJob implements Job {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	public static final String JOB_INFO_ID = "jobinfoid";
	/**
	 * 是否是临时执行的任务，如果是临时执行的任务，会忽略任务间隔一分钟的限制
	 */
	public static final String IS_TEMPORARY_EXECUTE = "istemporaryexecute";

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
		Long jobInfoId = jobDataMap.getLong(JOB_INFO_ID);
		boolean isTemporaryExecute = jobDataMap.getBoolean(CoreJob.IS_TEMPORARY_EXECUTE);

		ApplicationContext ac = SpringApplicationContextAware.getApplicationContext();
		JobStartComponent jobStartComponent = ac.getBean(JobStartComponent.class);
		JobInvokeComponent jobInvokeComponent = ac.getBean(JobInvokeComponent.class);

		JobSnapshot jobSnapshot = null;
		try {
			jobSnapshot = jobStartComponent.startJob(jobInfoId, isTemporaryExecute);
		} catch (Exception e) {
			logger.error("jobStartComponent.startJob error, jobInfoId: " + jobInfoId, e);
		}
		if (jobSnapshot == null) {
			// jobSnapshot 为空时，表示这个任务已经被其他服务器触发并执行了
			return;
		}

		Long jobSnapshotId = jobSnapshot.getId();
		boolean isInvokeSuccess = false;
		try {
			isInvokeSuccess = jobInvokeComponent.invoke(jobInfoId, jobSnapshotId);
		} catch(Exception e) {
			logger.error("jobInvokeComponent.invoke error, jobInfoId: " + jobInfoId, e);
		}
		if (!isInvokeSuccess) {
			// invoke 过程中发生异常或者某些问题，不再继续
			return;
		}

		boolean offerLast = ExecutingJobHolder.offerLastExecutingJob(jobSnapshotId);
		if (!offerLast) {
			logger.warn("任务执行中,再次将任务添加到队列失败, jobSnapshotId: " + jobSnapshotId);
		}

	}
}
