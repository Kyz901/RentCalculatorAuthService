package com.authservice.config;

import com.kuzmin.logger.LogLevel;
import com.kuzmin.logger.MultiLogger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggerConfiguration {

    @Bean
    public MultiLogger logger(
        @Value("${rentcalculator.log.level}") final String levelName,
        @Value("${rentcalculator.api.name}") final String apiName
    ) {
        return MultiLogger
            .builder()
            .logLevel(LogLevel.valueOf(levelName))
            .apiName(apiName)
            .build();
    }
}
