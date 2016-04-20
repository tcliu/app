package org.codelab.app.beans;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Component;

@Component
public class MyBean {

	@Resource(name = "map1")
	private Map<String,Object> map1;
	
	private Object value;
	
	@PostConstruct
	public void init() {
		value = map1.get("a");
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}
