package org.mltds.goodjob.trigger.test;

import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class ClientJobTest extends TerminableJob {

	@Override
	public String execute(String param) {
		System.out.println("param: " + param);

		try {
			TimeUnit.SECONDS.sleep(10L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return "success";

	}

}
