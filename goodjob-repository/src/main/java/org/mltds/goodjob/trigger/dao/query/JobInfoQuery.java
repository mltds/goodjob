package org.mltds.goodjob.trigger.dao.query;

/**
 * @author sunyi
 */
public class JobInfoQuery {

	private String name;
	private String group;
	private String likeName; // 模糊查询 %likeName%
	private String likeGroup; // 模糊查询 %likeGroup%
	private Boolean isActivity;
	private Boolean isCheckFinish;
	private Integer offset = new Integer(0); // 与 rows 一起使用
	private Integer rows; // 与 offset 一起使用

	public JobInfoQuery() {
	}

	public JobInfoQuery(String name, String group) {
		this.name = name;
		this.group = group;
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

	public Boolean getIsActivity() {
		return isActivity;
	}

	public void setIsActivity(Boolean isActivity) {
		this.isActivity = isActivity;
	}

	public Boolean getIsCheckFinish() {
		return isCheckFinish;
	}

	public void setIsCheckFinish(Boolean isCheckFinish) {
		this.isCheckFinish = isCheckFinish;
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

	public String getLikeName() {
		return likeName;
	}

	public void setLikeName(String likeName) {
		this.likeName = likeName;
	}

	public String getLikeGroup() {
		return likeGroup;
	}

	public void setLikeGroup(String likeGroup) {
		this.likeGroup = likeGroup;
	}
}
