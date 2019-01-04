package org.mltds.goodjob.trigger.utils;

import org.quartz.JobKey;

/**
 * @author sunyi
 *         Created on 15/11/7
 */
public class TestUtils {

	public static void main(String args[]) {

		JobKey k1 = JobKey.jobKey("n", "g");
		JobKey k2 = JobKey.jobKey("n", "g");

		System.out.println(k1.equals(k2));
		System.out.println(k1.hashCode() == k2.hashCode());

	}
}
