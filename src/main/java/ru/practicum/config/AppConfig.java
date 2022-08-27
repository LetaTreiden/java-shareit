package ru.practicum.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "file:C:\\myapp\\myapp.properties", ignoreResourceNotFound = true) // ������ ��� Windows
public class AppConfig {
}