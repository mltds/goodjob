package org.mltds.goodjob.trigger.dao.query;

import org.mltds.goodjob.common.dataobject.enums.JobSnapshotStatusEnum;

import java.util.Date;

/**
 * @author sunyi
 */
public class JobSnapshotQuery {

	private Long id;
	private Long jobInfoId;
	private String name;
	private String group;
	private JobSnapshotStatusEnum status;
	private Date actualFinishTimeGte;
	private Integer offset = new Integer(0); // 与 rows 一起使用
	private Integer rows; // 与 offset 一起使用

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getJobInfoId() {
		return jobInfoId;
	}

	public void setJobInfoId(Long jobInfoId) {
		this.jobInfoId = jobInfoId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public JobSnapshotStatusEnum getStatus() {
		return status;
	}

	public void setStatus(JobSnapshotStatusEnum status) {
		this.status = status;
	}

	public Date getActualFinishTimeGte() {
		return actualFinishTimeGte;
	}

	public void setActualFinishTimeGte(Date actualFinishTimeGte) {
		this.actualFinishTimeGte = actualFinishTimeGte;
	}

	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public Integer getRows() {
		return rows;
	}

	public void setRows(Integer rows) {
		this.rows = rows;
	}
}
