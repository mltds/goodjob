package org.mltds.goodjob.trigger.component.execute;

import org.mltds.goodjob.trigger.component.JobExecutingComponent;
import org.mltds.goodjob.trigger.component.Launcher;
import org.mltds.goodjob.trigger.component.SpringApplicationContextAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.TimeUnit;

/**
 * 获取任务结果线程
 *
 * @author sunyi
 */
public class GetJobResultThread extends Thread {

	private Logger logger = LoggerFactory.getLogger(GetJobResultThread.class);

	@Override
	public void run() {

		logger.info(this.getClass().getSimpleName() + " is start");

		int runCounter = 0;
		int runCounterLogPoint = 600; // 每次大约1秒，10分钟左右刷一次。

		ApplicationContext ac = SpringApplicationContextAware.getApplicationContext();

		/******* init *************/
		while (!Launcher.shutdownNow) {
			try {

				Long jobSnapshotId = ExecutingJobHolder.takeFirstExecutingJob();

				JobExecutingComponent jobExecutingComponent = ac.getBean(JobExecutingComponent.class);
				boolean executing = jobExecutingComponent.handleExecuting(jobSnapshotId);

				if (executing) {
					ExecutingJobHolder.offerLastExecutingJob(jobSnapshotId);
				}

				runCounter++;
				if (runCounter == runCounterLogPoint) {
					runCounter = 0;
					logger.info(this.getClass().getSimpleName() + " is running");
				}
				TimeUnit.SECONDS.sleep(1L);
			} catch (Throwable e) {
				logger.error("处理执行中任务异常", e);
			}
		}

		logger.info(this.getClass().getSimpleName() + " is shutdown");
	}

}
