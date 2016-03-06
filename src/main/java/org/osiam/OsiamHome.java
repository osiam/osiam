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

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static com.google.common.base.Preconditions.checkState;

@Order(ConfigFileApplicationListener.DEFAULT_ORDER - 1)
public class OsiamHome implements ApplicationListener<ApplicationEvent> {

    private static final Logger logger = LoggerFactory.getLogger(OsiamHome.class);

    private boolean hasInitializedHome = false;
    private boolean shouldInitializeHome = true;
    private Path osiamHome;

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationEnvironmentPreparedEvent) {
            ConfigurableEnvironment environment = ((ApplicationEnvironmentPreparedEvent) event).getEnvironment();
            configure(environment);
            if(shouldInitializeHome) {
                initialize();
            }
        } else if (event instanceof ApplicationPreparedEvent) {
            writeLogs();
        }
    }

    public void configure(ConfigurableEnvironment environment) {
        String rawOsiamHome = environment.getProperty("osiam.home", System.getenv("HOME") + "/.osiam");
        checkState(!Strings.isNullOrEmpty(rawOsiamHome), "'osiam.home' is not set");
        osiamHome = Paths.get(rawOsiamHome).toAbsolutePath();
        environment.getPropertySources().addFirst(new PropertySource<Path>("osiamHome", osiamHome) {
            @Override
            public Object getProperty(String name) {
                return "osiam.home".equals(name) ? source.toString() : null;
            }
        });
    }

    public void initialize() {
        try {
            if (Files.notExists(osiamHome)) {
                Files.createDirectories(osiamHome);
            } else {
                checkState(Files.isDirectory(osiamHome), "'osiam.home' (%s) is not a directory", osiamHome);
                checkState(Files.isReadable(osiamHome), "'osiam.home' (%s) is not readable", osiamHome);
                checkState(Files.isExecutable(osiamHome), "'osiam.home' (%s) is not accessible", osiamHome);
            }

            if (!isEmpty(osiamHome)) {
                return;
            }

            checkState(Files.isWritable(osiamHome), "'osiam.home' (%s) is not writable", osiamHome);
            Resource[] resources = new PathMatchingResourcePatternResolver().getResources("classpath:/home/**/*");
            for (Resource resource : resources) {
                // don't process directories
                if (resource.getURL().toString().endsWith("/")) {
                    continue;
                }
                copyToHome(resource, osiamHome);
            }
            if (Files.notExists(osiamHome.resolve("data"))) {
                Files.createDirectories(osiamHome.resolve("data"));
            }

            hasInitializedHome = true;
        } catch (IOException e) {
            throw new IllegalStateException("Could not initialize osiam.home", e);
        }
    }

    public void writeLogs() {
        logger.info("osiam.home = {}", osiamHome);
        if (hasInitializedHome) {
            logger.info("Initialized osiam.home");
        }
    }

    /**
     * Inspired by http://stackoverflow.com/a/5937917/3171122
     */
    private boolean isEmpty(Path directory) throws IOException {
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory)) {
            return !directoryStream.iterator().hasNext();
        }
    }

    private void copyToHome(Resource resource, Path osiamHomeDir) throws IOException {
        String pathUnderHome = resource.getURL().toString().replaceFirst(".*home/", "");
        Path target = osiamHomeDir.resolve(pathUnderHome);
        Files.createDirectories(target.getParent());
        Files.copy(resource.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
    }

    public void shouldInitializeHome(boolean shouldInitializeHome) {
        this.shouldInitializeHome = shouldInitializeHome;
    }
}
