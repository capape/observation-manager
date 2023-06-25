package de.lehmannet.om.ui.util;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Backup {

    private final static Logger LOGGER = LoggerFactory.getLogger(Backup.class);

    private final boolean created;
    private final String path;

    private Backup(String path, boolean created) {
        this.created = created;
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public static Backup create(String newPath) {
        // Saving same file make a copy first
        Path originalPath = FileSystems.getDefault().getPath(newPath);

        if (originalPath.toFile().exists()) {
            String backupPath = newPath + Instant.now().toEpochMilli() + ".backup";
            Path copied = FileSystems.getDefault().getPath(backupPath);
            try {
                Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);
                return new Backup(backupPath, true);

            } catch (IOException e) {
                LOGGER.error("Problem making backup for {} into {}", newPath, backupPath, e);
            }
        }

        return new Backup("", false);
    }

    public void delete() {

        if (created) {
            Path backupPath = FileSystems.getDefault().getPath(path);
            try {
                Files.delete(backupPath);
            } catch (IOException e) {
                LOGGER.error("Problem deleting backup {}", path, e);
            }
        }

    }
}
