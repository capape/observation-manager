package de.lehmannet.om.ui.navigation.observation.utils;

import java.io.File;
import java.util.StringTokenizer;



public class InstallDir {

    private static final String MAIN_JAR_NAME = "observationManager.jar";

    private final String folderPath;
    private final File installDir;

    private InstallDir(String folderPath) {
        this.folderPath = folderPath;
        
        final String extpath = System.getProperty("java.ext.dirs");

        final StringTokenizer tokenizer = new StringTokenizer(extpath, "" + File.pathSeparatorChar);

        String entry = null;
        while (tokenizer.hasMoreTokens()) {
            entry = tokenizer.nextToken();
            if (entry.lastIndexOf(MAIN_JAR_NAME) != -1) {
                // We found our main jar in the extpath, so we (hopefully!) can
                // calculate the install dir
                File jarPath = new File(entry);
                // Get lib dir, and then get lib dirs parent dir, which should
                // be the installation dir....hopefully
                this.installDir = jarPath.getParentFile().getParentFile();
                return;
            }
        }

        // If not set current user working dir and hope for the best ;-)
        this.installDir = new File(System.getProperty("user.dir"));
    }

    public String getPath() {
        return this.installDir.getAbsolutePath();
    }

    public String getPathForFile(String nameFile) {
        return this.installDir.getAbsolutePath() + File.separatorChar + nameFile;
    }
    public String getPathForFolder(String nameFile) {
        return this.installDir.getAbsolutePath() + File.separatorChar + nameFile + File.separatorChar;
    }
    
    public File getInstallDir() {

        return this.installDir;

    }
   

    public static class Builder {
        private String folderName;
        public Builder() {

        }

        public Builder withInstallDir(String folderName) {
            this.folderName = folderName;
            return this;
        }

        public InstallDir build() {
            return new InstallDir(folderName);
        }

    }
}