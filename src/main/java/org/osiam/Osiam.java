package org.osiam;

import com.google.common.collect.ImmutableMap;
import com.ryantenney.metrics.spring.config.annotation.EnableMetrics;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import java.util.Map;

@SpringBootApplication
@EnableWebSecurity
@EnableMetrics
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class Osiam extends SpringBootServletInitializer {

    private static final Map<String, Object> DEFAULT_PROPERTIES = ImmutableMap.<String, Object>of(
            "osiam.home", System.getenv("HOME") + "/.osiam",
            "spring.config.location", "classpath:/home/config/osiam.yaml,file:${osiam.home}/config/osiam.yaml"
    );

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Osiam.class);
        application.setDefaultProperties(DEFAULT_PROPERTIES);
        application.run(args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder applicationBuilder) {
        applicationBuilder.application().setDefaultProperties(DEFAULT_PROPERTIES);
        return applicationBuilder.sources(Osiam.class);
    }
}
