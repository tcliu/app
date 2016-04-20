package org.codelab.app.web.controllers;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.script.ScriptException;
import javax.servlet.http.HttpServletRequest;

import org.codelab.app.service.ConfigurationService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

@RestController
@RequestMapping(value = "/config", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ConfigurationRestController {

	private AntPathMatcher antPathMatcher;

	@Resource
	private ConfigurationService configService;
	
	@PostConstruct
	public void afterPropertiesSet() {
		antPathMatcher = new AntPathMatcher();
	}

	@RequestMapping(value = "/bean-names", method = RequestMethod.GET)
	public String[] getBeanNames() {
		return configService.getBeanNames();
	}

	@RequestMapping(value = "/expr/**", method = RequestMethod.GET)
	public ResponseEntity<Object> evaluateBeanValue(HttpServletRequest request) throws ScriptException {
		String expression = extractPattern(request);
		return evaluateBeanValue(expression);
	}
	
	@RequestMapping(value = "/expr", method = {RequestMethod.GET, RequestMethod.POST})
	public ResponseEntity<Object> evaluateBeanValue(@RequestBody Map<String,Object> payload, HttpServletRequest request) throws ScriptException {
		String expression = (String) payload.get("e");
		return evaluateBeanValue(expression);
	}
	
	@RequestMapping(value = "/refresh", method = {RequestMethod.GET, RequestMethod.POST})
	public ResponseEntity<Boolean> refresh() {
		configService.refreshApplicationContext();
		return new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/sys-props", method = RequestMethod.GET)
	public Map<Object,Object> getSystemProperties() {
		return configService.getSystemProperties();
	}
	
	@RequestMapping(value = "/sys-props", method = RequestMethod.POST)
	public ResponseEntity<Boolean> setSystemProperties(@RequestBody Map<String,String> payload) {
		configService.setSystemProperty(payload.get("key"), payload.get("value"));
		return new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK);
	}
	
	@ExceptionHandler(Exception.class)
	public Map<String,Object> handleException(HttpServletRequest req, Exception e) {
		Map<String,Object> m = new LinkedHashMap<String,Object>();
		m.put("exception", e.getMessage());
		return m;
	}

	private String extractPattern(HttpServletRequest request) {
		String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
		return antPathMatcher.extractPathWithinPattern(bestMatchPattern, path);
	}
	
	private ResponseEntity<Object> evaluateBeanValue(String expression) throws ScriptException {
		Object o = configService.evaluateExpression(expression);
		if (o == null || o instanceof CharSequence || ClassUtils.isPrimitiveOrWrapper(o.getClass())) {
			HttpHeaders responseHeaders = new HttpHeaders();
		    responseHeaders.setContentType(MediaType.TEXT_PLAIN);
			return new ResponseEntity<Object>(o, responseHeaders, HttpStatus.OK);
		}
		return new ResponseEntity<Object>(o, HttpStatus.OK);
	}

}
