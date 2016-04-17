package org.codelab.app.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class WebConfiguration {

	@Bean
	public ObjectMapper jacksonObjectMapper() {
		ObjectMapper om = new ObjectMapper();
		om.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		return om;
	}
	
}
