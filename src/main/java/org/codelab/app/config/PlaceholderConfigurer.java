package org.codelab.app.config;

import java.util.Properties;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

public class PlaceholderConfigurer extends PropertyPlaceholderConfigurer {

	protected MessageSource messageSource;
	
	@Override
	protected String resolvePlaceholder(String placeholder, Properties props, int systemPropertiesMode) {
		String s = super.resolvePlaceholder(placeholder, props, systemPropertiesMode);
		if (s == null) {
			s = getMessage(placeholder);
		}
		return s;
	}

	public MessageSource getMessageSource() {
		return messageSource;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
	private String getMessage(String code) {
		if (messageSource != null) {
			try {
				return messageSource.getMessage(code, null, null);
			} catch (NoSuchMessageException e) {
				return null;
			}
		}
		return null;
	}

}
