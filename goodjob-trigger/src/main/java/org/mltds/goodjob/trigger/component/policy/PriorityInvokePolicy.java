package org.mltds.goodjob.trigger.component.policy;

import java.util.List;


public class PriorityInvokePolicy implements InvokePolicy {

	private final List<String> urlList;
	private final int retryMaxTimes ;
	

	private int urlListIndexNow = 0;

	private int retryTimes = 0;
	private String currentUrl;

	public PriorityInvokePolicy(List<String> urlList) {
		this(urlList,3);
	}

	public PriorityInvokePolicy(List<String> urlList, int retryMaxTimes) {
		this.urlList = urlList;
		this.retryMaxTimes = retryMaxTimes;
	}

	@Override
	public String getNextUrl() {
		if(currentUrl != null && retryTimes < retryMaxTimes){
			// 获取过链接, 并且没超出最大重试次数.
			// 重试次数+1, 并且返回这个链接. 
			retryTimes++;
			return currentUrl;
		}else{
			// 获取新的链接.
			if (urlListIndexNow < urlList.size()) {
				currentUrl = urlList.get(urlListIndexNow++);
				return currentUrl;
			} else {
				return null;
			}
		}
	}
}