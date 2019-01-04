package org.mltds.goodjob.trigger.component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import org.mltds.commons.lang.Result;
import org.mltds.commons.util.HttpClientUtils;
import org.mltds.goodjob.worker.enums.JobStatus;
import org.mltds.goodjob.worker.enums.MethodFlag;
import org.mltds.goodjob.worker.model.JobExecutingResponse;
import org.mltds.goodjob.worker.model.Request;
import org.mltds.goodjob.worker.model.JobResult;
import org.mltds.goodjob.trigger.dao.JobInfoDAO;
import org.mltds.goodjob.trigger.dao.JobSnapshotDAO;
import org.mltds.goodjob.common.dataobject.dataobject.JobInfo;
import org.mltds.goodjob.common.dataobject.dataobject.JobSnapshot;
import org.mltds.goodjob.common.dataobject.enums.JobInfoTypeEnum;
import org.mltds.goodjob.common.dataobject.enums.JobSnapshotStatusEnum;
import org.mltds.goodjob.trigger.service.JobInfoService;
import org.mltds.goodjob.trigger.service.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author sunyi
 *         Created on 15/11/4
 */
@Component
public class JobExecutingComponent {

	private Logger logger = LoggerFactory.getLogger(JobInvokeComponent.class);
	protected static int TIME_OUT = 1000;

	@Autowired
	private JobInfoDAO jobInfoDAO;

	@Autowired
	private JobInfoService jobInfoService;

	@Autowired
	private JobSnapshotDAO jobSnapshotDAO;

	@Autowired
	private SmsService smsService;

	@Transactional(rollbackFor = {Throwable.class})
	public boolean handleExecuting(Long jobSnapshotId) {

		JobSnapshot jobSnapshot = null;
		JobInfo jobInfo = null;

		try {

			jobSnapshot = jobSnapshotDAO.findByIdForUpdate(jobSnapshotId);

			if (!JobSnapshotStatusEnum.EXECUTING.equals(jobSnapshot.getStatus())) {
				return false;
			}

			Long jobInfoId = jobSnapshot.getJobInfoId();
			jobInfo = jobInfoDAO.findById(jobInfoId);

			if (jobInfo == null) {
				String message = "获取任务结果时，JobInfo == null。";
				logger.warn(message + getJobSummary(jobInfo, jobSnapshot));
				jobFail(message, null, jobSnapshot, null);
				return false;
			}

			if (!jobInfo.isActivity()) {
				String message = "获取任务结果时，JobInfo 是不激活状态。";
				logger.warn(message + getJobSummary(jobInfo, jobSnapshot));
				jobFail(message, jobInfo, jobSnapshot, null);
				return false;
			}

			// 获取任务执行情况或者结果.
			JobExecutingResponse exeRes = getExecuteResult(jobInfo, jobSnapshot);

			JobStatus jobStatus = exeRes.getJobStatus();

			if (jobStatus.equals(JobStatus.EXECUTING)) { // 任务执行中
				return true;
			} else if (jobStatus.equals(JobStatus.FINISHED)) { // 任务完成
				JobResult jobResult = exeRes.getJobResult();
				handleJobCompleted(jobInfo, jobSnapshot, jobResult);
				return false;
			} else if (jobStatus.equals(JobStatus.UNKNOW)) { // 目标服务器没有找到任务.
				handleJobUnknow(jobInfo, jobSnapshot);
				return false;
			} else {
				logger.warn("非期望的任务执行状态, " + getJobSummary(jobInfo, jobSnapshot));
				// 理论上不会发生的情况，就当做是正在执行吧
				return true;
			}

		} catch (org.apache.http.conn.ConnectTimeoutException e) {
			// 因超时日志过多，且对系统无重大影响，所以打印 warn 日志
			logger.warn("获取任务结果超时, " + getJobSummary(jobInfo, jobSnapshot), e);
			return true;
		} catch (Throwable e) {
			logger.error("获取任务结果时发生异常, " + getJobSummary(jobInfo, jobSnapshot), e);
			// 获取任务执行结果时发生异常，有可能是网络问题，目标服务器假死等等。
			// 不希望直接把任务标记为失败，最终还是目标服务器情况为准。
			return true;
		}

	}

	/**
	 * 获取任务执行情况或者结果.
	 */
	protected JobExecutingResponse getExecuteResult(JobInfo jobInfo, JobSnapshot jobSnapshot) throws Exception {
		Request req = new Request();
		req.setJobDetailId(jobSnapshot.getId());
		req.setMethodFlag(MethodFlag.EXECUTING);
		req.setClassFullPath(jobInfo.getClassPath());
		String reqBody = JSON.toJSONString(req);

		String resBody = HttpClientUtils.post(jobSnapshot.getUrl(), reqBody, "application/json", "utf-8", TIME_OUT, TIME_OUT);

		JobExecutingResponse exeRes = null;
		try {
			exeRes = JSON.parseObject(resBody, JobExecutingResponse.class);
		} catch (JSONException e) {
			logger.error("获取任务执行结果 JSON 解析异常," + getJobSummary(jobInfo, jobSnapshot) + ",Response Body:{}", resBody);
			throw e;
		}
		return exeRes;
	}


	/**
	 * 处理任务完成的情况.
	 */
	protected void handleJobCompleted(JobInfo jobInfo, JobSnapshot jobSnapshot, JobResult jobResult) {
		if (jobResult.isSuccess()) {
			jobSucc(jobInfo, jobSnapshot, jobResult);
		} else {
			jobFail("执行时发生异常. 异常信息【" + jobResult.getResult() + "】", jobInfo, jobSnapshot, jobResult);
		}
		logger.info("任务执行完成，获取结果为JobSummary:{},JobResult:{}", new Object[]{getJobSummary(jobInfo, jobSnapshot)}, JSON.toJSONString(jobResult));
	}

	/**
	 * 在目标服务器,获取不到任务信息时.
	 */
	protected void handleJobUnknow(JobInfo jobInfo, JobSnapshot jobSnapshot) {
		jobFail("目标服务器没有这条任务执行的记录或结果", jobInfo, jobSnapshot, null);
	}

	/**
	 * 任务执行成功时，调用的方法
	 */
	protected void jobSucc(JobInfo jobInfo, JobSnapshot jobSnapshot, JobResult jobResult) {
		JobSnapshot update = new JobSnapshot();
		update.setId(jobSnapshot.getId());
		update.setStatus(JobSnapshotStatusEnum.COMPLETED);
		update.setResult(jobResult.getResult());
		update.setTimeConsume(jobResult.getTimeConsume());
		update.setDetail("任务已结束,并且执行成功. 结果【" + jobResult.getResult() + "】" + getNowTime() + "\n");
		update.setActualStartTime(jobResult.getActualStartTime());
		update.setActualFinishTime(jobResult.getActualFinishTime());

		jobSnapshotDAO.updateByIdAndConcatDetail(update);

		JobInfoTypeEnum type = jobInfo.getType();
		if (type.equals(JobInfoTypeEnum.ONCE)) {
			Result<Boolean> result = jobInfoService.deleteJobInfoById(jobInfo.getId());
			if (!result.isSuccess()) {
				logger.error("jobInfoService.deleteById fail! " + result.getErrorMsg());
			}
		}
	}

	/**
	 * 任务失败.
	 */
	protected void jobFail(String errorMessage, JobInfo jobInfo, JobSnapshot jobSnapshot, JobResult jobResult) {

		JobSnapshot update = new JobSnapshot();
		update.setId(jobSnapshot.getId());
		update.setResult(errorMessage);
		update.setStatus(JobSnapshotStatusEnum.ERROR);
		update.setDetail("任务失败：" + errorMessage + getNowTime() + "\n");

		if(jobResult != null) {
			update.setTimeConsume(jobResult.getTimeConsume());
			update.setActualStartTime(jobResult.getActualStartTime());
			update.setActualFinishTime(jobResult.getActualFinishTime());
		}

		jobSnapshotDAO.updateByIdAndConcatDetail(update);

		if (jobInfo != null) {
			JobInfoTypeEnum type = jobInfo.getType();
			if (type.equals(JobInfoTypeEnum.ONCE)) {
				Result<Boolean> result = jobInfoService.deleteJobInfoById(jobInfo.getId());
				if (!result.isSuccess()) {
					logger.error("jobInfoService.deleteById fail! " + result.getErrorMsg());
				}
			}
		}

		if (jobInfo != null && jobInfo.getOwnerPhone() != null) {
			smsService.sendAlertSms(jobInfo.getOwnerPhone(), jobInfo.getId(), jobInfo.getName(), jobSnapshot.getId(), errorMessage);
		}
	}


	/**
	 * 获取现在的时间 yyyy-MM-dd HH:mm:ss
	 */
	private String getNowTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date());
	}


	private String getJobSummary(JobInfo jobInfo, JobSnapshot jobSnapshot) {

		String jobInfoId, jobInfoName, jobInfoGroup, jobSnapshotId;
		jobInfoId = jobSnapshot == null ? "" : jobSnapshot.getJobInfoId().toString();  // 从 JobSnapshot 中获取是因为 JobInfo 可能为null
		jobInfoName = jobInfo == null ? "" : jobInfo.getName();
		jobInfoGroup = jobInfo == null ? "" : jobInfo.getGroup();
		jobSnapshotId = jobSnapshot == null ? "" : jobSnapshot.getId().toString();

		return String.format("JobSummary:[jobInfoId:%s, jobInfoName:%s, jobInfoGroup:%s, jobSnapshotId:%s]", new Object[]{jobInfoId, jobInfoName, jobInfoGroup, jobSnapshotId});
	}


}
