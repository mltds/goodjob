package org.mltds.goodjob.trigger.web.controller;

import org.mltds.commons.lang.Result;
import org.mltds.goodjob.trigger.dao.JobInfoDAO;
import org.mltds.goodjob.common.dataobject.dataobject.JobInfo;
import org.mltds.goodjob.trigger.dao.query.JobInfoQuery;
import org.mltds.goodjob.trigger.service.JobInfoService;
import org.mltds.goodjob.trigger.utils.StringEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chen.jie
 */
@Controller
@RequestMapping("/jobinfo")
public class JobInfoManagementController {

	private Logger logger = LoggerFactory.getLogger(JobInfoManagementController.class);

	private static final int STATUS_SUCCESS = 0;

	private static final int STATUS_FAILURE = 1;

	@Resource
	private JobInfoService jobInfoService;

	@Autowired
	private JobInfoDAO jobInfoDAO;

	@InitBinder
	public void initBinder(WebDataBinder binder) throws Exception {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		CustomDateEditor dateEditor = new CustomDateEditor(df, true);
		binder.registerCustomEditor(Date.class, dateEditor);
		binder.registerCustomEditor(String.class, new StringEditor());
	}

	@RequestMapping("/view/{id}.htm")
	public String viewJobInfo(@PathVariable("id") long id, Model model) {
		Result<JobInfo> result = jobInfoService.selectJobInfoById(id);
		if (result.isSuccess()) {
			model.addAttribute("status", STATUS_SUCCESS);
			model.addAttribute("data", result.getData());
		} else {
			model.addAttribute("status", STATUS_FAILURE);
			model.addAttribute("errorMsg", result.getErrorMsg());
		}

		return "jobInfo/view";
	}

	@RequestMapping("/list.htm")
	public String selectList(@RequestParam(value = "name", required = false) String name,
	                         @RequestParam(value = "group", required = false) String group,
	                         @RequestParam(value = "page", required = false) Integer page,
	                         @RequestParam(value = "rows", required = false) Integer rows,
	                         Model model) {

		if (page == null) {
			page = 1;
		}
		if (rows == null) {
			rows = 20;
		}

		JobInfoQuery query = new JobInfoQuery();
		query.setLikeName(name);
		query.setLikeGroup(group);
		query.setOffset((page - 1) * rows); // 第 1 页，数据中是第 0 页
		query.setRows(rows);

		List<JobInfo> list = jobInfoDAO.findByParam(query);
		int count = jobInfoDAO.countByParam(query);

		Map<String, String> p = new HashMap<String, String>();
		p.put("name", name);
		p.put("group", group);
		model.addAttribute("p", p);
		model.addAttribute("page", page);
		model.addAttribute("rows", rows);
		model.addAttribute("data", list);

		int pages; // 总页数
		if(count == 0 ) {
			pages = 1;
		}else if (count % rows == 0) {
			pages = count / rows;
		}else {
			pages = count / rows + 1;
		}

		model.addAttribute("pages", pages);
		model.addAttribute("status", STATUS_SUCCESS);


		return "jobInfo/list";
	}

	@RequestMapping("/toadd.htm")
	public String toAddJobInfo() {
		return "jobInfo/add";
	}

	@RequestMapping(value = "/add.htm", method = RequestMethod.POST)
	public String addJobInfo(@ModelAttribute("data") JobInfo jobInfo, BindingResult br, ModelMap model) {
		if (jobInfo.isActivity() == null) {
			jobInfo.setActivity(false);
		}

		if (jobInfo.isCheckFinish() == null) {
			jobInfo.setCheckFinish(false);
		}

		Result<Long> result = jobInfoService.addJobInfo(jobInfo);
		if (result.isSuccess()) {
			Result<JobInfo> sr = jobInfoService.selectJobInfoById(jobInfo.getId());
			model.addAttribute("data", sr.getData());
			model.addAttribute("status", STATUS_SUCCESS);
		} else {
			model.addAttribute("status", STATUS_FAILURE);
			model.addAttribute("errorMsg", result.getErrorMsg());
			return "jobInfo/add";
		}
		return "/jobInfo/edit";
	}

	@RequestMapping("/toedit/{id}.htm")
	public String toUpdateJobInfo(@PathVariable("id") long id, Model model) {
		Result<JobInfo> result = jobInfoService.selectJobInfoById(id);
		if (result.isSuccess()) {
			model.addAttribute("data", result.getData());
		} else {
			model.addAttribute("status", STATUS_FAILURE);
			model.addAttribute("errorMsg", result.getErrorMsg());
		}

		return "jobInfo/edit";
	}

	@RequestMapping("/edit.htm")
	public String updateJobInfo(@ModelAttribute("data") JobInfo jobInfo, Model model) {

		// 如果前端不填写字符串，那么是 null， DAO 层不会更新
		// 没有在 DAO 层做是因为其他地方传 null 就是指不想更新这个字段
		// 理论上应该不区分 “” 和 null 的， 但没想到其他好的办法
		if (jobInfo.getParam() == null) {
			jobInfo.setParam("");
		}

		if (jobInfo.getDesc() == null) {
			jobInfo.setDesc("");
		}

		if (jobInfo.getOwnerPhone() == null) {
			jobInfo.setOwnerPhone("");
		}

		if (jobInfo.isActivity() == null) {
			jobInfo.setActivity(false);
		}

		if (jobInfo.isCheckFinish() == null) {
			jobInfo.setCheckFinish(false);
		}

		Result<Long> result = jobInfoService.updateJobInfo(jobInfo);
		if (result.isSuccess()) {
			Result<JobInfo> sr = jobInfoService.selectJobInfoById(jobInfo.getId());
			model.addAttribute("data", sr.getData());
			model.addAttribute("status", STATUS_SUCCESS);
		} else {
			model.addAttribute("status", STATUS_FAILURE);
			model.addAttribute("errorMsg", result.getErrorMsg());
		}

		return "jobInfo/edit";
	}

	@ResponseBody
	@RequestMapping("/delete/{id}")
	public Map<String, Object> deleteJobInfo(@PathVariable("id") long id) {
		Map<String, Object> mapResult = new HashMap<String, Object>();

		Result<Boolean> result = jobInfoService.deleteJobInfoById(id);
		if (result.isSuccess()) {
			mapResult.put("status", STATUS_SUCCESS);
		} else {
			mapResult.put("status", STATUS_FAILURE);
			mapResult.put("errorMsg", result.getErrorMsg());
		}

		return mapResult;
	}

	@ResponseBody
	@RequestMapping("/test")
	public Map<String, Object> testUrlsAndClassPath(@RequestParam("urls") String urls, @RequestParam("classPath") String classPath) {
		Map<String, Object> r = new HashMap<String, Object>();

		Result<Boolean> result = jobInfoService.testUrlsAndClassPath(urls, classPath);
		if (result.isSuccess()) {
			r.put("status", STATUS_SUCCESS);
			r.put("successMsg", "所有服务器都测试成功.");
		} else {
			r.put("status", STATUS_FAILURE);
			r.put("errorMsg", result.getErrorMsg());
		}

		return r;
	}

	@RequestMapping("/execute.htm")
	public String execute(@ModelAttribute("data") JobInfo jobInfo, Model model) {

		// 如果前端不填写字符串，那么是 null， DAO 层不会更新
		// 没有在 DAO 层做是因为其他地方传 null 就是指不想更新这个字段
		// 理论上应该不区分 “” 和 null 的， 但没想到其他好的办法
		if (jobInfo.getParam() == null) {
			jobInfo.setParam("");
		}

		if (jobInfo.getDesc() == null) {
			jobInfo.setDesc("");
		}

		if (jobInfo.getOwnerPhone() == null) {
			jobInfo.setOwnerPhone("");
		}

		if (jobInfo.isActivity() == null) {
			jobInfo.setActivity(false);
		}

		if (jobInfo.isCheckFinish() == null) {
			jobInfo.setCheckFinish(false);
		}

		Result<Long> updateResult = jobInfoService.updateJobInfo(jobInfo);
		if (!updateResult.isSuccess()) {
			model.addAttribute("status", STATUS_FAILURE);
			model.addAttribute("errorMsg", updateResult.getErrorMsg());
			return "jobInfo/edit";
		}


		Result<Void> result = jobInfoService.execute(jobInfo);

		if (result.isSuccess()) {
			model.addAttribute("status", STATUS_SUCCESS);
			model.addAttribute("successMsg", "提交成功，任务1秒钟之后将被执行");
		} else {
			model.addAttribute("status", STATUS_FAILURE);
			model.addAttribute("errorMsg", result.getErrorMsg());
		}

		return "jobInfo/edit";
	}

}
