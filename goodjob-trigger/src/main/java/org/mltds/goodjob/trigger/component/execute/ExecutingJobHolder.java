package org.mltds.goodjob.trigger.component.execute;

import java.util.concurrent.LinkedBlockingDeque;

public class ExecutingJobHolder {

	private static final LinkedBlockingDeque<Long> executingJob = new LinkedBlockingDeque<Long>();

	public static final boolean offerLastExecutingJob(Long jobSnapshotId) {
		if (executingJob.contains(jobSnapshotId)) {
			return true; // 已经有了的话，为了队列长度考虑，不再向队列内添加，视为成功。
		}
		return executingJob.offerLast(jobSnapshotId);
	}

	public static final Long takeFirstExecutingJob() throws InterruptedException {
		return executingJob.takeFirst();
	}

}