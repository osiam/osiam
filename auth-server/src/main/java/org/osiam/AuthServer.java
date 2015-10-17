/*
 * Copyright (C) 2013 tarent AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.osiam;

import com.google.common.collect.ImmutableMap;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.servlet.Filter;
import javax.sql.DataSource;
import java.util.Map;

@SpringBootApplication
@EnableTransactionManagement
@EnableAspectJAutoProxy(proxyTargetClass = true)
@PropertySource("classpath:/auth-server.properties")
public class AuthServer extends SpringBootServletInitializer {

    private static final Map<String, Object> DEFAULT_PROPERTIES = ImmutableMap.<String, Object>of(
            "spring.thymeleaf.prefix", "classpath:/auth-server/templates/web/",
            "spring.messages.basename", "auth-server/i18n/login"
    );

    @Value("${org.osiam.auth-server.db.driver}")
    private String driverClassName;

    @Value("${org.osiam.auth-server.db.url}")
    private String databaseUrl;

    @Value("${org.osiam.auth-server.db.username}")
    private String databaseUserName;

    @Value("${org.osiam.auth-server.db.password}")
    private String databasePassword;

    @Value("${org.osiam.auth-server.db.vendor}")
    private String databaseVendor;

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(AuthServer.class);
        application.setDefaultProperties(DEFAULT_PROPERTIES);
        application.run(args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        application.application().setDefaultProperties(DEFAULT_PROPERTIES);
        return application.sources(AuthServer.class);
    }

    @Bean
    public Filter characterEncodingFilter() {
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        characterEncodingFilter.setForceEncoding(true);
        return characterEncodingFilter;
    }

    @Primary
    @Bean
    public DataSource hikariDataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setPoolName("osiam-auth-server-cp");
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
        flyway.setDataSource(hikariDataSource());
        flyway.setLocations("db/migration/" + databaseVendor);
        flyway.setTable("auth_server_schema_version");
        MigrationVersion version = MigrationVersion.fromVersion("0");
        flyway.setBaselineVersion(version);
        return flyway;
    }
}
