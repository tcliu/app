package org.codelab.app.web.listener;

import javax.servlet.ServletContextEvent;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

public class ContextLoaderListener extends org.springframework.web.context.ContextLoaderListener {

	@Override
	public void contextInitialized(ServletContextEvent event) {
		long stime = System.currentTimeMillis();
		super.contextInitialized(event);
		WebApplicationContext webappCtx = ContextLoader.getCurrentWebApplicationContext();
		System.out.printf("%s bean definitions loaded in %.3f second(s).%n", webappCtx.getBeanDefinitionCount(), 
				(System.currentTimeMillis() - stime) / 1000.0);
	}

}
