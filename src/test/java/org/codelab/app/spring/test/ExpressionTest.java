package org.codelab.app.spring.test;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mvel2.MVEL;
import org.mvel2.integration.VariableResolver;
import org.mvel2.integration.impl.CachingMapVariableResolverFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-context.xml")
public class ExpressionTest {

	@Autowired
	private ConfigurableApplicationContext applicationContext;
	
	@Test
	public void resolveEmbeddedValue() {
		String v2 = applicationContext.getBeanFactory().resolveEmbeddedValue("${config.dir}");
		System.out.println(v2);
	}
	
	@Test
	public void getMessageSource() {
		String v2 = applicationContext.getBeanFactory().resolveEmbeddedValue("${welcome}");
		System.out.println(v2);
	}
	
	@Test
	public void testGroovy() throws ScriptException {
		ScriptEngineManager sem = new ScriptEngineManager();
		ScriptEngine engine = sem.getEngineByName("groovy");
		CompiledScript script = ((Compilable) engine).compile("map1.a=1;map1.b=2;map1.d=[1,2,3,4,5];");
		Bindings bindings = new SimpleBindings() {

			@Override
			public boolean containsKey(Object key) {
				return super.containsKey(key) || applicationContext.containsBean(key.toString());
			}

			@Override
			public Object get(Object key) {
				Object o = super.get(key);
				if (o == null && applicationContext.containsBean(key.toString())) {
					o = applicationContext.getBean(key.toString());
				}
				return o;
			}
			
			
		};
		bindings.put("ctx", applicationContext);
		Object o = script.eval(bindings);
		System.out.println(o);
	}
	
	@Test
	public void testMvel() {
		Map<String,Object> m = new HashMap<>();
		Map<String,Object> m2 = new HashMap<>();
		m.put("a", m2);
		m2.put("b", 3);
		
		//((Map<String,Object>) vars.get("a")).put("b", 2);
		Serializable expression = MVEL.compileExpression("a.c=3;a");
		Object result = MVEL.executeExpression(expression, new CachingMapVariableResolverFactory(null) {

			@Override
			public VariableResolver getVariableResolver(String name) {
				// TODO Auto-generated method stub
				return super.getVariableResolver(name);
			}

			@Override
			public boolean isResolveable(String name) {
				return m.containsKey(name);
			}

			@Override
			public boolean isTarget(String name) {
				// TODO Auto-generated method stub
				return super.isTarget(name);
			}
		
		});
		System.out.println(result);
	}
}
