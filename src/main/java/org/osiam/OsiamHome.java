package org.osiam;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.google.common.base.Preconditions.checkState;

@Component
public class OsiamHome {

    public static final Logger logger = LoggerFactory.getLogger(OsiamHome.class);

    private final Path osiamHomeDir;

    @Autowired
    public OsiamHome(@Value("${osiam.home}") String osiamHome) throws IOException {
        checkState(!Strings.isNullOrEmpty(osiamHome), "'osiam.home' is not set");
        this.osiamHomeDir = Paths.get(osiamHome).toRealPath();
        logger.info("osiam.home = {}", osiamHomeDir);
        checkState(Files.exists(osiamHomeDir), "'osiam.home' (%s) does not exist", osiamHomeDir);
        checkState(Files.isDirectory(osiamHomeDir), "'osiam.home' (%s) is not a directory", osiamHomeDir);
        checkState(Files.isReadable(osiamHomeDir), "'osiam.home' (%s) is not readable", osiamHomeDir);
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
}
