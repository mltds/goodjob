package org.mltds.goodjob.trigger.component;

import org.mltds.goodjob.trigger.component.execute.ExecutingJobHolder;
import org.mltds.goodjob.trigger.component.execute.GetJobResultThread;
import org.mltds.goodjob.trigger.dao.JobInfoDAO;
import org.mltds.goodjob.trigger.dao.JobSnapshotDAO;
import org.mltds.goodjob.common.dataobject.dataobject.JobInfo;
import org.mltds.goodjob.common.dataobject.dataobject.JobSnapshot;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class Launcher implements InitializingBean {

	private static final Logger logger = LoggerFactory.getLogger(Launcher.class);

	public static volatile boolean shutdownNow = false; // 应用现在是否应该关闭

	@Autowired
	private JobInfoDAO jobInfoDAO;

	@Autowired
	private JobSnapshotDAO jobSnapshotDAO;

	@Resource
	private SchedulerWrapper schedulerWrapper;

	/**
	 * 获取任务结果的线程池的数量
	 */
	private final int GET_JOB_RESULT_THREAD_COUNT = 1;

	/**
	 * 获取任务结果的线程池
	 */
	private ExecutorService getJobResultThreadPool = Executors.newFixedThreadPool(GET_JOB_RESULT_THREAD_COUNT);

	/**
	 * 从数据库获取任务列表的线程池
	 */
	private ExecutorService reloadJobThreadPool = Executors.newSingleThreadExecutor();

	public void afterPropertiesSet() {

		logger.info("goodjob server launcher starting...");

		/***************** 从数据库加载已注册的jobInfo 和 正在执行的任务 *****************/
		loadJobInfo();
		loadExecutingJob();

		/***************** 启动获取任务结果的线程池 *****************/
		for (int i = 0; i < GET_JOB_RESULT_THREAD_COUNT; i++) {
			getJobResultThreadPool.execute(new GetJobResultThread());
		}

		/***************** 启动获取数据库JOB列表的线程池 *****************/
		reloadJobThreadPool.execute(new ReloadJobThread());

		/***************** 注册应用停止的钩子 *****************/
		Runtime.getRuntime().addShutdownHook(new ShutdownThread());

		logger.info("goodjob server launcher started!");

	}

	private void loadJobInfo() {

		Set<JobKey> scheduleJobList = new HashSet<JobKey>(0);

		try {
			Scheduler scheduler = schedulerWrapper.getScheduler();
			GroupMatcher<JobKey> matcher = GroupMatcher.anyJobGroup();
			Set<JobKey> jobKeys = scheduler.getJobKeys(matcher);

			scheduleJobList.addAll(jobKeys);
		} catch (SchedulerException e) {
			logger.error("loadJobInfo fail， 获取已经 schedule 的 job 发生异常", e);
		}

		List<JobInfo> jobInfoList = jobInfoDAO.findByParam(null);
		for (JobInfo jobInfo : jobInfoList) {
			try {

				JobDetail jobDetail = schedulerWrapper.createJobDetailByJobInfo(jobInfo);
				JobKey jobKey = jobDetail.getKey();

				if (jobInfo.isActivity()) {
					/***************** 注册激活状态的任务 *****************/
					Trigger trigger = schedulerWrapper.createCronTrigger(jobInfo);
					schedulerWrapper.scheduleJob(jobDetail, trigger);
				} else {
					/***************** 移除停止的任务 *****************/
					schedulerWrapper.deleteJob(jobKey);
				}
				scheduleJobList.remove(jobKey);
			} catch (Exception e) {
				logger.error("loadJobInfo fail, " + jobInfo, e);
			}
		}

		try {
			for (JobKey jobKey : scheduleJobList) {
				schedulerWrapper.deleteJob(jobKey);
			}
		} catch (SchedulerException e) {
			logger.error("loadJobInfo，移除无效的job时，发生异常", e);
		}


	}

	private void loadExecutingJob() {
		/***************** 重新加未完成的任务 *****************/
		List<JobSnapshot> jobSnapshotList = jobSnapshotDAO.findExecutingList();
		for (JobSnapshot jobSnapshot : jobSnapshotList) {
			try {
				Long jobSnapshotId = jobSnapshot.getId();
				boolean succ = ExecutingJobHolder.offerLastExecutingJob(jobSnapshotId);
				if (!succ) {
					logger.warn("loadExecutingJob, offerLastExecutingJob fail, jobSnapshotId: " + jobSnapshotId);
				}
			} catch (Exception e) {
				logger.error("loadExecutingJob error", e);
			}
		}
	}

	private class ReloadJobThread implements Runnable {
		@Override
		public void run() {
			while (!shutdownNow) {
				try {
					loadJobInfo();
				} catch (Exception e) {
					logger.error("Reload Job fail", e);
				}

				try {
					TimeUnit.SECONDS.sleep(10L);
				} catch (InterruptedException e) {
				}
			}
			logger.info(this.getClass().getSimpleName() + "is shutdown");
		}
	}

	private class ShutdownThread extends Thread {
		@Override
		public void run() {
			shutdownNow = true;
			getJobResultThreadPool.shutdownNow();
			reloadJobThreadPool.shutdownNow();
		}
	}
}
