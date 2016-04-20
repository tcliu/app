package org.codelab.app.service.test;

import javax.annotation.Resource;
import javax.script.ScriptException;

import org.codelab.app.service.ConfigurationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-context.xml")
public class ConfigurationServiceTest {

	@Resource
	private ConfigurationService configurationService;
	
	@Test
	public void evaluateExpression() throws ScriptException {
		Object o;
		String expression;
		o = configurationService.evaluateExpression(expression = "1+1");
		System.out.printf("%s = %s%n", expression, o);
		o = configurationService.evaluateExpression(expression = "map1");
		System.out.printf("%s = %s%n", expression, o);
	}
	
}
