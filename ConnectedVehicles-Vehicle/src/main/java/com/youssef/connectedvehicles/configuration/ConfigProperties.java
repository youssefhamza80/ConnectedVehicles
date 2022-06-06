package com.youssef.connectedvehicles.configuration;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static lombok.AccessLevel.PRIVATE;

@ConfigurationProperties(prefix = "vehicle")
@FieldDefaults(level = PRIVATE)
@NoArgsConstructor
@Data
public class ConfigProperties {
	private long connectionTimeoutMinutes=1;
}
