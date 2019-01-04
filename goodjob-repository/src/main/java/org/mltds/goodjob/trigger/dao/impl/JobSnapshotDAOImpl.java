package org.mltds.goodjob.trigger.dao.impl;

import org.mltds.goodjob.common.dataobject.JobSnapshot;
import org.mltds.goodjob.trigger.dao.JobSnapshotDAO;
import org.mltds.goodjob.trigger.dao.query.JobSnapshotQuery;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class JobSnapshotDAOImpl extends SqlSessionDaoSupport implements JobSnapshotDAO {

	@Override
	public JobSnapshot findById(Long id) {
		return (JobSnapshot) getSqlSession().selectOne("JobSnapshot.findById", id);
	}

	@Override
	public JobSnapshot findByIdForUpdate(Long id) {
		return (JobSnapshot) getSqlSession().selectOne("JobSnapshot.findByIdForUpdate", id);
	}

	@Override
	public void insert(JobSnapshot jobSnapshot) {
		jobSnapshot.setCreateTime(new Date());
		jobSnapshot.setModifyTime(new Date());
		getSqlSession().insert("JobSnapshot.insert", jobSnapshot);
	}

	@Override
	public int updateById(JobSnapshot jobSnapshot) {
		if (jobSnapshot == null) {
			return 0;
		}

		if (jobSnapshot.getId() == null) {
			throw new RuntimeException("id 不能为空");
		}

		if (jobSnapshot.getModifyTime() == null) {
			jobSnapshot.setModifyTime(new Date());
		}

		return getSqlSession().update("JobSnapshot.updateById", jobSnapshot);
	}

	@Override
	public int updateByIdAndConcatDetail(JobSnapshot jobSnapshot) {
		if (jobSnapshot == null) {
			return 0;
		}

		if (jobSnapshot.getId() == null) {
			throw new RuntimeException("id 不能为空");
		}

		if (jobSnapshot.getModifyTime() == null) {
			jobSnapshot.setModifyTime(new Date());
		}

		return getSqlSession().update("JobSnapshot.updateByIdAndConcatDetail", jobSnapshot);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<JobSnapshot> findExecutingList() {
		return getSqlSession().selectList("JobSnapshot.findExecutingList");
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<JobSnapshot> selectJobSnapshotList(JobSnapshot jobSnapshot) {
		return getSqlSession().selectList("JobSnapshot.selectJobSnapshotList", jobSnapshot);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<JobSnapshot> getListByNameAndGroupAndStatus(String name, String group, String status) {
		Map<String, String> map = new HashMap<String, String>(3);
		map.put("name", name);
		map.put("group", group);
		map.put("status", status);
		return getSqlSession().selectList("JobSnapshot.getListByNameAndGroupAndStatus", map);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<JobSnapshot> getListByNameAndGroupAndStatus(String name, String group, String status, Integer limit) {
		Map<String, Object> map = new HashMap<String, Object>(3);
		map.put("name", name);
		map.put("group", group);
		map.put("status", status);
		map.put("limit", limit);
		return getSqlSession().selectList("JobSnapshot.getListByNameAndGroupAndStatusLimit", map);

	}

	@Override
	public void findAndInsertIntoHistoryBeforeCreateTime(Date createTime) {
		getSqlSession().insert("JobSnapshot.findAndInsertIntoHistoryBeforeCreateTime", createTime);
	}

	@Override
	public void deleteBeforeCreateTime(Date createTime) {
		getSqlSession().delete("JobSnapshot.deleteBeforeCreateTime", createTime);
	}


	@Override
	public List<JobSnapshot> findByParam(JobSnapshotQuery param) {
		return getSqlSession().selectList("JobSnapshot.findByParam", param);
	}
}
