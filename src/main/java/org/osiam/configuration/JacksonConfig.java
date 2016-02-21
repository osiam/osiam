package org.osiam.configuration;

import com.codahale.metrics.json.MetricsModule;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.osiam.resources.helper.UserDeserializer;
import org.osiam.resources.scim.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.util.concurrent.TimeUnit;

@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilder jacksonBuilder() {
        Jackson2ObjectMapperBuilder jacksonBuilder = new Jackson2ObjectMapperBuilder();
        jacksonBuilder.filters(new SimpleFilterProvider().setFailOnUnknownId(false));
        jacksonBuilder.modules(jacksonUserDeserializerModule(), metricsModule());
        return jacksonBuilder;
    }

    private Module jacksonUserDeserializerModule() {
        return new SimpleModule("userDeserializerModule", new Version(1, 0, 0, null, "org.osiam", "osiam"))
                .addDeserializer(User.class, new UserDeserializer(User.SCHEMA));
    }

    private Module metricsModule() {
        return new MetricsModule(TimeUnit.SECONDS, TimeUnit.SECONDS, false);
    }
}
