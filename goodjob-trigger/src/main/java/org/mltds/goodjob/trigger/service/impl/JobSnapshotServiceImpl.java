package org.mltds.goodjob.trigger.service.impl;

import com.alibaba.fastjson.JSON;
import org.mltds.commons.lang.Result;
import org.mltds.commons.util.HttpClientUtils;
import org.mltds.goodjob.worker.enums.MethodFlag;
import org.mltds.goodjob.worker.model.Request;
import org.mltds.goodjob.worker.model.JobStopResponse;
import org.mltds.goodjob.trigger.dao.JobInfoDAO;
import org.mltds.goodjob.trigger.dao.JobSnapshotDAO;
import org.mltds.goodjob.common.dataobject.dataobject.JobInfo;
import org.mltds.goodjob.common.dataobject.dataobject.JobSnapshot;
import org.mltds.goodjob.common.dataobject.enums.JobSnapshotStatusEnum;
import org.mltds.goodjob.trigger.service.JobSnapshotService;
import org.apache.http.conn.ConnectTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author chen.jie
 */
@Service("jobSnapshotServiceImpl")
public class JobSnapshotServiceImpl implements JobSnapshotService {

	private static final Logger LOG = LoggerFactory.getLogger(JobSnapshotServiceImpl.class);


	// 连接超时或响应超时. 总体超时时间为: TIME_OUT + TIME_OUT.
	private static final int TIME_OUT = 1000 * 5;

	@Autowired
	private JobInfoDAO jobInfoDAO;

	@Resource
	private JobSnapshotDAO jobSnapshotDAO;

	@Override
	public Result<List<JobSnapshot>> selectJobSnapshotList(JobSnapshot jobSnapshot) {
		Result<List<JobSnapshot>> result = new Result<List<JobSnapshot>>();

		try {
			List<JobSnapshot> jobSnapshotList = jobSnapshotDAO.selectJobSnapshotList(jobSnapshot);
			result.setSuccess(true);
			result.setData(jobSnapshotList);
		} catch (Exception e) {
			LOG.error("goodjob SysException: ", e);
			result.setSuccess(false);
			result.setErrorMsg("系统异常，请联系开发人员！");
		}

		return result;
	}

	@Override
	public Result<List<JobSnapshot>> selectListByNameAndGroupAndStatus(String name, String group, String status) {
		Result<List<JobSnapshot>> result = new Result<List<JobSnapshot>>();

		try {
			List<JobSnapshot> jobSnapshotList = jobSnapshotDAO.getListByNameAndGroupAndStatus(name, group, status);
			result.setSuccess(true);
			result.setData(jobSnapshotList);
		} catch (Exception e) {
			LOG.error("goodjob SysException: ", e);
			result.setSuccess(false);
			result.setErrorMsg("系统异常，请联系开发人员！");
		}

		return result;
	}

	@Override
	public Result<List<JobSnapshot>> selectListByNameAndGroupAndStatus(String name, String group, String status, int limit) {
		Result<List<JobSnapshot>> result = new Result<List<JobSnapshot>>();
		try {
			if (limit <= 0) {
				// 默认100条.
				limit = 100;
			}
			List<JobSnapshot> jobSnapshotList = jobSnapshotDAO.getListByNameAndGroupAndStatus(name, group, status, limit);
			result.setSuccess(true);
			result.setData(jobSnapshotList);
		} catch (Exception e) {
			LOG.error("goodjob SysException: ", e);
			result.setSuccess(false);
			result.setErrorMsg("系统异常，请联系开发人员！");
		}

		return result;
	}

	@Override
	public Result<JobSnapshot> selectJobSnapshotById(long id) {
		Result<JobSnapshot> result = new Result<JobSnapshot>();

		if (id <= 0L) {
			result.setSuccess(false);
			result.setErrorMsg("参数不合法！");
			return result;
		}

		try {
			JobSnapshot jobSnapshot = jobSnapshotDAO.findById(id);
			result.setSuccess(true);
			result.setData(jobSnapshot);
		} catch (Exception e) {
			LOG.error("goodjob SysException: ", e);
			result.setSuccess(false);
			result.setErrorMsg("系统异常，请联系开发人员！");
		}

		return result;
	}

	@Override
	/**
	 * 清理一个月前的数据
	 * 把事务去掉是因为，如果在运行这个方法，获取任务结果时 select id for update ，与这个一起执行会导致 MySQL 死锁
	 */
	public Result<Boolean> dataCleanOneMonthAgo() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, -1);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);

		Date createTime = c.getTime();

		jobSnapshotDAO.findAndInsertIntoHistoryBeforeCreateTime(createTime);
		jobSnapshotDAO.deleteBeforeCreateTime(createTime);

		Result<Boolean> r = new Result<Boolean>();
		r.setSuccess(true);
		r.setData(true);

		return r;
	}

	@Override
	/**
	 * 清理一周前的数据
	 * 把事务去掉是因为，如果在运行这个方法，获取任务结果时 select id for update ，与这个一起执行会导致 MySQL 死锁
	 */
	public Result<Boolean> dataCleanOneWeekAgo() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_YEAR, -7);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);

		Date createTime = c.getTime();

		jobSnapshotDAO.findAndInsertIntoHistoryBeforeCreateTime(createTime);
		jobSnapshotDAO.deleteBeforeCreateTime(createTime);

		Result<Boolean> r = new Result<Boolean>();
		r.setSuccess(true);
		r.setData(true);

		return r;
	}


	/**
	 * 执行停止任务操作和获取结果.
	 *
	 * @throws Exception
	 * @throws SocketTimeoutException
	 * @throws ConnectTimeoutException
	 * @author san.feng
	 */
	public JobStopResponse execStopAndGetResult(Long jobSnapshotId) {

		JobStopResponse stopResp = null;

		JobSnapshot jobSnapshot = jobSnapshotDAO.findById(jobSnapshotId);
		JobInfo jobInfo = jobInfoDAO.findById(jobSnapshot.getJobInfoId());

		if (!jobSnapshot.getStatus().equals(JobSnapshotStatusEnum.EXECUTING)) {
			stopResp = new JobStopResponse();
			stopResp.setStopNoticeSucc(false);
			stopResp.setErrorMsg("任务非执行状态, 不能停止!");
			return stopResp;
		}

		// 执行请求, 封装返回结果
		Request req = new Request();
		req.setJobDetailId(jobSnapshotId);
		req.setMethodFlag(MethodFlag.STOP);
		req.setClassFullPath(jobInfo.getClassPath());
		String reqBody = JSON.toJSONString(req);

		String resBody = null;
		try {
			resBody = HttpClientUtils.post(jobSnapshot.getUrl(), reqBody, "application/json", "utf-8", TIME_OUT, TIME_OUT);
		} catch (Exception e) {
			stopResp = new JobStopResponse();
			stopResp.setStopNoticeSucc(false);
			stopResp.setErrorMsg(e.getMessage());
			return stopResp;
		}

		if (resBody.contains("Unknown Http Request")) {
			stopResp = new JobStopResponse();
			stopResp.setStopNoticeSucc(false);
			stopResp.setErrorMsg("goodjob client not support stop operation, please update goodjob client version!");
		} else {
			stopResp = JSON.parseObject(resBody, JobStopResponse.class);
		}

		// 更新状态
		dealJobStopResponse(jobSnapshot, stopResp);

		return stopResp;
	}


	private void dealJobStopResponse(JobSnapshot jobSnapshot, JobStopResponse stopResp) {

		if (!stopResp.isStopNoticeSucc()) return;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		JobSnapshot update = new JobSnapshot();
		update.setId(jobSnapshot.getId());
		update.setDetail("停止任务通知成功. 时间:" + sdf.format(new Date()) + "\n");

		jobSnapshotDAO.updateByIdAndConcatDetail(update);
	}

}
