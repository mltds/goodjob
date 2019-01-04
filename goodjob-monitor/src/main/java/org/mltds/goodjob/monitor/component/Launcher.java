package org.mltds.goodjob.monitor.component;

import org.mltds.goodjob.common.dataobject.JobInfo;
import org.mltds.goodjob.common.dataobject.JobSnapshot;
import org.mltds.goodjob.trigger.dao.JobInfoDAO;
import org.mltds.goodjob.trigger.dao.JobSnapshotDAO;
import org.mltds.goodjob.common.dataobject.enums.JobSnapshotStatusEnum;
import org.mltds.goodjob.trigger.dao.query.JobInfoQuery;
import org.mltds.goodjob.trigger.dao.query.JobSnapshotQuery;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author sunyi
 */
@Component
public class Launcher implements InitializingBean {

	private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

	private static final Logger logger = LoggerFactory.getLogger(Launcher.class);

	@Autowired
	private JobInfoDAO jobInfoDAO;

	@Autowired
	private JobSnapshotDAO jobSnapshotDAO;

	@Override
	public void afterPropertiesSet() throws Exception {

		CheckFinishThread checkFinishThread = new CheckFinishThread();
		checkFinishThread.jobInfoDAO = jobInfoDAO;
		checkFinishThread.jobSnapshotDAO = jobSnapshotDAO;
		scheduledExecutorService.scheduleAtFixedRate(checkFinishThread, 0L, 1L, TimeUnit.MINUTES);


		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				scheduledExecutorService.shutdownNow();
			}
		});
	}

	public static class CheckFinishThread implements Runnable {

		private JobInfoDAO jobInfoDAO;
		private JobSnapshotDAO jobSnapshotDAO;
		private Map<Long, String> checked = new HashMap<Long, String>(); // 理论上来说是没有并发访问问题的

		@Override
		public void run() {
			logger.info("CheckFinishThread executing...");

			int i = 0;
			int rows = 10;

			while (true) {

				JobInfoQuery query = new JobInfoQuery();
				query.setIsCheckFinish(true);
				query.setIsActivity(true);
				query.setOffset(i++ * rows);
				query.setRows(rows);

				List<JobInfo> list = jobInfoDAO.findByParam(query);
				if (CollectionUtils.isEmpty(list)) {
					break;
				}

				for (JobInfo jobInfo : list) {
					try {
						check(jobInfo);
					} catch (Exception e) {
						String message = String.format("Check Finish Error，ID: [%d], Name: [%s], Group: [%s], IsCheckFinish: [%b], CheckFinishTime: [%s]",
								new Object[]{jobInfo.getId(), jobInfo.getName(), jobInfo.getGroup(), jobInfo.isCheckFinish(), jobInfo.getCheckFinishTime()});
						logger.error(message, e);
					}
				}
			}

			logger.info("CheckFinishThread finished, good job...");
		}


		private void check(JobInfo jobInfo) {
			String checkFinishTime = jobInfo.getCheckFinishTime();
			String[] split = checkFinishTime.split(":");
			int h = Integer.valueOf(split[0]);
			int m = Integer.valueOf(split[1]);

			Calendar now = Calendar.getInstance();   // 当前时间

			Calendar checkTime = Calendar.getInstance(); // 今天应检查时间
			checkTime.set(Calendar.HOUR_OF_DAY, h);
			checkTime.set(Calendar.MINUTE, m);
			checkTime.set(Calendar.SECOND, 0);
			checkTime.set(Calendar.MILLISECOND, 0);

			Calendar today = Calendar.getInstance(); // 今天凌晨
			today.set(Calendar.HOUR_OF_DAY, 0);
			today.set(Calendar.MINUTE, 0);
			today.set(Calendar.SECOND, 0);
			today.set(Calendar.MILLISECOND, 0);

			if (now.before(checkTime)) {
				return;
			}

			String yyyyMMdd = DateFormatUtils.format(now, "yyyyMMdd");
			if (yyyyMMdd.equals(checked.get(jobInfo.getId()))) {
				return;
			}

			JobSnapshotQuery jobSnapshotQuery = new JobSnapshotQuery();
			jobSnapshotQuery.setJobInfoId(jobInfo.getId());

			jobSnapshotQuery.setActualFinishTimeGte(today.getTime());
			jobSnapshotQuery.setOffset(0);
			jobSnapshotQuery.setRows(1);
			jobSnapshotQuery.setStatus(JobSnapshotStatusEnum.COMPLETED);
			List<JobSnapshot> JobSnapshotList = jobSnapshotDAO.findByParam(jobSnapshotQuery);

			if (JobSnapshotList.size() > 0) {
				// 有执行成功的记录，证明没有问题
				String message = String.format("检查任务完成情况, 任务已经成功执行，JobInfoID: [%d], Name: [%s], Group: [%s], JobSnapshotID: [%d], IsCheckFinish: [%b], CheckFinishTime: [%s]",
						new Object[]{jobInfo.getId(), jobInfo.getName(), jobInfo.getGroup(), JobSnapshotList.get(0).getId(), jobInfo.isCheckFinish(), jobInfo.getCheckFinishTime(),});
				logger.info(message);
			} else {
				// 没有执行成功的记录，要打 110 报警
				sendWarning(jobInfo);

				String message = String.format("检查任务完成情况, 但没有执行成功的记录，JobInfoID: [%d], Name: [%s], Group: [%s], IsCheckFinish: [%b], CheckFinishTime: [%s]",
						new Object[]{jobInfo.getId(), jobInfo.getName(), jobInfo.getGroup(), jobInfo.isCheckFinish(), jobInfo.getCheckFinishTime()});
				logger.warn(message);
			}

			checked.put(jobInfo.getId(), yyyyMMdd);
		}

		private void sendWarning(JobInfo jobInfo) {
			String warning = String.format("goodjobMonitor检查任务完成情况, 但没有执行成功的记录，JobInfoID: [%d], Name: [%s], Group: [%s]",
					new Object[]{jobInfo.getId(), jobInfo.getName(), jobInfo.getGroup()});


			String ownerPhoneStr = jobInfo.getOwnerPhone();
			if (StringUtils.isBlank(ownerPhoneStr)) {
				return;
			}

			String[] ownerPhoneArr = ownerPhoneStr.split(",");
			int sendSucc = 0;

			for (String ownerPhone : ownerPhoneArr) {
				try {

					// 发送报警信息代码，因依赖内部实现，所以删除相关代码是

				} catch (Exception e) {
					logger.error("发送报警信息失败, OwnerPhone:[" + ownerPhone + "], WarningMessage:[" + warning + "]", e);
				}
			}

			if (sendSucc == 0) {
				// 没有一个发送成功，则向外抛异常
				throw new RuntimeException("发送报警信息全部失败, WarningMessage:[" + warning + "]");
			}
		}
	}
}