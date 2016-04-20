package org.codelab.app.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.script.ScriptException;

import org.apache.commons.lang3.ArrayUtils;
import org.codelab.app.service.ConfigurationService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.MapFactoryBean;
import org.springframework.stereotype.Component;

@Component
public class BeanPostProcessor implements org.springframework.beans.factory.config.BeanPostProcessor {

	@Resource
	private ConfigurationService configurationService;

	@Value("classpath:*.groovy")
	private org.springframework.core.io.Resource[] scriptClasspathResources;
		
	@Value("file:${config.dir:${user.home}/config}/${config.script:*.groovy}")
	private org.springframework.core.io.Resource[] scriptFileResources;

	@PostConstruct
	public void init() throws ScriptException, IOException {
		org.springframework.core.io.Resource[] mergedResources = ArrayUtils.addAll(scriptClasspathResources, scriptFileResources);
		for (org.springframework.core.io.Resource scriptResource : mergedResources) {
			if (scriptResource.exists()) {
				try (InputStream is = scriptResource.getInputStream()) {
					configurationService.evaluateExpression(is);
				}
			}
		}
		
	}
	
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		System.out.printf("[before] %s -> %s%n", beanName, bean);
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		
		
		
		System.out.printf("[after] %s -> %s%n", beanName, bean);
		if ("map1".equals(beanName)) {
			try {
				Map<Object,Object> m = null;
				if (bean instanceof MapFactoryBean) {
					m = ((MapFactoryBean) bean).getObject();
				} else if (bean instanceof Map) {
					m = (Map<Object,Object>) bean;
				}
				if (m != null) {
					m.put("c", 99999);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.printf("[after] %s -> %s%n", beanName, bean);	
		}
		
		return bean;
	}

}
