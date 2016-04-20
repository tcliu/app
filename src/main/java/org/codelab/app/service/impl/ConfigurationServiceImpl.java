package org.codelab.app.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.WeakHashMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import org.apache.commons.io.IOUtils;
import org.codelab.app.service.ConfigurationService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class ConfigurationServiceImpl implements ConfigurationService, ApplicationContextAware {

	@Resource
	private ConfigurableApplicationContext applicationContext;
	
	private ScriptEngineManager scriptEngineManager;
	
	private Bindings scriptBindings;

	private final Map<String,ScriptEngine> cachedScriptEngines = new HashMap<>();
	
	private final Map<String,CompiledScript> cachedScripts = new WeakHashMap<>();

	@PostConstruct
	public void init() {
		scriptEngineManager = new ScriptEngineManager();
		scriptBindings = new SimpleBindings() {
			
			@Override
			public boolean containsKey(Object key) {
				return super.containsKey(key) || applicationContext.containsBean(key.toString());
			}

			@Override
			public Object get(Object key) {
				Object o = super.get(key);
				if (o == null && applicationContext.containsBeanDefinition(key.toString())) {
					o = applicationContext.getBean(key.toString());
				}
				return o;
			}
		};
		scriptBindings.put("ctx", applicationContext);
	}
	
	@PreDestroy
	public void destroy() {
		cachedScriptEngines.clear();
		cachedScripts.clear();
		scriptEngineManager = null;
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
	 * @param expressionInputStream the input stream for the expression
	 * @return the evaluated value
	 * @throws ScriptException 
	 * @throws IOException 
	 */
	@Override
	public Object evaluateExpression(InputStream expressionInputStream) throws ScriptException, IOException {
		String expression = IOUtils.toString(expressionInputStream);
		return evaluateExpression(expression);
	}
	
	/**
	 * Evaluates an expression on a Spring bean
	 * @param expression the expression
	 * @return the evaluated value
	 * @throws ScriptException 
	 */
	@Override
	public Object evaluateExpression(String expression) throws ScriptException {
		return evaluateExpression(expression, "groovy");
	}

	/**
	 * Evaluates an expression
	 * @param expression the expression
	 * @param beanFactory the bean factory
	 * @return the evaluated value
	 * @throws ScriptException 
	 */
	private Object evaluateExpression(String expression, String engineName) throws ScriptException {
		if (expression != null && !expression.isEmpty()) {
			ScriptEngine scriptEngine = cachedScriptEngines.get(engineName);
			if (scriptEngine == null) {
				cachedScriptEngines.put(engineName, scriptEngine = scriptEngineManager.getEngineByName(engineName));
			}
			if (scriptEngine instanceof Compilable) {
				CompiledScript compiledScript = cachedScripts.get(expression);
				if (compiledScript == null && scriptEngine instanceof Compilable) {
					cachedScripts.put(expression, compiledScript = ((Compilable) scriptEngine).compile(expression));
				}
				return compiledScript.eval(scriptBindings);
			} else {
				return scriptEngine.eval(expression);
			}
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
	
	@Override
	public void setSystemProperty(String key, String value) {
		System.setProperty(key, value);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = (ConfigurableApplicationContext) applicationContext;
	}

}
