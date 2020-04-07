package de.lehmannet.om.extension.variableStars;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.lehmannet.om.Finding;
import de.lehmannet.om.IExtendableSchemaElement;
import de.lehmannet.om.mapper.FindingVariableStarMapper;
import de.lehmannet.om.util.SchemaException;

/**
 * FindingVariableStar extends the de.lehmannet.om.Finding class. Its
 * specialised for variable star observations and their findings. The class is
 * mostly oriented after the recommondations of the german "AAVSO - American
 * Association of Variable Star Observers"
 * (<a href="http://www.aavso.org/">AAVSO Homepage</a>).<br>
 * 
 * @author doergn@users.sourceforge.net
 * @since 2.0
 */
public class FindingVariableStar extends Finding {

    // ---------
    // Constants ---------------------------------------------------------
    // ---------
    // XSML schema instance value. Enables class/schema loaders to identify this
    // class
    public static final String XML_XSI_TYPE_VALUE = "oal:findingsVariableStarType";
    
    // Constant for XML representation: finding element observed visual magnitude
    public static final String XML_ELEMENT_VISMAG = "visMag";

    // Constant for XML representation: finding attribute fainter than
    public static final String XML_ELEMENT_FINDING_ATTRIBUTE_FAINTERTHAN = "fainterThan";

    // Constant for XML representation: finding attribute uncertain (magnitude)
    public static final String XML_ELEMENT_FINDING_ATTRIBUTE_UNCERTAIN = "uncertain";

    // Constant for XML representation: finding element comparism star
    public static final String XML_ELEMENT_COMPARISMSTAR = "comparisonStar";

    // Constant for XML representation: finding element chart id/date
    public static final String XML_ELEMENT_CHARTID = "chartID";

    // Constant for XML representation: finding attribute nonAAVSOchart
    public static final String XML_ELEMENT_FINDING_ATTRIBUTE_NONAAVSOCHART = "nonAAVSOchart";

    // Constant for XML representation: finding attribute nonAAVSOchart
    public static final String XML_ELEMENT_FINDING_ATTRIBUTE_BRIGHTSKY = "brightSky";

    // Constant for XML representation: finding attribute nonAAVSOchart
    public static final String XML_ELEMENT_FINDING_ATTRIBUTE_CLOUDS = "clouds";

    // Constant for XML representation: finding attribute nonAAVSOchart
    public static final String XML_ELEMENT_FINDING_ATTRIBUTE_POORSEEING = "poorSeeing";

    // Constant for XML representation: finding attribute nonAAVSOchart
    public static final String XML_ELEMENT_FINDING_ATTRIBUTE_NEARHORIZON = "nearHorizion";

    // Constant for XML representation: finding attribute nonAAVSOchart
    public static final String XML_ELEMENT_FINDING_ATTRIBUTE_UNUSUALACTIVITY = "unusualActivity";

    // Constant for XML representation: finding attribute nonAAVSOchart
    public static final String XML_ELEMENT_FINDING_ATTRIBUTE_OUTBURST = "outburst";

    // Constant for XML representation: finding attribute nonAAVSOchart
    public static final String XML_ELEMENT_FINDING_ATTRIBUTE_COMPARISMSEQPROBLEM = "comparismSequenceProblem";

    // Constant for XML representation: finding attribute nonAAVSOchart
    public static final String XML_ELEMENT_FINDING_ATTRIBUTE_STARIDENTIFICATIONUNCERTAIN = "starIdentificationUncertain";

    // Constant for XML representation: finding attribute nonAAVSOchart
    public static final String XML_ELEMENT_FINDING_ATTRIBUTE_FAINTSTAR = "faintStar";

    // Constant for XML representation: finding comment indicating whether this
    // finding was already exported
    public static final String XML_COMMENT_FINDING_EXPORTED_TO_AAVSO = "Exported to AAVSO (ObservationManager automatically generated comment)";


    // ------------------
    // Instance Variables ------------------------------------------------
    // ------------------

    // Magnitude of the observation was fainter-than.
    private boolean magnitudeFainterThan = false;

    // The magnitude of the observation.
    private float magnitude = Float.NaN;

    // This should be the latest date you see anywhere on the chart, entered as
    // YYMMDD.
    // If you do not see a date, use the first day of the copyright year (Ex:
    // "Copyright 2007" would be 070101).
    private String chartDate = null;

    // The label of the first comparison star used. Could be the magnitude label on
    // the chart, and auid,
    // or something else.
    private final List<String> comparismStars = new ArrayList<>(4);

    // Flag indicating whether this finding was already exported into the AAVSO
    // format
    private boolean alreadyExportedToAAVSOformat = false;

    // Comments

    /* B: Sky is bright, moon, twilight, light pollution, aurorae. */
    private boolean brightSky = false;

    /* U: Clouds, dust, smoke, haze, etc. */
    private boolean clouds = false;

    /* W: Poor seeing. */
    private boolean poorSeeing = false;

    /* L: Low in the sky, near horizon, in trees, obstructed view. */
    private boolean nearHorizion = false;

    /* D: Unusual Activity (fading, flare, bizarre behavior, etc.) */
    private boolean unusualActivity = false;

    /* Y: Outburst. */
    private boolean outburst = false;

    /* K: Non-AAVSO chart. */
    private boolean nonAAVSOchart = false;

    /* S: Comparison sequence problem. */
    private boolean comparismSequenceProblem = false;

    /* Z: Magnitude of star uncertain. */
    private boolean magnitudeUncertain = false;

    /* I: Identification of star uncertain. */
    private boolean starIdentificationUncertain = false;

    /* V: Faint star, near observing limit, only glimpsed. */
    private boolean faintStar = false;

    // ------------
    // Constructors ------------------------------------------------------
    // ------------

    public FindingVariableStar(Node findingElement) throws SchemaException {

        super(findingElement);

        Element finding = (Element) findingElement;
        this.setMagnitude(FindingVariableStarMapper.getMandatoryMagnitude(finding));
        this.setMagnitudeFainterThan(FindingVariableStarMapper.getOptionalMagnitudeFainterThan(finding));
        this.setMagnitudeUncertain(FindingVariableStarMapper.getOptionalMagnitudeUncertain(finding));
        this.setChartDate(FindingVariableStarMapper.getMandatoryChartDate(finding));
        this.setNonAAVSOchart(FindingVariableStarMapper.getOptionalNonAAVSOchart(finding));
        this.setComparismStars(FindingVariableStarMapper.getMandatoryCompStars(finding));
        this.setAlreadyExportedToAAVSO(FindingVariableStarMapper.getOptionalAlreadyExportedToAAVSO(finding));
        this.setBrightSky(FindingVariableStarMapper.getOptionalBrightSky(finding));
        this.setClouds(FindingVariableStarMapper.getOptionalCloudAttributes(finding));
        this.setComparismSequenceProblem(FindingVariableStarMapper.getOptionalComparismSequenceProblem(finding));
        this.setFaintStar(FindingVariableStarMapper.getOptionalFaintStar(finding));
        this.setNearHorizion(FindingVariableStarMapper.getOptionalNearHorizon(finding));
        this.setOutburst(FindingVariableStarMapper.getOptionalOutburst(finding));
        this.setPoorSeeing(FindingVariableStarMapper.getOptionalPoorSeeing(finding));
        this.setStarIdentificationUncertain(FindingVariableStarMapper.getOptionalStarIdentificationUncertain(finding));
        this.setUnusualActivity(FindingVariableStarMapper.extracted6(finding));

    }

    public FindingVariableStar(float magnitude, List<String> comparismStars, String chartDate) {

        super("");

        this.setMagnitude(magnitude);
        this.setComparismStars(comparismStars);
        this.setChartDate(chartDate);

    }

    // -------------
    // SchemaElement -----------------------------------------------------
    // -------------

    /**
     * Returns a display name for this element.<br>
     * The method differs from the toString() method as toString() shows more
     * technical information about the element. Also the formating of toString() can
     * spread over several lines.<br>
     * This method returns a string (in one line) that can be used as displayname in
     * e.g. a UI dropdown box.
     * 
     * @return Returns a String with a one line display name
     */
    @Override
    public String getDisplayName() {

        String result = this.isMagnitudeFainterThan() ? "<" : "";
        result = result + this.getMagnitude();

        return result;

    }

    // ------
    // Object ------------------------------------------------------------
    // ------

    /**
     * Overwrittes toString() method from java.lang.Object.<br>
     * Returns the field values of this FindingVariableStar.
     * 
     * @return This FindingVariableStar field values
     * @see java.lang.Object
     */
    @Override
    public String toString() {

        StringBuilder buffer = new StringBuilder();
        buffer.append("FindingVariableStar: Magnitude=");
        buffer.append(this.getMagnitude());
        buffer.append(" Fainter than=");
        buffer.append(this.magnitudeFainterThan);
        buffer.append(" Chart date=");
        buffer.append(this.getChartDate());
        buffer.append(" Comparism stars=");
        ListIterator<String> iterator = this.comparismStars.listIterator();
        while (iterator.hasNext()) {
            buffer.append(iterator.next());
            if (iterator.hasNext()) {
                buffer.append(", ");
            }
        }
        buffer.append(" Bright sky=");
        buffer.append(this.isBrightSky());
        buffer.append(" Clouds=");
        buffer.append(this.isClouds());
        buffer.append(" Poor seeing=");
        buffer.append(this.isPoorSeeing());
        buffer.append(" Near Horizon=");
        buffer.append(this.isNearHorizion());
        buffer.append(" Unusual activity=");
        buffer.append(this.isUnusualActivity());
        buffer.append(" Outburst=");
        buffer.append(this.isOutburst());
        buffer.append(" non-AAVSO chart=");
        buffer.append(this.isNonAAVSOchart());
        buffer.append(" Comparism sequence problem=");
        buffer.append(this.isComparismSequenceProblem());
        buffer.append(" Magintude uncertain=");
        buffer.append(this.isMagnitudeUncertain());
        buffer.append(" Star identification uncertain=");
        buffer.append(this.isStarIdentificationUncertain());
        buffer.append(" Faint star=");
        buffer.append(this.isFaintStar());

        return buffer.toString();

    }

    /**
     * Overwrittes equals(Object) method from java.lang.Object.<br>
     * Checks if this FindingVariableStar and the given Object are equal. Two
     * FindingVariableStar are equal if both return the same string from their
     * toString() method and their XSI type is equal.<br>
     * 
     * @param obj The Object to compare this FindingVariableStar with.
     * @return <code>true</code> if both Objects are instances from class
     *         FindingVariableStar, both XSI types are equal and their fields
     *         contain the same values. (Can be checked with calling and comparing
     *         both objects toString() method)
     * @see java.lang.Object
     */
    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof FindingVariableStar)) {
            return false;
        }

        // As we overwritte the toString() method and access all fields there,
        // two FindingVariableStar are equal, if both objects return the same string
        // from their toString() method.
        return (this.toString().equals(obj.toString()))
                && (this.getXSIType().equals(((FindingVariableStar) obj).getXSIType()));

    }

    // ------------------------
    // IExtendableSchemaElement -----------------------------------------------
    // ------------------------

    // ------------------------------------------------------------------------
    /**
     * Returns the XML schema instance type of the implementation.<br>
     * Example:<br>
     * <target xsi:type="myOwnTarget"><br>
     * </target><br>
     * 
     * @return The xsi:type value of this implementation
     */
    @Override
    public String getXSIType() {

        return FindingVariableStar.XML_XSI_TYPE_VALUE;

    }

    // -------
    // Finding -----------------------------------------------------------
    // -------

    /**
     * Adds this FindingVariableStar to an given parent XML DOM Element. The
     * FindingVariableStar Element will be set as a child element of the passed
     * Element.
     * 
     * @param parent The parent element for this FindingVariableStar
     * @return Returns the Element given as parameter with this FindingVariableStar
     *         as child Element.<br>
     *         Might return <code>null</code> if parent was <code>null</code>.
     * @see org.w3c.dom.Element
     */
    @Override
    public Element addToXmlElement(Element parent) {

        Document ownerDoc = parent.getOwnerDocument();

        Element e_Finding = super.createXmlFindingElement(parent);

        // ----- Set XSI:Type
        e_Finding.setAttribute(IExtendableSchemaElement.XML_XSI_TYPE, FindingVariableStar.XML_XSI_TYPE_VALUE);

        // ----- Set Comments (do this at the very beginning to possibly increase speed
        // during read
        if (this.alreadyExportedToAAVSOformat) {
            Comment comment = ownerDoc.createComment(FindingVariableStar.XML_COMMENT_FINDING_EXPORTED_TO_AAVSO);
            e_Finding.appendChild(comment);
        }

        // ----- Set mandatory elements

        addMagnitudeData(ownerDoc, e_Finding);

        addComparismStars(ownerDoc, e_Finding);
        addChartData(ownerDoc, e_Finding);

        // ----- Set optional elements

        if (this.isBrightSky()) {
            e_Finding.setAttribute(XML_ELEMENT_FINDING_ATTRIBUTE_BRIGHTSKY, Boolean.toString(this.isBrightSky()));
        }

        if (this.isClouds()) {
            e_Finding.setAttribute(XML_ELEMENT_FINDING_ATTRIBUTE_CLOUDS, Boolean.toString(this.isClouds()));
        }

        if (this.isPoorSeeing()) {
            e_Finding.setAttribute(XML_ELEMENT_FINDING_ATTRIBUTE_POORSEEING, Boolean.toString(this.isPoorSeeing()));
        }

        if (this.isNearHorizion()) {
            e_Finding.setAttribute(XML_ELEMENT_FINDING_ATTRIBUTE_NEARHORIZON, Boolean.toString(this.isNearHorizion()));
        }

        if (this.isUnusualActivity()) {
            e_Finding.setAttribute(XML_ELEMENT_FINDING_ATTRIBUTE_UNUSUALACTIVITY,
                    Boolean.toString(this.isUnusualActivity()));
        }

        if (this.isOutburst()) {
            e_Finding.setAttribute(XML_ELEMENT_FINDING_ATTRIBUTE_OUTBURST, Boolean.toString(this.isOutburst()));
        }

        if (this.isComparismSequenceProblem()) {
            e_Finding.setAttribute(XML_ELEMENT_FINDING_ATTRIBUTE_COMPARISMSEQPROBLEM,
                    Boolean.toString(this.isComparismSequenceProblem()));
        }

        if (this.isStarIdentificationUncertain()) {
            e_Finding.setAttribute(XML_ELEMENT_FINDING_ATTRIBUTE_STARIDENTIFICATIONUNCERTAIN,
                    Boolean.toString(this.isStarIdentificationUncertain()));
        }

        if (this.isFaintStar()) {
            e_Finding.setAttribute(XML_ELEMENT_FINDING_ATTRIBUTE_FAINTSTAR, Boolean.toString(this.isFaintStar()));
        }

        parent.appendChild(e_Finding);

        return parent;

    }

    private void addMagnitudeData(Document ownerDoc, Element e_Finding) {
        // Observed magnitude with attributes for fainterThan and uncertain values
        Element e_visMag = ownerDoc.createElement(XML_ELEMENT_VISMAG);
        Node e_visMagText = ownerDoc.createTextNode(Float.toString(this.magnitude));
        e_visMag.appendChild(e_visMagText);

        if (this.isMagnitudeFainterThan()) {
            e_visMag.setAttribute(XML_ELEMENT_FINDING_ATTRIBUTE_FAINTERTHAN,
                    Boolean.toString(this.isMagnitudeFainterThan()));
        }

        if (this.isMagnitudeUncertain()) {
            e_visMag.setAttribute(XML_ELEMENT_FINDING_ATTRIBUTE_UNCERTAIN,
                    Boolean.toString(this.isMagnitudeUncertain()));
        }

        e_Finding.appendChild(e_visMag);
    }

    private void addChartData(Document ownerDoc, Element e_Finding) {
        // Chart ID
        Element e_chart = ownerDoc.createElement(XML_ELEMENT_CHARTID);
        Node e_chartText = ownerDoc.createTextNode(this.getChartDate());
        e_chart.appendChild(e_chartText);

        if (this.isNonAAVSOchart()) {
            e_chart.setAttribute(XML_ELEMENT_FINDING_ATTRIBUTE_NONAAVSOCHART, Boolean.toString(this.isNonAAVSOchart()));
        }

        e_Finding.appendChild(e_chart);
    }

    private void addComparismStars(Document ownerDoc, Element e_Finding) {
       
        for(String compStar : this.getComparismStars())  {
            addComparismStar(ownerDoc, e_Finding, compStar);
        }
    }

    private void addComparismStar(Document ownerDoc, Element e_Finding, String compStar) {
        Element e_currentCompStar;
        Node e_currentCompStarText;
        e_currentCompStar = ownerDoc.createElement(XML_ELEMENT_COMPARISMSTAR);
        e_currentCompStarText = ownerDoc.createTextNode(compStar);
        e_currentCompStar.appendChild(e_currentCompStarText);

        e_Finding.appendChild(e_currentCompStar);
    }

    // -------------
    // PublicMethods ----------------------------------------------------------
    // -------------

    // ------------------------------------------------------------------------
    /**
     * Returns true if magnitude value is the maximum seen magnitude during
     * observation, and still the star itself couldn't be seen
     * 
     * @return true if magnitude value is the maximum seen magnitude during
     *         observation, and still the star itself couldn't be seen
     */
    public boolean isMagnitudeFainterThan() {

        return this.magnitudeFainterThan;

    }

    // ------------------------------------------------------------------------
    /**
     * Returns the seen magnitude of the star
     * 
     * @return true the magnitude as float value
     */
    public float getMagnitude() {

        return this.magnitude;

    }

    // ------------------------------------------------------------------------
    /**
     * Returns a list of comparism stars which were used to determin the stars
     * magnitude
     * 
     * @return java.lang.List containing java.lang.String objects representing the
     *         comparism star
     */
    public List<String> getComparismStars() {

        return new ArrayList<>(this.comparismStars);

    }

    // ------------------------------------------------------------------------
    /**
     * Returns the name or ID of a chart which was used to identify the star, and
     * the comparism stars
     * 
     * @return a String with the chart name or ID
     */
    public String getChartDate() {

        return this.chartDate;

    }

    // ------------------------------------------------------------------------
    /**
     * Returns <code>true</code> if this finding was already exported to an AAVSO
     * file before.<br>
     * 
     * @return a boolean with the export status
     */
    public boolean isAlreadyExportedToAAVSO() {

        return this.alreadyExportedToAAVSOformat;

    }

    // ------------------------------------------------------------------------
    /**
     * Sets the export status of this finding.<br>
     * 
     * @param exported A boolean value indicating whether this finding was already
     *                 exported to an AAVSO file before
     */
    public void setAlreadyExportedToAAVSO(boolean exported) {

        this.alreadyExportedToAAVSOformat = exported;

    }

    // ------------------------------------------------------------------------
    /**
     * Sets the chart date, which can be any string to identify the chart.<br>
     * This should be the latest date you see anywhere on the chart, entered as
     * YYMMDD.<br>
     * If you do not see a date, use the first day of the copyright year (Ex:
     * "Copyright 2007" would be 070101).
     * 
     * @param chartDate A date string for chart identification
     * @thorows IllegalArgumentException In case the given chart date was
     *          <code>null</code> or an empty string
     */
    public void setChartDate(String chartDate) throws IllegalArgumentException {

        if ((chartDate == null) || ("".equals(chartDate.trim()))) {
            throw new IllegalArgumentException("Chart date cannot be null or empty string.");
        }

        this.chartDate = chartDate;

    }

    // ------------------------------------------------------------------------
    /**
     * Sets the magnitude of the variable star.<br>
     * 
     * @param magnitude The observed magnitude of the variable star
     * @throws IllegalArgumentException in case the given magnitude is Float.NaN
     */
    public void setMagnitude(float magnitude) throws IllegalArgumentException {

        if (Float.isNaN(magnitude)) {
            throw new IllegalArgumentException("Magnitude cannot be Float.NaN.");
        }

        this.magnitude = magnitude;

    }

    // ------------------------------------------------------------------------
    /**
     * Set or unset if magnitude value is the maximum seen magnitude during
     * observation, and still the star itself couldn't be seen Default value is
     * <code>false</code>.
     */
    public void setMagnitudeFainterThan(boolean fainterThan) {

        this.magnitudeFainterThan = fainterThan;

    }

    // ------------------------------------------------------------------------
    /**
     * Sets a new List of comparism stars to this finding.<br>
     * The given list must at least contain one entry. The list entries must be of
     * type java.lang.String. The old List of comparism star magnitudes will be
     * overwritten. If you want to add one or more comparism star magnitudes to the
     * existing list use addAllComparismStarMagnitudes(java.util.List) or
     * addComparismStarMagnitude(java.lang.String) instead.
     * 
     * @param comparismStars The new List of comparism star of the finding
     * @throws IllegalArgumentException if the given new List doesn't contain at
     *                                  least one entry
     */
    public void setComparismStars(List<String> comparismStars) throws IllegalArgumentException {

        // List is null or empty
        if ((comparismStars == null) || comparismStars.isEmpty()) {
            throw new IllegalArgumentException("New comparism star magnitude list cannot be NULL or empty. ");
        }

        // At least one/first entry need to be a Float and must not be Float.NaN
        Object firstEntry = comparismStars.get(0);
        if (!(firstEntry instanceof String)) {
            throw new IllegalArgumentException("New comparism star magnitude list must contain at least one String. ");
        }

        // Delete all current entries
        this.comparismStars.clear();

        // Add values
        this.addAllComparismStars(comparismStars);

    }

    // ------------------------------------------------------------------------
    /**
     * Adds a single comparism star to this finding.<br>
     * 
     * @param comparismStar A new comparism star which will be addded to the List of
     *                      comparism star
     */
    private void addComparismStar(String comparismStar) {

        // Check on NULL and Float.NaN
        if ((comparismStar == null) || ("".equals(comparismStar.trim()))) {
            return;
        }

        this.comparismStars.add(comparismStar.trim());

    }

    // ------------------------------------------------------------------------
    /**
     * Adds a List of comparism stars to this finding.<br>
     * The old List of comparism stars will be extended by the new List of comparism
     * stars.
     * 
     * @param comparismStars A List of comparism star which will be added to the
     *                       existing List of comparism star which is stored in the
     *                       finding
     */
    private void addAllComparismStars(List<String> comparismStars) {

        // List is null or empty
        if ((comparismStars == null) || comparismStars.isEmpty()) {
            return;
        }

        // Add each entry seperatly to ensure it is a Float and not Float.NaN
        ListIterator<String> iterator = comparismStars.listIterator();
        String current = null;
        while (iterator.hasNext()) {
            current = iterator.next();
            current = current.trim();
            this.addComparismStar(current);
        }

    }

    // ------------------------------------------------------------------------
    /**
     * Returns true if the sky was bright or moon, twilight, light pollution or
     * aurorae occured
     * 
     * @return true if the sky was bright or moon, twilight, light pollution or
     *         aurorae occured
     */
    public boolean isBrightSky() {

        return brightSky;

    }

    // ------------------------------------------------------------------------
    /**
     * Set or unset if was bright or moon, twilight, light pollution or aurorae
     * occured. Default value is <code>false</code>.
     */
    public void setBrightSky(boolean brightSky) {

        this.brightSky = brightSky;

    }

    // ------------------------------------------------------------------------
    /**
     * Returns true if clouds, dust, smoke, or haze occured during observation
     * 
     * @return true if clouds, dust, smoke, or haze occured during observation
     */
    public boolean isClouds() {

        return clouds;

    }

    // ------------------------------------------------------------------------
    /**
     * Set or unset if clouds, dust, smoke, or haze occured during observation
     * Default value is <code>false</code>.
     */
    public void setClouds(boolean clouds) {

        this.clouds = clouds;

    }

    // ------------------------------------------------------------------------
    /**
     * Returns true if there was a comparison sequence problem
     * 
     * @return true if there was a comparison sequence problem
     */
    public boolean isComparismSequenceProblem() {

        return comparismSequenceProblem;

    }

    // ------------------------------------------------------------------------
    /**
     * Set or unset if there was a comparison sequence problem Default value is
     * <code>false</code>.
     */
    public void setComparismSequenceProblem(boolean comparismSequenceProblem) {

        this.comparismSequenceProblem = comparismSequenceProblem;

    }

    // ------------------------------------------------------------------------
    /**
     * Returns true if star was faint, near observing limit, only glimpsed.
     * 
     * @return true if star was faint, near observing limit, only glimpsed.
     */
    public boolean isFaintStar() {

        return faintStar;

    }

    // ------------------------------------------------------------------------
    /**
     * Set or unset if star was faint, near observing limit, only glimpsed. Default
     * value is <code>false</code>.
     */
    public void setFaintStar(boolean faintStar) {

        this.faintStar = faintStar;

    }

    // ------------------------------------------------------------------------
    /**
     * Returns true if magnitude of star was uncertain
     * 
     * @return true if magnitude of star was uncertain
     */
    public boolean isMagnitudeUncertain() {

        return magnitudeUncertain;

    }

    // ------------------------------------------------------------------------
    /**
     * Set or unset if magnitude of star was uncertain Default value is
     * <code>false</code>.
     */
    public void setMagnitudeUncertain(boolean magnitudeUncertain) {

        this.magnitudeUncertain = magnitudeUncertain;

    }

    // ------------------------------------------------------------------------
    /**
     * Returns true if star was near the horizon, low in the sky, in trees or the
     * view was obstructed
     * 
     * @return true if star was near the horizon, low in the sky, in trees or the
     *         view was obstructed
     */
    public boolean isNearHorizion() {

        return nearHorizion;

    }

    // ------------------------------------------------------------------------
    /**
     * Set or unset if star was near the horizon, low in the sky, in trees or the
     * view was obstructed Default value is <code>false</code>.
     */
    public void setNearHorizion(boolean nearHorizion) {

        this.nearHorizion = nearHorizion;

    }

    // ------------------------------------------------------------------------
    /**
     * Returns true if the given chart is not a AAVSO chart
     * 
     * @return true if the given chart is not a AAVSO chart
     */
    public boolean isNonAAVSOchart() {

        return nonAAVSOchart;

    }

    // ------------------------------------------------------------------------
    /**
     * Set or unsetif the given chart is not a AAVSO chart Default value is
     * <code>false</code>.
     */
    public void setNonAAVSOchart(boolean nonAAVSOchart) {

        this.nonAAVSOchart = nonAAVSOchart;

    }

    // ------------------------------------------------------------------------
    /**
     * Returns true in case of a star outburst
     * 
     * @return true in case of a star outburst
     */
    public boolean isOutburst() {

        return outburst;

    }

    // ------------------------------------------------------------------------
    /**
     * Set or unset in case of a star outburst Default value is <code>false</code>.
     */
    public void setOutburst(boolean outburst) {

        this.outburst = outburst;

    }

    // ------------------------------------------------------------------------
    /**
     * Returns true if seeing was poor
     * 
     * @return true if seeing was poor
     */
    public boolean isPoorSeeing() {

        return poorSeeing;

    }

    // ------------------------------------------------------------------------
    /**
     * Set or unset if seeing was poor Default value is <code>false</code>.
     */
    public void setPoorSeeing(boolean poorSeeing) {

        this.poorSeeing = poorSeeing;

    }

    // ------------------------------------------------------------------------
    /**
     * Returns true if identification of star was uncertain
     * 
     * @return true if identification of star was uncertain
     */
    public boolean isStarIdentificationUncertain() {

        return starIdentificationUncertain;

    }

    // ------------------------------------------------------------------------
    /**
     * Set or unset if identification of star was uncertain Default value is
     * <code>false</code>.
     */
    public void setStarIdentificationUncertain(boolean starIdentificationUncertain) {

        this.starIdentificationUncertain = starIdentificationUncertain;

    }

    // ------------------------------------------------------------------------
    /**
     * Returns true star showed an unusual activity during observation like fading,
     * flare, etc.
     * 
     * @return true star showed an unusual activity during observation like fading,
     *         flare, etc.
     */
    public boolean isUnusualActivity() {

        return unusualActivity;

    }

    // ------------------------------------------------------------------------
    /**
     * Set or unset if star showed an unusual activity during observation like
     * fading, flare, etc. Default value is <code>false</code>.
     */
    public void setUnusualActivity(boolean unusualActivity) {

        this.unusualActivity = unusualActivity;

    }

}