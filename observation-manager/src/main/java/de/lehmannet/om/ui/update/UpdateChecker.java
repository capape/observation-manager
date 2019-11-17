package de.lehmannet.om.ui.update;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import de.lehmannet.om.ui.extension.IExtension;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.util.FloatUtil;

public class UpdateChecker implements Runnable {

    private static final String UPDATEFILE_LATESTVERSION = "latestVersion";
    private static final String UPDATEFILE_DOWNLOADURL = "downloadURL";

    private ObservationManager om = null;
    private ArrayList result = new ArrayList();

    public UpdateChecker(ObservationManager om) {

        this.om = om;

    }

    public List getResult() {

        return this.result;

    }

    @Override
    public void run() {

        // Check extensions
        List extensions = this.om.getExtensionLoader().getExtensions();
        ListIterator iterator = extensions.listIterator();
        IExtension currentExtension = null;
        URL currentExtensionURL = null;
        UpdateEntry currentResult = null;
        try {
            while (iterator.hasNext()) {
                currentExtension = (IExtension) iterator.next();
                currentExtensionURL = currentExtension.getUpdateInformationURL();
                if (currentExtensionURL != null) {
                    currentResult = this.checkForUpdates(currentExtension.getName(), currentExtension.getVersion(),
                            currentExtensionURL);
                    if (currentResult != null) { // New version found
                        result.add(currentResult);
                    }
                }
            }

            // Check OM itself
            currentResult = this.checkForUpdates("Observation Manager",
                    FloatUtil.parseFloat(ObservationManager.VERSION), ObservationManager.UPDATE_URL);
            if (currentResult != null) { // New version found
                result.add(currentResult);
            }

        } catch (ConnectException ce) {
            this.result = null; // This will indicate to the result retrieve that something went totally wrong
        }

    }

    private UpdateEntry checkForUpdates(String name, float oldVersion, URL checkURL) throws ConnectException {

        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) checkURL.openConnection();
            conn.setRequestProperty("User-Agent", "Observation Manager Update Client");
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                System.err.println("No update check possible for: " + checkURL + "\nHTTP Response was: "
                        + conn.getResponseMessage());
                conn.disconnect();

                throw new ConnectException("HTTP error while connecting to host for update");
            } else {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String currentLine = null;
                float newVersion = Float.NaN;
                URL downloadURL = null;
                do {
                    currentLine = in.readLine();

                    // Check whether line contains any data
                    if ((currentLine == null) || ("".equals(currentLine.trim()))) {
                        continue;
                    }

                    // Try to get online Version
                    if (currentLine.startsWith(UpdateChecker.UPDATEFILE_LATESTVERSION)) {
                        String s_newVersion = currentLine.substring(currentLine.indexOf("=") + 1);
                        newVersion = FloatUtil.parseFloat(s_newVersion);

                        // Comment this out for testing purposes, if you always want a download
                        if (newVersion <= oldVersion) { // Online version is NOT newer than current version
                            return null;
                        }
                    }

                    // Try to get download URL
                    if (currentLine.startsWith(UpdateChecker.UPDATEFILE_DOWNLOADURL)) {
                        String d_downloadURL = currentLine.substring(currentLine.indexOf("=") + 1);
                        downloadURL = new URL(d_downloadURL);
                    }

                    // We have all required informations, so we can exit
                    if ((downloadURL != null) && !(Float.isNaN(newVersion))) {
                        return new UpdateEntry(name, "" + oldVersion, "" + newVersion, downloadURL);
                    }

                } while (currentLine != null);

                return null;
            }

        } catch (IOException ioe) {
            if (ioe instanceof UnknownHostException) {
                System.err.println("Host for update unknown: " + checkURL);
                System.err.println(
                        "** Network connection not available or PROXY settings may be needed. Change the following line in obs.sh/obs.bat file to set proxy:");
                System.err.println("** start javaw -Djava.ext.dirs=.....  to");
                System.err.println(
                        "** start javaw -Dhttp.proxyHost={YOURPROXY} -Dhttp.proxyPort={YOURPROXYPORT} -Djava.ext.dirs=.....");
            } else {
                System.err.println("Error during update check for URL: " + checkURL + "\nNested exception was: " + ioe);
            }

            throw new ConnectException("Unable to connect to host for update");
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

    }

}
