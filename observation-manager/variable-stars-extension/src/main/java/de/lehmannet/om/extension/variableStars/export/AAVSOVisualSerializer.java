package de.lehmannet.om.extension.variableStars.export;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import de.lehmannet.om.IFinding;
import de.lehmannet.om.IObservation;
import de.lehmannet.om.IObserver;
import de.lehmannet.om.Observer;
import de.lehmannet.om.extension.variableStars.FindingVariableStar;
import de.lehmannet.om.util.DateConverter;

import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AAVSOVisualSerializer implements ISerializer {

    private static Logger log = LoggerFactory.getLogger(AAVSOVisualSerializer.class);
    // ---------- AAVSO Comment codes

    /* B: Sky is bright, moon, twilight, light pollution, aurorae. */
    private static final String COMMENTCODE_B = "B";

    /* U: Clouds, dust, smoke, haze, etc. */
    private static final String COMMENTCODE_U = "U";

    /* W: Poor seeing. */
    private static final String COMMENTCODE_W = "W";

    /* L: Low in the sky, near horizon, in trees, obstructed view. */
    private static final String COMMENTCODE_L = "L";

    /* D: Unusual Activity (fading, flare, bizarre behavior, etc.) */
    private static final String COMMENTCODE_D = "D";

    /* Y: Outburst. */
    private static final String COMMENTCODE_Y = "Y";

    /* K: Non-AAVSO chart. */
    private static final String COMMENTCODE_K = "K";

    /* S: Comparison sequence problem. */
    private static final String COMMENTCODE_S = "S";

    /* Z: Magnitude of star uncertain. */
    private static final String COMMENTCODE_Z = "Z";

    /* I: Identification of star uncertain. */
    private static final String COMMENTCODE_I = "I";

    /* V: Faint star, near observing limit, only glimpsed. */
    private static final String COMMENTCODE_V = "V";

    // ---------- AAVSO Header parameters

    private static final String PARAMETER_TYPE = "#TYPE=";

    private static final String PARAMETER_OBSERVERCODE = "#OBSCODE=";

    private static final String PARAMETER_SOFTWARE = "#SOFTWARE=";

    private static final String PARAMETER_DELIMITER = "#DELIM=";

    private static final String PARAMETER_DATE = "#DATE=";

    private static final String PARAMETER_OBSERVATIONTYPE = "#OBSTYPE=";

    // ---------- Internal constants

    // Constant type is always visual. Extended format is not supported
    private static final String VISUAL = "Visual";

    // Constant date format
    private static final String JULIAN_DATE = "JD";

    // Constant delimiter character
    private static final char DELIMITER = '|'; // GMB - it was ';' but HTML entities already embed ';'

    // Constant not applicable string
    private static final String NOT_APPLICABLE = "na";

    // GMB - Constant line separator in notes field (CR LF replacement)
    private static final char NOTES_LINE_SEP = '-';

    // ------------------
    // Instance Variables ------------------------------------------------
    // ------------------

    // Last used AAVSO Observer initials (3 characters)
    private String lastAavsoInitals = "";

    // Name of the upload software
    private String softwareName = "OpenAstronomyLog Java API";

    // List of variable star observations (IObservation) which should be written
    // into file
    private List<IObservation> variableStarObservations = new ArrayList<>(0);

    // ------------
    // Constructors -----------------------------------------------------------
    // ------------

    // ------------------------------------------------------------------------
    /**
     * Constructor<br>
     *
     * @param softwareName
     *            Name and version of software used to create the format
     * @param variableStarObservations
     *            List of IObservation elements for serialization
     */
    public AAVSOVisualSerializer(String softwareName, List<IObservation> variableStarObservations) {

        this.softwareName = softwareName;
        this.variableStarObservations = variableStarObservations;

    }

    // -----------
    // ISerializer ------------------------------------------------------------
    // -----------

    // ------------------------------------------------------------------------
    /**
     * Serialize all observations to stream
     */
    @Override
    public int serialize(OutputStream stream) throws Exception {

        // Write header
        stream.write(this.getHeader().getBytes("UTF-8"));

        ListIterator<IObservation> iterator = this.variableStarObservations.listIterator();
        IObservation currentObservation = null;
        IObserver currentObserver = null;
        String initials = "";
        int counter = 0;
        while (iterator.hasNext()) {
            currentObservation = iterator.next();
            currentObserver = currentObservation.getObserver();

            // Get Variable Star Finding
            IFinding finding = (IFinding) currentObservation.getResults().get(0);
            if (!(finding instanceof FindingVariableStar)) {
                String message = "Finding must be of type de.lehmannet.om.extension.variableStars.FindingVariableStar for observaton: "
                        + currentObservation;
                this.closeStreamOnError(stream, message);
                throw new Exception(message);
            }

            // Get AAVSO observer initals
            initials = currentObserver.getUsernameForAccount(Observer.ACCOUNT_AAVSO);
            if ((initials == null) || ("".equals(initials.trim()))) {
                String message = "No AAVSO observer initials found for:\n" + currentObserver + "\nat Observation: \n"
                        + currentObservation;
                log.error(message);
                continue; // Don't cancel export, but try next entry
                /*
                 * this.closeStreamOnError(stream, message); throw new Exception(message);
                 */
            }

            writeObserver(stream, initials);

            writeName(stream, currentObservation);

            writeObservation(stream, currentObservation);

            FindingVariableStar fvs = (FindingVariableStar) finding;

            writeMagnitude(stream, fvs);

            writeComments(stream, fvs);

            // Write comparism star data (only first and second here. Rest goes into
            // comments)

            writeComparismData(stream, fvs);

            writeChartDate(stream, fvs);

            writeNotes(stream, fvs);
            // The finding status to exported
            fvs.setAlreadyExportedToAAVSO(true);

            // Next line for next observation
            stream.write("\n".getBytes("UTF-8"));

            counter++;

        }

        // End stream correctly
        stream.flush();
        stream.close();

        return counter;

    }

    private String writeObservation(OutputStream stream, IObservation currentObservation) throws IOException {
        // Get observation julian date and write it into stream
        String obsDate = "" + DateConverter.toJulianDate(currentObservation.getBegin().toZonedDateTime());
        stream.write(obsDate.getBytes("UTF-8"));
        stream.write(AAVSOVisualSerializer.DELIMITER);
        return obsDate;
    }

    private String writeComparismData(OutputStream stream, FindingVariableStar fvs) throws IOException {

        List<String> comparismStars = fvs.getComparismStars();
        String cs1 = comparismStars.get(0);
        stream.write(cs1.getBytes("UTF-8"));
        stream.write(AAVSOVisualSerializer.DELIMITER);
        if (comparismStars.size() == 1) { // Only one comparism star
            stream.write(AAVSOVisualSerializer.NOT_APPLICABLE.getBytes("UTF-8"));
            stream.write(AAVSOVisualSerializer.DELIMITER);
        } else if (comparismStars.size() > 1) { // At least two comparism stars
            String cs2 = (String) comparismStars.get(1);
            if (cs2 != null) {
                stream.write(cs2.getBytes("UTF-8"));
                stream.write(AAVSOVisualSerializer.DELIMITER);
            } else {
                stream.write(AAVSOVisualSerializer.NOT_APPLICABLE.getBytes("UTF-8"));
                stream.write(AAVSOVisualSerializer.DELIMITER);
            }
        }
        return cs1;
    }

    private void writeNotes(OutputStream stream, FindingVariableStar fvs) throws IOException {
        // Write notes
        StringBuilder notes = new StringBuilder();

        List<String> comparismStars = fvs.getComparismStars();
        // If there are more comp. stars add them here
        if (comparismStars.size() > 2) {
            ListIterator<String> compStarItertor = comparismStars.listIterator(2);
            while (compStarItertor.hasNext()) {
                notes.append(compStarItertor.next()).append(","); // This delimiter must be !=
                // AAVSOVisualSerializer.DELIMITER
            }
            notes.append(" - ");
        }
        notes.append(fvs.getDescription());

        // GMB - escaping non-ASCII chars to html4 entities and replaces CR LF with
        // dashes
        String rawNotes = "";
        rawNotes = StringEscapeUtils.escapeHtml4(notes.toString());
        rawNotes = rawNotes.replace('\n', NOTES_LINE_SEP);
        rawNotes = rawNotes.replace('\r', NOTES_LINE_SEP);
        notes = new StringBuilder(rawNotes);
        // GMB - end patch

        // Make sure notes are not longer then 100 characters
        if (notes.length() > 100) {
            notes = new StringBuilder(notes.substring(0, 100));
        }

        // If there's no note/description, set na
        if ("".equals(notes.toString())) {
            notes = new StringBuilder(AAVSOVisualSerializer.NOT_APPLICABLE);
        }

        stream.write(notes.toString().getBytes("UTF-8"));
    }

    private void writeChartDate(OutputStream stream, FindingVariableStar fvs) throws IOException {
        // Write chart
        stream.write(fvs.getChartDate().getBytes("UTF-8"));
        stream.write(AAVSOVisualSerializer.DELIMITER);
    }

    private String writeComments(OutputStream stream, FindingVariableStar fvs) throws IOException {
        // Write comment codes
        String comments = this.getCommentCode(fvs);
        stream.write(comments.getBytes("UTF-8"));
        stream.write(AAVSOVisualSerializer.DELIMITER);
        return comments;
    }

    private String writeMagnitude(OutputStream stream, FindingVariableStar fvs) throws IOException {
        // Write magnitude
        String mag = "";
        if (fvs.isMagnitudeFainterThan()) {
            mag = "<";
        }
        mag = mag + fvs.getMagnitude();
        stream.write(mag.getBytes("UTF-8"));
        stream.write(AAVSOVisualSerializer.DELIMITER);
        return mag;
    }

    private void writeName(OutputStream stream, IObservation currentObservation) throws IOException {
        // Write variable star name
        stream.write(currentObservation.getTarget().getName().getBytes("UTF-8"));
        stream.write(AAVSOVisualSerializer.DELIMITER);
    }

    private void writeObserver(OutputStream stream, String initials) throws IOException {
        // Write observer AAVSO initals to stream (in case the observer initials changed
        // since last loop)
        if (!initials.equals(this.lastAavsoInitals)) {
            String obsCodeParameter = AAVSOVisualSerializer.PARAMETER_OBSERVERCODE + initials + "\n";
            stream.write(obsCodeParameter.getBytes("UTF-8"));
            this.lastAavsoInitals = initials;
        }
    }

    // ---------------
    // Private Methods --------------------------------------------------------
    // ---------------

    // ------------------------------------------------------------------------
    private void closeStreamOnError(OutputStream stream, String message) {

        String finalMessage = "ERROR - " + message;

        try {
            stream.write(finalMessage.getBytes("UTF-8"));
            stream.flush();
            stream.close();
        } catch (IOException ioe) {
            log.error("Unable to close stream on error.");
        }

    }

    // ------------------------------------------------------------------------
    private String getHeader() {

        return AAVSOVisualSerializer.PARAMETER_TYPE + AAVSOVisualSerializer.VISUAL + "\n"
                + AAVSOVisualSerializer.PARAMETER_SOFTWARE + this.softwareName + "\n"
                + AAVSOVisualSerializer.PARAMETER_DELIMITER + AAVSOVisualSerializer.DELIMITER + "\n"
                + AAVSOVisualSerializer.PARAMETER_DATE + AAVSOVisualSerializer.JULIAN_DATE + "\n"
                + AAVSOVisualSerializer.PARAMETER_OBSERVATIONTYPE + AAVSOVisualSerializer.VISUAL + "\n";

    }

    // ------------------------------------------------------------------------
    private String getCommentCode(FindingVariableStar finding) {

        String result = "";

        if (finding.isBrightSky()) {
            result = result + AAVSOVisualSerializer.COMMENTCODE_B;
        }

        if (finding.isClouds()) {
            result = result + AAVSOVisualSerializer.COMMENTCODE_U;
        }

        if (finding.isPoorSeeing()) {
            result = result + AAVSOVisualSerializer.COMMENTCODE_W;
        }

        if (finding.isNearHorizion()) {
            result = result + AAVSOVisualSerializer.COMMENTCODE_L;
        }

        if (finding.isUnusualActivity()) {
            result = result + AAVSOVisualSerializer.COMMENTCODE_D;
        }

        if (finding.isOutburst()) {
            result = result + AAVSOVisualSerializer.COMMENTCODE_Y;
        }

        if (finding.isNonAAVSOchart()) {
            result = result + AAVSOVisualSerializer.COMMENTCODE_K;
        }

        if (finding.isComparismSequenceProblem()) {
            result = result + AAVSOVisualSerializer.COMMENTCODE_S;
        }

        if (finding.isMagnitudeUncertain()) {
            result = result + AAVSOVisualSerializer.COMMENTCODE_Z;
        }

        if (finding.isStarIdentificationUncertain()) {
            result = result + AAVSOVisualSerializer.COMMENTCODE_I;
        }

        if (finding.isFaintStar()) {
            result = result + AAVSOVisualSerializer.COMMENTCODE_V;
        }

        if ("".equals(result)) {
            result = AAVSOVisualSerializer.NOT_APPLICABLE;
        }

        return result;

    }

}
