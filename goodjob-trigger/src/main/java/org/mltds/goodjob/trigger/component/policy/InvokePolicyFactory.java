package org.mltds.goodjob.trigger.component.policy;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.mltds.goodjob.common.dataobject.enums.JobInfoInvokePolicyEnum;

@Component
public class InvokePolicyFactory {
	
	@Value("${retry.max.times}")
	private int retryMaxTimes;

	public InvokePolicy getJobExecutePolicy(JobInfoInvokePolicyEnum policy, List<String> urlList) {
		if (policy == null) {
			throw new IllegalArgumentException("The policy must not be null!");
		}

		if (CollectionUtils.isEmpty(urlList)) {
			throw new IllegalArgumentException("The urlList must not be empty!");
		}

		switch (policy) {
		case PRIORITY:
			return new PriorityInvokePolicy(urlList,retryMaxTimes);
		case RANDOM:
			return new RandomInvokePolicy(urlList,retryMaxTimes);
		}
		throw new RuntimeException("Unexpected invoke policy!");
	}
	
}
