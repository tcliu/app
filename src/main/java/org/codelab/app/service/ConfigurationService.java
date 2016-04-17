package org.codelab.app.service;

import java.util.Map;

public interface ConfigurationService {

	String[] getBeanNames();

	<T> T getBean(String name);

	Object evaluateBeanValue(String expression, boolean allowAssignment);

	void refreshApplicationContext();
	
	Map<Object,Object> getSystemProperties();
}
