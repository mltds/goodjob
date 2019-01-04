package org.mltds.goodjob.trigger.component;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class SpringApplicationContextAware implements ApplicationContextAware {

	private static ApplicationContext springAc;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		SpringApplicationContextAware.springAc = applicationContext;
	}

	public static ApplicationContext getApplicationContext() {
		while (!Launcher.shutdownNow) {
			if (springAc == null) {
				try {
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException e) {
					break;
				}
			}else {
				break;
			}
		}
		return springAc;
	}
}
