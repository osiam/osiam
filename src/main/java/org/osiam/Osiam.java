package org.osiam;

import com.google.common.collect.ImmutableMap;
import com.ryantenney.metrics.spring.config.annotation.EnableMetrics;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.osiam.security.helper.SSLRequestLoggingFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.servlet.Filter;
import javax.sql.DataSource;
import java.util.Map;

@SpringBootApplication
@EnableWebMvc
@EnableWebSecurity
@EnableTransactionManagement
@EnableMetrics
@EnableAspectJAutoProxy(proxyTargetClass = true)
@PropertySource("classpath:/osiam.properties")
public class Osiam extends SpringBootServletInitializer {

    private static final Map<String, Object> DEFAULT_PROPERTIES = ImmutableMap.<String, Object>of(
            "spring.thymeleaf.prefix", "classpath:/osiam/templates/web/",
            "spring.messages.basename", "osiam/i18n/login",
            "spring.jpa.hibernate.naming_strategy", "org.hibernate.cfg.ImprovedNamingStrategy");

    @Value("${org.osiam.db.driver}")
    private String driverClassName;

    @Value("${org.osiam.db.url}")
    private String databaseUrl;

    @Value("${org.osiam.db.username}")
    private String databaseUserName;

    @Value("${org.osiam.db.password}")
    private String databasePassword;

    @Value("${org.osiam.db.vendor}")
    private String databaseVendor;

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Osiam.class);
        application.setDefaultProperties(DEFAULT_PROPERTIES);
        application.run(args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        application.application().setDefaultProperties(DEFAULT_PROPERTIES);
        return application.sources(Osiam.class);
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

    @Primary
    @Bean
    public DataSource dataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setPoolName("osiam-cp");
        hikariConfig.setDriverClassName(driverClassName);
        hikariConfig.setJdbcUrl(databaseUrl);
        hikariConfig.setUsername(databaseUserName);
        hikariConfig.setPassword(databasePassword);
        return new HikariDataSource(hikariConfig);
    }

    @Bean(initMethod = "migrate")
    public Flyway flyway() {
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource());
        flyway.setLocations("db/migration/" + databaseVendor);
        flyway.setBaselineOnMigrate(true);
        flyway.setBaselineVersion(MigrationVersion.fromVersion("2"));
        return flyway;
    }

    @Bean
    public ShaPasswordEncoder passwordEncoder() {
        ShaPasswordEncoder passwordEncoder = new ShaPasswordEncoder(512);
        passwordEncoder.setIterations(1000);
        return passwordEncoder;
    }

}
