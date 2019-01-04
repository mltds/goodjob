package org.mltds.goodjob.trigger.service;

/**
 * @author sunyi
 *         Created on 15/11/10
 */
public interface SmsService {


	/**
	 * 发送报警短信
	 *
	 * @param phones 支持多个手机号，用半角逗号分割
	 * @param jobInfoId
	 * @param jobInfoName
	 * @param errorMessage
	 */
	void sendAlertSms(String phones, Long jobInfoId, String jobInfoName, Long jobSnapshotId, String errorMessage);


}
