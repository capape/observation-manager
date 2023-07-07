package de.lehmannet.om.ui.update;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.lehmannet.om.ui.navigation.ObservationManager;

public class UpdateChecker implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateChecker.class);

    private static final String STRING_UDATE_URL = "https://raw.githubusercontent.com/capape/observation-manager/master/observation-manager/observation-manager-app/src/main/resources/version.properties";

    private static final String UPDATEFILE_LATESTVERSION = "latest.version";
    private static final String UPDATEFILE_DOWNLOADURL = "download.url";

    private ObservationManager om = null;
    private List<UpdateEntry> result = new ArrayList<>();

    public UpdateChecker(ObservationManager om) {

        this.om = om;

    }

    public List<UpdateEntry> getResult() {

        return Collections.unmodifiableList(this.result);

    }

    public boolean isUpdateAvailable() {
        return this.result != null && !this.result.isEmpty();
    }

    @Override
    public void run() {

        try {

            Optional<URL> url = UpdateChecker.getUpdateUrl();
            if (url.isEmpty()) {
                this.result = null;
            } else {

                // Check OM itself
                UpdateEntry currentResult = this.checkForUpdates("Observation Manager", om.getVersion(), url.get());
                if (currentResult != null) { // New version found
                    result.add(currentResult);
                }
            }

        } catch (ConnectException ce) {
            this.result = null; // This will indicate to the result retrieve that something went totally wrong
        }

    }

    private UpdateEntry checkForUpdates(String name, String oldVersion, URL checkURL) throws ConnectException {

        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) checkURL.openConnection();
            conn.setRequestProperty("User-Agent", "Observation Manager Update Client");
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                LOGGER.error("No update check possible for: {}. HTTP Response was: {}", checkURL,
                        conn.getResponseMessage());
                conn.disconnect();

                throw new ConnectException("HTTP error while connecting to host for update");
            } else {
                try (BufferedReader in = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    String currentLine = null;

                    URL downloadURL = null;
                    // Try to get online Version
                    String newVersion = oldVersion;

                    do {
                        currentLine = in.readLine();

                        // Check whether line contains any data
                        if ((currentLine == null) || ("".equals(currentLine.trim()))) {
                            continue;
                        }

                        if (currentLine.startsWith(UpdateChecker.UPDATEFILE_LATESTVERSION)) {

                            newVersion = currentLine.substring(currentLine.indexOf("=") + 1);

                        }

                        // Try to get download URL
                        if (currentLine.startsWith(UpdateChecker.UPDATEFILE_DOWNLOADURL)) {
                            String dDownloadURL = currentLine.substring(currentLine.indexOf("=") + 1);
                            downloadURL = new URL(dDownloadURL);
                        }

                        // We have all required informations, so we can exit
                        if ((downloadURL != null) && Version.isValidVersion(newVersion)) {
                            return new UpdateEntry(name, oldVersion, newVersion, downloadURL);
                        }

                    } while (currentLine != null);

                    return null;
                }
            }

        } catch (IOException ioe) {
            if (ioe instanceof UnknownHostException) {
                LOGGER.error("Host for update unknown: {} ", checkURL);
                LOGGER.error(
                        "** Network connection not available or PROXY settings may be needed. Change the following line in obs.sh/obs.bat file to set proxy:");
                LOGGER.error("** start javaw -Djextensions.dirs=.....  to");
                LOGGER.error(
                        "** start javaw -Dhttp.proxyHost={YOURPROXY} -Dhttp.proxyPort={YOURPROXYPORT} -Dextensions.dirs=.....");
            } else {
                LOGGER.error("Error during update check for URL:  {}", checkURL, ioe);
            }

            throw new ConnectException("Unable to connect to host for update");
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

    }

    private static Optional<URL> getUpdateUrl() {
        try {
            return Optional.of(new URL(STRING_UDATE_URL));
        } catch (MalformedURLException url) {
            LOGGER.error("Malformed update check URL: {} ", STRING_UDATE_URL);
        }
        return Optional.empty();
    }

}
