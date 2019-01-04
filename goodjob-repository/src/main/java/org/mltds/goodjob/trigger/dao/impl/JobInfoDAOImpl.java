package org.mltds.goodjob.trigger.dao.impl;

import org.mltds.goodjob.trigger.dao.JobInfoDAO;
import org.mltds.goodjob.common.dataobject.dataobject.JobInfo;
import org.mltds.goodjob.trigger.dao.query.JobInfoQuery;
import org.apache.commons.collections.CollectionUtils;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;

@Repository
public class JobInfoDAOImpl extends SqlSessionDaoSupport implements JobInfoDAO {

	@Override
	public List<JobInfo> findByParam(JobInfoQuery jobInfoQuery) {
		return getSqlSession().selectList("JobInfo.findByParam", jobInfoQuery);
	}

	@Override
	public int countByParam(JobInfoQuery jobInfoQuery) {
		return (Integer) getSqlSession().selectOne("JobInfo.countByParam", jobInfoQuery);
	}

	public JobInfo findByNameAndGroup(String name, String group) {

		Assert.hasText(name);
		Assert.hasText(group);

		List<JobInfo> jobInfoList = findByParam(new JobInfoQuery(name, group));
		return CollectionUtils.isEmpty(jobInfoList) ? null : jobInfoList.get(0);
	}

	@Override
	public int insert(JobInfo jobInfo) {
		jobInfo.setCreateTime(new Date());
		jobInfo.setModifyTime(new Date());
		return getSqlSession().insert("JobInfo.insert", jobInfo);
	}

	@Override
	public int updateById(JobInfo jobInfo) {
		jobInfo.setModifyTime(new Date());
		return getSqlSession().update("JobInfo.updateById", jobInfo);
	}

	@Override
	public int deleteById(long id) {
		return getSqlSession().delete("JobInfo.deleteById", id);
	}

	@Override
	public JobInfo findById(long id) {
		return (JobInfo) getSqlSession().selectOne("JobInfo.findById", id);
	}

	@Override
	public JobInfo findByIdForUpdate(long id) {
		return (JobInfo) getSqlSession().selectOne("JobInfo.findByIdForUpdate", id);
	}

}
