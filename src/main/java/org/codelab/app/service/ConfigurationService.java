package org.codelab.app.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.script.ScriptException;

public interface ConfigurationService {

	String[] getBeanNames();

	<T> T getBean(String name);

	Object evaluateExpression(InputStream expressionInputStream) throws ScriptException, IOException;
	
	Object evaluateExpression(String expression) throws ScriptException;
	
	void refreshApplicationContext();
	
	Map<Object,Object> getSystemProperties();
	
	void setSystemProperty(String key, String value);
}
