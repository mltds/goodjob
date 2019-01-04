package org.mltds.goodjob.trigger.web.controller;

import org.mltds.commons.lang.Result;
import org.mltds.goodjob.common.dataobject.dataobject.JobInfoHistory;
import org.mltds.goodjob.trigger.service.JobInfoHistoryService;
import org.mltds.goodjob.trigger.utils.StringEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
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
@RequestMapping("/jobinfohistory")
public class JobInfoHistoryManagementController {

	private static final int STATUS_SUCCESS = 0;

	private static final int STATUS_FAILURE = 1;

	@Resource
	private JobInfoHistoryService jobInfoHistoryService;
	
	@InitBinder
	public void initBinder(WebDataBinder binder) throws Exception {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		CustomDateEditor dateEditor = new CustomDateEditor(df, true);
		binder.registerCustomEditor(Date.class, dateEditor);
		binder.registerCustomEditor(String.class, new StringEditor());
	}

	@RequestMapping("/view/{id}.htm")
	public String viewJobSnapshot(@PathVariable("id") long id, Model model) {
		Result<JobInfoHistory> result = jobInfoHistoryService.selectJobInfoHistoryById(id);
		if (result.isSuccess()) {
			model.addAttribute("status", STATUS_SUCCESS);
			model.addAttribute("data", result.getData());
		} else {
			model.addAttribute("status", STATUS_FAILURE);
			model.addAttribute("errorMsg", result.getErrorMsg());
		}

		return "jobInfoHistory/view";
	}

	@RequestMapping("/list.htm")
	public String selectList(@RequestParam(value="name",required=false)String name, 
			@RequestParam(value="group",required=false)String group, Model model) {

		int limit = 100; //默认最多一百条

		Result<List<JobInfoHistory>> result = jobInfoHistoryService.selectListByNameAndGroup(name, group, limit);
		
		Map<String,String> p = new HashMap<String,String>();
		p.put("name", name);
		p.put("group", group);
		model.addAttribute("p", p);
		
		if (result.isSuccess()) {
			model.addAttribute("status", STATUS_SUCCESS);
			model.addAttribute("data", result.getData());
		} else {
			model.addAttribute("status", STATUS_FAILURE);
			model.addAttribute("errorMsg", result.getErrorMsg());
		}

		return "jobInfoHistory/list";
	}
}