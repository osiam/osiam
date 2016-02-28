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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static com.google.common.base.Preconditions.checkState;

@Component
public class OsiamHome {

    public static final Logger logger = LoggerFactory.getLogger(OsiamHome.class);

    private final Path osiamHomeDir;

    @Autowired
    public OsiamHome(@Value("${osiam.home}") String osiamHome) throws IOException {
        checkState(!Strings.isNullOrEmpty(osiamHome), "'osiam.home' is not set");
        osiamHomeDir = checkOsiamHome(osiamHome);
        logger.info("osiam.home = {}", osiamHomeDir);
        if (!isEmpty(osiamHomeDir)) {
            return;
        }
        checkState(Files.isWritable(osiamHomeDir), "'osiam.home' (%s) is not writable", osiamHomeDir);
        logger.info("Initializing osiam.home");
        Resource[] resources = new PathMatchingResourcePatternResolver().getResources("classpath:/home/**/*");
        for (Resource resource : resources) {
            // don't process directories
            if (resource.getURL().toString().endsWith("/")) {
                continue;
            }
            copyToHome(resource);
        }
    }

    public String getI18nDirectory() {
        return osiamHomeDir.resolve("i18n").toUri().toString();
    }

    public String getCssDirectory() {
        return osiamHomeDir.resolve("css").toUri().toString();
    }

    public String getJsDirectory() {
        return osiamHomeDir.resolve("js").toUri().toString();
    }

    private Path checkOsiamHome(String osiamHome) throws IOException {
        Path osiamHomeDir = Paths.get(osiamHome).toAbsolutePath();

        if (Files.notExists(osiamHomeDir)) {
            Files.createDirectories(osiamHomeDir);
        } else {
            checkState(Files.isDirectory(osiamHomeDir), "'osiam.home' (%s) is not a directory", osiamHomeDir);
            checkState(Files.isReadable(osiamHomeDir), "'osiam.home' (%s) is not readable", osiamHomeDir);
            checkState(Files.isExecutable(osiamHomeDir), "'osiam.home' (%s) is not accessible", osiamHomeDir);
        }

        return osiamHomeDir.toRealPath();
    }

    /**
     * Inspired by http://stackoverflow.com/a/5937917/3171122
     */
    private boolean isEmpty(Path directory) throws IOException {
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory)) {
            return !directoryStream.iterator().hasNext();
        }
    }

    private void copyToHome(Resource resource) throws IOException {
        String pathUnderHome = resource.getURL().toString().replaceFirst(".*home/", "");
        Path target = osiamHomeDir.resolve(pathUnderHome);
        Files.createDirectories(target.getParent());
        Files.copy(resource.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
    }
}
