package org.mltds.goodjob.trigger.service.impl;

import org.mltds.commons.lang.Result;
import org.mltds.goodjob.trigger.dao.JobInfoHistoryDAO;
import org.mltds.goodjob.common.dataobject.dataobject.JobInfoHistory;
import org.mltds.goodjob.trigger.service.JobInfoHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author chen.jie
 */
@Service
public class JobInfoHistoryServiceImpl implements JobInfoHistoryService {

	private static final Logger LOG = LoggerFactory.getLogger(JobInfoHistoryServiceImpl.class);

	@Resource
	private JobInfoHistoryDAO jobInfoHistoryDAO;

	@Override
	public Result<List<JobInfoHistory>> selectListByNameAndGroup(
			String name, String group, int limit) {
		Result<List<JobInfoHistory>> result = new Result<List<JobInfoHistory>>();

		try {

			if (limit == 0) {
				limit = 100;
			}

			List<JobInfoHistory> jobInfoHistoryList = jobInfoHistoryDAO.getListByNameAndGroup(name, group, limit);
			result.setSuccess(true);
			result.setData(jobInfoHistoryList);
		} catch (Exception e) {
			LOG.error("goodjob SysException: ", e);
			result.setSuccess(false);
			result.setErrorMsg("系统异常，请联系开发人员！");
		}

		return result;
	}

	@Override
	public Result<JobInfoHistory> selectJobInfoHistoryById(long id) {
		Result<JobInfoHistory> result = new Result<JobInfoHistory>();

		if (id <= 0L) {
			result.setSuccess(false);
			result.setErrorMsg("参数不合法！");
			return result;
		}

		try {
			JobInfoHistory jobInfoHistory = jobInfoHistoryDAO.findJobInfoHistoryById(id);
			result.setSuccess(true);
			result.setData(jobInfoHistory);
		} catch (Exception e) {
			LOG.error("goodjob SysException: ", e);
			result.setSuccess(false);
			result.setErrorMsg("系统异常，请联系开发人员！");
		}

		return result;
	}

}
