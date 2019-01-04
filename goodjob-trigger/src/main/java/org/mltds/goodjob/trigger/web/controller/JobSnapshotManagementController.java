package org.mltds.goodjob.trigger.web.controller;

import com.alibaba.fastjson.JSON;
import org.mltds.commons.lang.Result;
import org.mltds.goodjob.worker.model.JobStopResponse;
import org.mltds.goodjob.common.dataobject.dataobject.JobSnapshot;
import org.mltds.goodjob.trigger.service.JobSnapshotService;
import org.mltds.goodjob.trigger.utils.StringEditor;
import org.apache.http.conn.ConnectTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author chen.jie
 * 
 */
@Controller
@RequestMapping("/jobsnapshot")
public class JobSnapshotManagementController {

	private Logger logger = LoggerFactory.getLogger(JobSnapshotManagementController.class);

	private static final int STATUS_SUCCESS = 0;

	private static final int STATUS_FAILURE = 1;

	@Resource
	private JobSnapshotService jobSnapshotService;

	@InitBinder
	public void initBinder(WebDataBinder binder) throws Exception {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		CustomDateEditor dateEditor = new CustomDateEditor(df, true);
		binder.registerCustomEditor(Date.class, dateEditor);
		binder.registerCustomEditor(String.class, new StringEditor());
	}

	@RequestMapping("/view/{id}.htm")
	public String viewJobSnapshot(@PathVariable("id") long id, Model model) {
		Result<JobSnapshot> result = jobSnapshotService.selectJobSnapshotById(id);
		if (result.isSuccess()) {
			model.addAttribute("status", STATUS_SUCCESS);
			model.addAttribute("data", result.getData());
		} else {
			model.addAttribute("status", STATUS_FAILURE);
			model.addAttribute("errorMsg", result.getErrorMsg());
		}

		return "jobSnapshot/view";
	}

	@RequestMapping("/list.htm")
	public String selectList(@RequestParam(value = "name", required = false) String name, @RequestParam(value = "group", required = false) String group,
			@RequestParam(value = "status", required = false) String status, Model model) {

		// 默认只展现前100条.
		Result<List<JobSnapshot>> result = jobSnapshotService.selectListByNameAndGroupAndStatus(name, group, status, 100);

		Map<String, String> p = new HashMap<String, String>();
		p.put("name", name);
		p.put("group", group);
		p.put("status", status);
		model.addAttribute("p", p);

		if (result.isSuccess()) {
			model.addAttribute("status", STATUS_SUCCESS);
			model.addAttribute("data", result.getData());
		} else {
			model.addAttribute("status", STATUS_FAILURE);
			model.addAttribute("errorMsg", result.getErrorMsg());
		}

		return "jobSnapshot/list";
	}

	@ResponseBody
	@RequestMapping("/clean/onemonth")
	public Map<String, Object> cleanOneMonth() {
		Map<String, Object> r = new HashMap<String, Object>();

		Result<Boolean> result = null;
		try {
			// 因为这里使用了事务, 所以service 出错会将异常抛出.
			result = jobSnapshotService.dataCleanOneMonthAgo();
		} catch (Exception e) {
			logger.error("", e);
		}

		if (result != null && result.isSuccess()) {
			r.put("status", STATUS_SUCCESS);
		} else {
			r.put("status", STATUS_FAILURE);
			r.put("errorMsg", "系统异常，请联系开发人员！");
		}
		return r;
	}

	@ResponseBody
	@RequestMapping("/clean/oneweek")
	public Map<String, Object> cleanOneWeek() {
		Map<String, Object> r = new HashMap<String, Object>();

		Result<Boolean> result = null;
		try {
			// 因为这里使用了事务, 所以service 出错会将异常抛出.
			result = jobSnapshotService.dataCleanOneWeekAgo();
		} catch (Exception e) {
			logger.error("", e);
		}

		if (result != null && result.isSuccess()) {
			r.put("status", STATUS_SUCCESS);
		} else {
			r.put("status", STATUS_FAILURE);
			r.put("errorMsg", "系统异常，请联系开发人员！");
		}
		return r;
	}
	
	
	
	/**
	 * san.feng
	 * 
	 * 人不好嘴不甜长的磕碜还没钱
	 * @throws Exception 
	 * @throws SocketTimeoutException 
	 * @throws ConnectTimeoutException 
	 * 
	 */
	@ResponseBody
	@RequestMapping("/stop/{id}")
	public Map<String, Object> stopJobSnapshort(@PathVariable("id") long id) throws ConnectTimeoutException, SocketTimeoutException, Exception {
		Map<String, Object> mapResult = new HashMap<String, Object>();

		logger.info("execute stop jobsnapshort >>>, jobSnapshortId=" + id);
		
		JobStopResponse stopResp = jobSnapshotService.execStopAndGetResult(id);
		
		logger.info("execute stop jobsnapshort >>>, jobSnapshortId=" + id + ", RESULT=" + JSON.toJSON(stopResp));
		
		if (stopResp.isStopNoticeSucc()) {
			mapResult.put("status", STATUS_SUCCESS);
			mapResult.put("stopDetail", stopResp.getStopDetail());
		} else {
			mapResult.put("status", STATUS_FAILURE);
			mapResult.put("errorMsg", stopResp.getErrorMsg());
		}
		
		return mapResult;
	}
	
}
