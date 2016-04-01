/**
 * The MIT License (MIT)
 *
 * Copyright (C) 2013-2016 tarent solutions GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.osiam;

import com.google.common.collect.ImmutableMap;
import com.ryantenney.metrics.spring.config.annotation.EnableMetrics;
import org.osiam.cli.InitHome;
import org.osiam.cli.MigrateDb;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

@SpringBootApplication
@EnableWebSecurity
@EnableMetrics
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ComponentScan(excludeFilters = {
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = "org\\.osiam\\.cli.*")
})
public class Osiam extends SpringBootServletInitializer {

    private static final Map<String, Object> DEFAULT_PROPERTIES = ImmutableMap.<String, Object>of(
            "spring.config.location", "file:${osiam.home}/config/osiam.yaml"
    );

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication();
        String command = extractCommand(args);
        OsiamHome osiamHome = new OsiamHome();
        if ("initHome".equals(command)) {
            application.setSources(Collections.<Object>singleton(InitHome.class));
            application.setWebEnvironment(false);
        } else if ("migrateDb".equals(command)) {
            application.setSources(Collections.<Object>singleton(MigrateDb.class));
            application.setWebEnvironment(false);
            osiamHome.shouldInitializeHome(false);
        } else {
            application.setSources(Collections.<Object>singleton(Osiam.class));
        }
        application.addListeners(osiamHome);
        application.setDefaultProperties(DEFAULT_PROPERTIES);
        application.run(args);
    }

    private static String extractCommand(String[] arguments) {
        return Arrays.stream(arguments)
                .map(String::trim)
                .filter(argument -> !argument.startsWith("-"))
                .filter(argument -> !argument.startsWith("--"))
                .findFirst()
                .orElse("server");
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder applicationBuilder) {
        applicationBuilder.application().setDefaultProperties(DEFAULT_PROPERTIES);
        applicationBuilder.application().addListeners(new OsiamHome());
        return applicationBuilder;
    }

    @Bean
    public ShaPasswordEncoder shaPasswordEncoder() {
        ShaPasswordEncoder shaPasswordEncoder = new ShaPasswordEncoder(512);
        shaPasswordEncoder.setIterations(1000);
        return shaPasswordEncoder;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder(13);
    }
}
