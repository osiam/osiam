package org.osiam.configuration;

import com.codahale.metrics.json.MetricsModule;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.osiam.resources.helper.UserDeserializer;
import org.osiam.resources.scim.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class JacksonConfig {

    @Bean
    public Module jacksonUserDeserializerModule() {
        return new SimpleModule("userDeserializerModule", new Version(1, 0, 0, null, "org.osiam", "osiam"))
                .addDeserializer(User.class, new UserDeserializer(User.SCHEMA));
    }

    @Bean
    public Module metricsModule() {
        return new MetricsModule(TimeUnit.SECONDS, TimeUnit.SECONDS, false);
    }
}
