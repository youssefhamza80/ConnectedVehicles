package com.youssef.altenchallenge.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "vehicle")
public class ConfigProperties {

	private long connectionTimeoutMinutes;

	public long getConnectionTimeoutMinutes() {
		return connectionTimeoutMinutes;
	}

	public void setConnectionTimeoutMinutes(long connectionTimeoutMinutes) {
		this.connectionTimeoutMinutes = connectionTimeoutMinutes;
	}

	public ConfigProperties() {
		super();
	}	
}
