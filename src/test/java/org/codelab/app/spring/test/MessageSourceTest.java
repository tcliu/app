package org.codelab.app.spring.test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-context.xml")
public class MessageSourceTest {

	@Resource
	private MessageSource messageSource;
	
	@Test
	public void getMessage() {
		String s = messageSource.getMessage("welcome", null, null);
		System.out.println(s);
	}
	
	@Test
	public void reloadMessage() throws NoSuchMessageException, InterruptedException, ExecutionException {
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		ScheduledFuture<?> f1 = executor.scheduleWithFixedDelay(() -> {
			String s = messageSource.getMessage("welcome", null, null);
			System.out.println(s);
		}, 0, 1, TimeUnit.SECONDS);
		ScheduledFuture<?> f2 = executor.schedule(() -> {
			f1.cancel(true);
		}, 30, TimeUnit.SECONDS);
		f1.get();
		f2.get();
		executor.shutdown();
	}
	
}
