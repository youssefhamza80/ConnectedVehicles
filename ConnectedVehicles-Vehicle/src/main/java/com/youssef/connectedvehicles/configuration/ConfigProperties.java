package com.youssef.connectedvehicles.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static lombok.AccessLevel.PRIVATE;

@ConfigurationProperties(prefix = "vehicle")
@AllArgsConstructor
@Data
@FieldDefaults(level = PRIVATE)
public class ConfigProperties {
	int connectionTimeoutMinutes;
}
