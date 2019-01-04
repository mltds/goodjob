package org.mltds.goodjob.trigger.service.impl;

import org.mltds.goodjob.trigger.service.SmsService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * @author sunyi
 *         Created on 15/11/10
 */
@Service
public class SmsServiceImpl implements SmsService {

	private Logger logger = LoggerFactory.getLogger(SmsServiceImpl.class);


	@Override
	public void sendAlertSms(String phones, Long jobInfoId, String jobInfoName, Long jobSnapshotId, String errorMessage) {

		if (StringUtils.isBlank(phones)) {
			return;
		}

		String[] phoneArray = phones.split(","); //半角逗号分隔

		for (String phone : phoneArray) {

			StringBuilder content = new StringBuilder();

			try {
				if (StringUtils.isBlank(phone)) {
					continue;
				}

				phone = phone.trim();

				Assert.notNull(jobInfoName);

				if (errorMessage.length() > 50) {
					errorMessage = errorMessage.substring(0, 50) + "...";
				}


				content.append("goodjob任务调度发生异常，JobInfoId:【");
				content.append(jobInfoId);
				content.append("】，任务名称:【");
				content.append(jobInfoName);
				content.append("】，JobSnapshotId:【");
				content.append(jobSnapshotId);
				content.append("】，错误信息:【");
				content.append(errorMessage);
				content.append("】");


				// 发送短信，因依赖公司内部相关实现，所以删除相关代码

			} catch (Exception e) {
				logger.warn("发送报警短信发生异常，短信内容：【" + content.toString() + "】", e);
			}
		}

	}

}
