package org.codelab.app.service.impl;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;
import java.util.WeakHashMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.codelab.app.service.ConfigurationService;
import org.mvel2.MVEL;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class ConfigurationServiceImpl implements ConfigurationService {

	@Autowired
	private ConfigurableApplicationContext applicationContext;
	
	private final Map<String,Serializable> cachedExpressions = new WeakHashMap<>();
	
	private final Map<String,Object> cachedBeans = new WeakHashMap<>();
	
	@PostConstruct
	public void init() {
		cachedBeans.put("ctx", applicationContext);
		cachedBeans.put("beans", cachedBeans);
	}
	
	@PreDestroy
	public void destroy() {
		cachedExpressions.clear();
		cachedBeans.clear();
	}
	
	/**
	 * Gets all bean names registered in the application context
	 * @return the array of bean names
	 */
	@Override
	public String[] getBeanNames() {
		return applicationContext.getBeanDefinitionNames();
	}

	/**
	 * Gets a Spring bean
	 * @param name the bean name
	 * @return the bean
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getBean(String name) {
		return (T) applicationContext.getBean(name);
	}

	/**
	 * Evaluates an expression on a Spring bean
	 * @param expression the expression
 	 * @param allowAssignment{@code true} to allow assignment
	 * @return the evaluated value
	 */
	@Override
	public Object evaluateBeanValue(String expression, boolean allowAssignment) {
		if (expression != null && !expression.isEmpty()) {
			Serializable expr = cachedExpressions.get(expression);
			if (expr == null) {
				cachedExpressions.put(expression, expr = MVEL.compileExpression(expression));
			}
			int dotIdx = expression.indexOf("."), eqIdx = expression.indexOf("=");
			String beanName = dotIdx != -1 || eqIdx != -1 ? expression.substring(0, Math.max(dotIdx, eqIdx)) : expression;
			if (beanName != null) {
				Object bean = cachedBeans.get(beanName);
				if (bean == null) {
					bean = getBeanQuietly(beanName);
					if (bean != null) {
						cachedBeans.put(beanName, bean);
					}
				}
			}
			return MVEL.executeExpression(expr, cachedBeans);
		}
		return null;
	}

	/**
	 * Refreshes the application context
	 */
	@Override
	public void refreshApplicationContext() {
		applicationContext.refresh();
		destroy();
		init();
	}
	
	@Override
	public Map<Object,Object> getSystemProperties() {
		return new TreeMap<Object,Object>(System.getProperties());
	}

	private <T> T getBeanQuietly(String beanName) {
		try {
			return getBean(beanName);
		} catch (NoSuchBeanDefinitionException e) {
			return null;
		}
	}
}
