package org.osiam;

import java.sql.SQLException;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.servlet.Filter;
import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.osiam.security.authorization.AccessTokenValidationService;
import org.osiam.security.authorization.OsiamMethodSecurityExpressionHandler;
import org.osiam.security.helper.SSLRequestLoggingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.orm.hibernate4.HibernateExceptionTranslator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@ComponentScan
@EnableWebMvc
@EnableWebSecurity
@EnableTransactionManagement
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class ResourceServer {

    @Value("${org.osiam.resource-server.db.driver}")
    private String driverClassName;

    @Value("${org.osiam.resource-server.db.url}")
    private String databaseUrl;

    @Value("${org.osiam.resource-server.db.username}")
    private String databaseUserName;

    @Value("${org.osiam.resource-server.db.password}")
    private String databasePassword;

    @Value("${org.osiam.resource-server.db.dialect}")
    private String databasePlatform;

    @Value("${org.osiam.resource-server.db.vendor}")
    private String databaseVendor;

    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() {
        PropertySourcesPlaceholderConfigurer propertySources = new PropertySourcesPlaceholderConfigurer();
        Resource[] resources = new ClassPathResource[] { new ClassPathResource("resource-server.properties") };
        propertySources.setLocations(resources);
        propertySources.setIgnoreUnresolvablePlaceholders(true);
        return propertySources;
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
    @DependsOn("flyway")
    public EntityManagerFactory entityManagerFactory() throws SQLException {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(false);
        vendorAdapter.setShowSql(false);
        vendorAdapter.setDatabasePlatform(databasePlatform);

        Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.hbm2ddl.auto", "validate");
        jpaProperties.put("hibernate.ejb.naming_strategy", "org.hibernate.cfg.ImprovedNamingStrategy");

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan("org.osiam.storage.entities");
        factory.setDataSource(dataSource());
        factory.setJpaProperties(jpaProperties);
        factory.afterPropertiesSet();

        return factory.getNativeEntityManagerFactory();
    }

    @Bean
    public JpaTransactionManager transactionManager() throws SQLException {
        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(entityManagerFactory());
        return txManager;
    }

    @Bean
    public MetricRegistry metricRegistry() {
        return new MetricRegistry();
    }

    @Bean
    public HibernateExceptionTranslator hibernateExceptionTranslator() {
        return new HibernateExceptionTranslator();
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
}
