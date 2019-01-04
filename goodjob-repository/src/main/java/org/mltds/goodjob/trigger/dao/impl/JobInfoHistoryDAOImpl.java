package org.mltds.goodjob.trigger.dao.impl;

import org.mltds.goodjob.trigger.dao.JobInfoHistoryDAO;
import org.mltds.goodjob.common.dataobject.dataobject.JobInfoHistory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class JobInfoHistoryDAOImpl extends SqlSessionDaoSupport implements JobInfoHistoryDAO {

	@Override
	public int insertJobInfoHistory(JobInfoHistory jobInfoHistory) {
		if(jobInfoHistory.getModifyTime() == null){
			jobInfoHistory.setModifyTime(new Date());
		}
		return getSqlSession().insert("JobInfoHistory.insert", jobInfoHistory);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<JobInfoHistory> getListByNameAndGroup(String name, String group, int limit) {
		Map<String, Object> map = new HashMap<String, Object>(2);
		map.put("name", name);
		map.put("group", group);
		map.put("limit", limit);
		return getSqlSession().selectList("JobInfoHistory.getListByNameAndGroup", map);
	}

	@Override
	public JobInfoHistory findJobInfoHistoryById(Long id) {
		return (JobInfoHistory) getSqlSession().selectOne("JobInfoHistory.findJobInfoHistoryById", id);
	}


}
