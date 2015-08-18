package org.osiam;

import java.util.*;
import java.util.concurrent.*;

import javax.servlet.*;
import javax.sql.*;

import org.flywaydb.core.*;
import org.flywaydb.core.api.*;
import org.osiam.security.authorization.*;
import org.osiam.security.helper.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.boot.builder.*;
import org.springframework.boot.context.web.*;
import org.springframework.context.annotation.*;
import org.springframework.http.*;
import org.springframework.security.authentication.encoding.*;
import org.springframework.security.config.annotation.web.builders.*;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.oauth2.config.annotation.web.configuration.*;
import org.springframework.security.oauth2.config.annotation.web.configurers.*;
import org.springframework.security.oauth2.provider.error.*;
import org.springframework.transaction.annotation.*;
import org.springframework.web.filter.*;
import org.springframework.web.servlet.config.annotation.*;

import com.codahale.metrics.*;
import com.codahale.metrics.jvm.*;
import com.fasterxml.jackson.databind.*;
import com.google.common.collect.*;
import com.ryantenney.metrics.spring.config.annotation.*;
import com.zaxxer.hikari.*;

@SpringBootApplication
@EnableWebMvc
@EnableWebSecurity
@EnableTransactionManagement
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableMetrics
@PropertySource("classpath:resource-server.properties")
public class ResourceServer extends SpringBootServletInitializer {

    private static final Map<String, Object> NAMING_STRATEGY = ImmutableMap.<String, Object> of(
            "spring.jpa.hibernate.naming_strategy", "org.hibernate.cfg.ImprovedNamingStrategy");

    @Value("${org.osiam.resource-server.db.driver}")
    private String driverClassName;

    @Value("${org.osiam.resource-server.db.url}")
    private String databaseUrl;

    @Value("${org.osiam.resource-server.db.username}")
    private String databaseUserName;

    @Value("${org.osiam.resource-server.db.password}")
    private String databasePassword;

    @Value("${org.osiam.resource-server.db.vendor}")
    private String databaseVendor;

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(ResourceServer.class);
        application.setDefaultProperties(NAMING_STRATEGY);
        application.run(args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        application.application().setDefaultProperties(NAMING_STRATEGY);
        return application.sources(ResourceServer.class);
    }

    @Bean
    public Filter characterEncodingFilter() {
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        characterEncodingFilter.setForceEncoding(true);
        return characterEncodingFilter;
    }

    @Bean
    public Filter sslRequestLoggingFilter() {
        return new SSLRequestLoggingFilter();
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    @Primary
    @Bean
    public DataSource dataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setPoolName("osiam-resource-server-cp");
        hikariConfig.setDriverClassName(driverClassName);
        hikariConfig.setJdbcUrl(databaseUrl);
        hikariConfig.setUsername(databaseUserName);
        hikariConfig.setPassword(databasePassword);
        return new HikariDataSource(hikariConfig);
    }

    @Bean(initMethod = "migrate")
    public Flyway flyway() {
        Flyway flyway = new Flyway();
        flyway.setBaselineOnMigrate(true);
        flyway.setDataSource(dataSource());
        flyway.setLocations("db/migration/" + databaseVendor);
        flyway.setTable("resource_server_schema_version");
        MigrationVersion version = MigrationVersion.fromVersion("0");
        flyway.setBaselineVersion(version);
        return flyway;
    }

    @Bean
    public ShaPasswordEncoder passwordEncoder() {
        ShaPasswordEncoder passwordEncoder = new ShaPasswordEncoder(512);
        passwordEncoder.setIterations(1000);
        return passwordEncoder;
    }

    @Configuration
    @EnableResourceServer
    protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

        @Autowired
        private AccessTokenValidationService accessTokenValidationService;

        @Override
        public void configure(ResourceServerSecurityConfigurer resources) {
            resources.resourceId("oauth2res")
                    .tokenServices(accessTokenValidationService)
                    .expressionHandler(new OsiamMethodSecurityExpressionHandler());
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {
            http.exceptionHandling()
                    .accessDeniedHandler(oauthAccessDeniedHandler())
                    .and()
                    .authorizeRequests()
                    .antMatchers("/ServiceProviderConfigs").permitAll()
                    .antMatchers("/me/**").access("#oauth2.hasScope('ADMIN') or #oauth2.hasScope('ME')")
                    .antMatchers(HttpMethod.POST, "/Users/**").access("#oauth2.hasScope('ADMIN')")
                    .regexMatchers(HttpMethod.GET, "/Users/?").access("#oauth2.hasScope('ADMIN')")
                    .antMatchers("/Users/**")
                    .access("#oauth2.hasScope('ADMIN') or #oauth2.hasScope('ME') and #osiam.isOwnerOfResource()")
                    .anyRequest().access("#oauth2.hasScope('ADMIN')");
        }

        @Bean
        public OAuth2AuthenticationEntryPoint entryPoint() {
            OAuth2AuthenticationEntryPoint entryPoint = new OAuth2AuthenticationEntryPoint();
            entryPoint.setRealmName("oauth2-resource-server");
            return entryPoint;
        }

        @Bean
        public OAuth2AccessDeniedHandler oauthAccessDeniedHandler() {
            return new OAuth2AccessDeniedHandler();
        }
    }

    @Configuration
    @EnableMetrics
    protected static class MetricsConfiguration extends MetricsConfigurerAdapter {

        @Override
        public void configureReporters(MetricRegistry metricRegistry) {
            metricRegistry.register("jvm.memory", new MemoryUsageGaugeSet());
        }
    }
}
