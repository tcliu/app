package org.codelab.app.spring.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-context.xml")
public class ExpressionTest {

	@Autowired
	private ConfigurableApplicationContext applicationContext;
	
	@Test
	public void resolveEmbeddedValue() {
		String v2 = applicationContext.getBeanFactory().resolveEmbeddedValue("#{systemProperties['ext.config.dir']}");
		System.out.println(v2);
		StandardEvaluationContext context = new StandardEvaluationContext();
		ExpressionParser parser = new SpelExpressionParser();
		context.setBeanResolver(new BeanFactoryResolver(applicationContext.getBeanFactory()));
		Expression expression = parser.parseExpression("#{systemProperties['ext.config.dir']}");
		Object value = expression.getValue(context);
		System.out.println(value);
	}
	
}
