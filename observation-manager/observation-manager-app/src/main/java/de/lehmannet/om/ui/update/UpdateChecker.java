package de.lehmannet.om.ui.update;

import de.lehmannet.om.ui.navigation.ObservationManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.semver4j.Semver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateChecker implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateChecker.class);

    public static URL UPDATE_URL = null;

    static {
        try {
            UPDATE_URL = new URL("https://capape.github.io/om/version.properties");
        } catch (MalformedURLException url) {
            LOGGER.error("Malformed update check URL: {} ", UPDATE_URL);
        }
    }

    private static final String UPDATEFILE_LATESTVERSION = "latest.version";
    private static final String UPDATEFILE_DOWNLOADURL = "download.url";

    private ObservationManager om = null;
    private List<UpdateEntry> result = new ArrayList<>();

    public UpdateChecker(ObservationManager om) {

        this.om = om;
    }

    public List<UpdateEntry> getResult() {

        return this.result;
    }

    public boolean isUpdateAvailable() {
        return this.result != null && !this.result.isEmpty();
    }

    @Override
    public void run() {

        try {

            // Check OM itself
            UpdateEntry currentResult = this.checkForUpdates("Observation Manager", om.getVersion(), UPDATE_URL);
            if (currentResult != null) { // New version found
                result.add(currentResult);
            }

        } catch (ConnectException ce) {
            this.result = null; // This will indicate to the result retrieve that something went totally wrong
        }
    }

    private UpdateEntry checkForUpdates(String name, String oldVersion, URL checkURL) throws ConnectException {

        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) checkURL.openConnection();
            conn.setReadTimeout(3000);
            conn.setReadTimeout(2000);
            conn.setRequestProperty("User-Agent", "Observation Manager Update Client");
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                LOGGER.error(
                        "No update check possible for: {}. HTTP Response was: {}", checkURL, conn.getResponseMessage());
                conn.disconnect();

                throw new ConnectException("HTTP error while connecting to host for update");
            } else {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String currentLine = null;

                    URL downloadURL = null;
                    // Try to get online Version
                    String newVersion = oldVersion;

                    do {
                        currentLine = in.readLine();

                        // Check whether line contains any data
                        if (StringUtils.isNotBlank(currentLine)) {

                            if (currentLine.startsWith(UPDATEFILE_LATESTVERSION)) {

                                newVersion = currentLine.substring(currentLine.indexOf("=") + 1);
                            }

                            // Try to get download URL
                            if (currentLine.startsWith(UPDATEFILE_DOWNLOADURL)) {
                                String downloadSpec = currentLine.substring(currentLine.indexOf("=") + 1);
                                downloadURL = new URL(downloadSpec);
                            }

                            // We have all required informations, so we can exit
                            if (downloadURL != null
                                    && Semver.isValid(newVersion)
                                    && Semver.parse(newVersion).isGreaterThan(oldVersion)) {
                                return new UpdateEntry(name, oldVersion, newVersion, downloadURL);
                            }
                        }

                    } while (currentLine != null);
                }
                return null;
            }

        } catch (SocketTimeoutException | UnknownHostException ee) {
            LOGGER.error("Host for update unknown: {} ", checkURL);
            LOGGER.error(
                    "** Network connection not available or PROXY settings may be needed. Change the following line in obs.sh/obs.bat file to set proxy:");
            LOGGER.error("** start javaw -Djextensions.dirs=.....  to");
            LOGGER.error(
                    "** start javaw -Dhttp.proxyHost={YOURPROXY} -Dhttp.proxyPort={YOURPROXYPORT} -Dextensions.dirs=.....");
            throw new ConnectException("Unable to connect to host for update");
        } catch (IOException ioe) {
            LOGGER.error("Error during update check for URL:  {}", checkURL, ioe);
            throw new ConnectException("Unable to connect to host for update");
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
