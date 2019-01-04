package org.mltds.goodjob.trigger.component;

import org.mltds.goodjob.trigger.dao.JobInfoDAO;
import org.mltds.goodjob.trigger.dao.JobSnapshotDAO;
import org.mltds.goodjob.common.dataobject.dataobject.JobInfo;
import org.mltds.goodjob.common.dataobject.dataobject.JobSnapshot;
import org.mltds.goodjob.common.dataobject.enums.JobSnapshotStatusEnum;
import org.mltds.goodjob.trigger.service.SmsService;
import org.mltds.goodjob.trigger.utils.NetUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 启动一个JOB
 *
 * @author sunyi
 *         Created on 15/11/4
 */
@Component
public class JobStartComponent {

	private Logger logger = LoggerFactory.getLogger(JobStartComponent.class);

	@Autowired
	private JobInfoDAO jobInfoDAO;

	@Autowired
	private JobSnapshotDAO jobSnapshotDAO;

	@Autowired
	private SmsService smsService;

	@Transactional(rollbackFor = {Throwable.class})
	public JobSnapshot startJob(Long jobInfoId, boolean isTemporaryExecute) throws Exception {
		JobInfo jobInfo = null;
		JobSnapshot jobSnapshot;

		try {
			jobInfo = jobInfoDAO.findById(jobInfoId);

			if (jobInfo == null) {
				return null;
			}

			if (!jobInfo.isActivity()) {
				return null;
			}

			jobInfo = jobInfoDAO.findByIdForUpdate(jobInfoId);

			boolean needExecute = isNeedExecute(jobInfo, isTemporaryExecute);
			if (!needExecute) return null;

			// 更新 jobInfo 信息
			updateJobInfo(jobInfoId);

			// 创建JobSnapshot
			jobSnapshot = createJobSnapshot(jobInfo);

			logger.info("启动任务成功, JobInfoId:{},name:{},group:{},jobSnapshotId:{}", new Object[]{jobInfoId, jobInfo.getName(), jobInfo.getGroup(), jobSnapshot.getId()});

		} catch (Exception e) {
			if (jobInfo != null && jobInfo.getOwnerPhone() != null) {
				smsService.sendAlertSms(jobInfo.getOwnerPhone(), jobInfoId, jobInfo.getName(), null, "任务启动失败！");
			}
			throw e; // 因为会把异常抛出去， 这里就不重复记日志了
		}

		return jobSnapshot;
	}

	private JobSnapshot createJobSnapshot(JobInfo jobInfo) {
		JobSnapshot jobSnapshot = new JobSnapshot();
		jobSnapshot.setJobInfoId(jobInfo.getId());
		jobSnapshot.setName(jobInfo.getName());
		jobSnapshot.setGroup(jobInfo.getGroup());
		jobSnapshot.setStatus(JobSnapshotStatusEnum.INIT);
		jobSnapshot.setServerAddress(NetUtils.getLocalAddressIp());
		jobSnapshot.setDetail("任务初始化 " + getNowTime() + "\n");
		jobSnapshotDAO.insert(jobSnapshot);

		return jobSnapshot;
	}

	private void updateJobInfo(Long jobInfoId) {
		JobInfo update = new JobInfo();
		update.setId(jobInfoId);
		update.setActivity(true);
		update.setLatestTriggerTime(new Date());
		update.setLatestServerAddress(NetUtils.getLocalAddressIp());

		jobInfoDAO.updateById(update);
	}

	/**
	 * 获取现在的时间 yyyy-MM-dd HH:mm:ss
	 */
	private String getNowTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date());
	}


	/**
	 * 是否需要执行
	 */
	private boolean isNeedExecute(JobInfo jobInfo, boolean isTemporaryExecute) {

		if (isTemporaryExecute) {
			return true; // 临时执行的任务，总是被放过
		}

		// 校验上次执行是否为一分钟内，原因是任务调度服务器是集群的， 防止多次触发任务。
		Date latestTriggerTime = jobInfo.getLatestTriggerTime();
		if (latestTriggerTime == null) {
			return true; // 上次执行为 null，则证明之前没执行过，可以执行
		} else {
			long latestTriggerTimeLong = latestTriggerTime.getTime();
			long now = (new Date()).getTime();
			if (now - latestTriggerTimeLong < 60 * 1000) {
				// 如果距离上次任务触发时间小于一分钟, 则不执行此次任务。
				// 原因是任务调度服务器是集群的， 防止多次触发任务。s
				// 依赖服务器间时间同步
				logger.info("启动任务时主动忽略，因执行间隔小于一分钟。JobInfoId: {}, Name: {}, Group: {}, LatestServerAddress: {}, LatestTriggerTime: {}",
						new Object[]{jobInfo.getId(), jobInfo.getName(), jobInfo.getGroup(),
								jobInfo.getLatestServerAddress(), DateFormatUtils.format(jobInfo.getLatestTriggerTime(), "yyyy-MM-dd HH:mm:ss")});
				return false;
			}
		}

		return true;
	}
}
