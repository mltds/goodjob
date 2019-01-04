package org.mltds.goodjob.trigger.component;

import java.util.Calendar;
import java.util.Date;

import javax.annotation.Resource;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import org.mltds.goodjob.trigger.component.job.CoreJob;
import org.mltds.goodjob.common.dataobject.dataobject.JobInfo;
import org.mltds.goodjob.common.dataobject.dataobject.JobSnapshot;
import org.mltds.goodjob.common.dataobject.enums.JobInfoTypeEnum;

/**
 * {@link org.quartz.Scheduler}外覆类，用于封装Scheduler相关操作
 *
 * @author chen.jie
 */
@Component
public class SchedulerWrapper {

	private static final Logger LOG = LoggerFactory.getLogger(SchedulerWrapper.class);

	@Resource
	private Scheduler scheduler;

	/**
	 * 判断scheduler中是否包含特定的JobDetail
	 *
	 * @param jobInfo
	 * @return
	 * @throws SchedulerException
	 */
	public boolean containsJobDetail(JobInfo jobInfo) throws SchedulerException {

		JobKey jobKey = getJobKeyByJobInfo(jobInfo);
		JobDetail jobDetail = scheduler.getJobDetail(jobKey);
		if (jobDetail == null) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 根据jobInfo的name、group获取JobKey
	 *
	 * @param jobInfo
	 * @return
	 */
	public JobKey getJobKeyByJobInfo(JobInfo jobInfo) {
		String name = jobInfo.getName();
		String group = jobInfo.getGroup();

		JobKey jobKey = JobKey.jobKey(name, group);
		return jobKey;
	}

	/**
	 * 根据jobInfo的name、group获取TriggerKey
	 *
	 * @param jobInfo
	 * @return
	 */
	public TriggerKey getTriggerKeyByJobInfo(JobInfo jobInfo) {
		String name = jobInfo.getName();
		String group = jobInfo.getGroup();

		TriggerKey triggerKey = TriggerKey.triggerKey(name, group);
		return triggerKey;
	}

	/**
	 * 根据jobSnapshot的name、group获取JobKey
	 *
	 * @param jobSnapshot
	 * @return
	 */
	public JobKey getJobKeyByJobSnapshot(JobSnapshot jobSnapshot) {
		String name = jobSnapshot.getName() + "_reload_" + jobSnapshot.getId();
		String group = jobSnapshot.getGroup() + "_reload_" + jobSnapshot.getId();

		JobKey jobKey = JobKey.jobKey(name, group);
		return jobKey;
	}

	/**
	 * 根据jobInfo创建一个JobDetail
	 *
	 * @param jobInfo
	 * @return
	 */
	public JobDetail createJobDetailByJobInfo(JobInfo jobInfo) {
		Class<? extends Job> jobClass = CoreJob.class;

		JobKey jobKey = getJobKeyByJobInfo(jobInfo);
		JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobKey).usingJobData(CoreJob.JOB_INFO_ID, jobInfo.getId()).build();

		return jobDetail;
	}

	/**
	 * 创建一个CronTrigger
	 *
	 * @param jobInfo
	 * @return
	 * @throws SchedulerException
	 */
	public Trigger createCronTrigger(JobInfo jobInfo) throws SchedulerException {
		String cron = null;
		JobInfoTypeEnum type = jobInfo.getType();
		if (type.equals(JobInfoTypeEnum.ONCE)) {
			cron = datetimeToCron(jobInfo.getTime());
		} else if (type.equals(JobInfoTypeEnum.REPEAT)) {
			cron = jobInfo.getCron();
		} else {
			LOG.warn("Case unexpected job info type: " + type + ", jobInfo: " + jobInfo);
			return null;
		}
		CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
		TriggerKey triggerKey = getTriggerKeyByJobInfo(jobInfo);
		CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(cronScheduleBuilder).build();

		return trigger;
	}

	/**
	 * 创建一个SimpleTrigger，并且立即执行
	 */
	public Trigger createNowTrigger(String name, String group) throws SchedulerException {
		TriggerKey triggerKey = TriggerKey.triggerKey(name, group);
		Trigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(SimpleScheduleBuilder.simpleSchedule())
				.startNow().build();
		return trigger;
	}

	public JobDetail createJobDetail(Long jobInoId, String name, String group) {
		Class<? extends Job> jobClass = CoreJob.class;
		JobKey jobKey = JobKey.jobKey(name, group);
		JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobKey).usingJobData(CoreJob.JOB_INFO_ID, jobInoId).build();
		return jobDetail;
	}

	/**
	 * 将JobDetail、Trigger添加到scheduler中, 如果已经有了，会 rescheduleJob。
	 *
	 * @param jobDetail
	 * @param trigger
	 * @return
	 * @throws SchedulerException
	 */
	public Date scheduleJob(JobDetail jobDetail, Trigger trigger) throws SchedulerException {
		JobKey jobKey = jobDetail.getKey();
		TriggerKey triggerKey = trigger.getKey();

		if (scheduler.checkExists(jobKey) && scheduler.checkExists(triggerKey)) {
			return rescheduleJob(triggerKey, trigger);
		} else {
			return scheduler.scheduleJob(jobDetail, trigger);
		}
	}

	/**
	 * 根据JobKey删除指定的Job
	 *
	 * @param jobKey
	 * @return
	 * @throws SchedulerException
	 */
	public boolean deleteJob(JobKey jobKey) throws SchedulerException {
		return scheduler.deleteJob(jobKey);
	}

	/**
	 * 根据指定的TriggerKey，删除旧的Trigger，添加新的Trigger
	 *
	 * @param triggerKey
	 * @param newTrigger
	 * @return
	 * @throws SchedulerException
	 */
	public Date rescheduleJob(TriggerKey triggerKey, Trigger newTrigger) throws SchedulerException {
		return scheduler.rescheduleJob(triggerKey, newTrigger);
	}

	/**
	 * 将Date时间转换为 cron 表达式.
	 *
	 * @param date
	 * @return
	 */
	public String datetimeToCron(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		StringBuilder cronBuilder = new StringBuilder();
		cronBuilder.append(c.get(Calendar.SECOND));
		cronBuilder.append(" ");
		cronBuilder.append(c.get(Calendar.MINUTE));
		cronBuilder.append(" ");
		cronBuilder.append(c.get(Calendar.HOUR_OF_DAY));
		cronBuilder.append(" ");
		cronBuilder.append(c.get(Calendar.DAY_OF_MONTH));
		cronBuilder.append(" ");
		// JANUARY 是 0.
		cronBuilder.append(c.get(Calendar.MONTH) + 1);
		cronBuilder.append(" ");
		cronBuilder.append("?");
		cronBuilder.append(" ");
		cronBuilder.append(c.get(Calendar.YEAR));
		String cron = cronBuilder.toString();
		return cron;
	}

	public Scheduler getScheduler() {
		return scheduler;
	}
}
