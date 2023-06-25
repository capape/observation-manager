package de.lehmannet.om.ui.extension.variableStars.dialog;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.lehmannet.om.ICloneable;
import de.lehmannet.om.IObservation;
import de.lehmannet.om.IObserver;
import de.lehmannet.om.Observer;
import de.lehmannet.om.extension.variableStars.FindingVariableStar;
import de.lehmannet.om.extension.variableStars.TargetVariableStar;
import de.lehmannet.om.ui.comparator.ObservationComparator;
import de.lehmannet.om.ui.dialog.OMDialog;
import de.lehmannet.om.ui.util.ConfigKey;
import de.lehmannet.om.ui.util.IConfiguration;
import de.lehmannet.om.ui.util.UserInterfaceHelper;
import de.lehmannet.om.util.DateConverter;
import de.lehmannet.om.util.DateManager;
import de.lehmannet.om.util.DateManagerImpl;

public class VariableStarChartDialog extends OMDialog implements PropertyChangeListener, ComponentListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(VariableStarChartDialog.class);
    private static final long serialVersionUID = 3386387945098447550L;
    private static final int MIN_WIDTH = 700;
    private static final int MIN_HEIGHT = 500;

    public VariableStarChartDialog(JFrame om, UserInterfaceHelper uiHelper, IConfiguration configuration,
            IObservation[] observations, Map<IObserver, Color> colorMap) {

        super(om);

        ResourceBundle bundle = ResourceBundle.getBundle("de.lehmannet.om.ui.extension.variableStars.VariableStar",
                Locale.getDefault());
        this.setTitle(bundle.getString("chart.title") + " " + observations[0].getTarget().getName());

        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setModal(true);

        // Sort observations by date
        TreeSet<IObservation> sortedObervations = new TreeSet<>(new ObservationComparator(true));
        sortedObervations.addAll(Arrays.asList(observations));

        MagnitudeDiagramm diagramm = new MagnitudeDiagramm(configuration, uiHelper, sortedObervations, colorMap);
        diagramm.addPropertyChangeListener("exit", this);
        this.setContentPane(diagramm);
        this.setSize(VariableStarChartDialog.serialVersionUID, 830, 600);
        // Check resize and make sure, minimum size is respected.
        // As we're supporting 1.4 we need to go this way.
        // With 1.6 we could use setMinimumSize()
        this.addComponentListener(this);

        this.setLocationRelativeTo(om);

        this.setVisible(true);

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

        // As we only listen to the exit property...no need to check anything in here.
        // Just close the dialog
        this.dispose();

    }

    @Override
    public void componentResized(ComponentEvent e) {

        int width = getWidth();
        int height = getHeight();

        // we check if either the width
        // or the height are below minimum
        boolean resize = false;
        if (width < MIN_WIDTH) {
            resize = true;
            width = MIN_WIDTH;
        }
        if (height < MIN_HEIGHT) {
            resize = true;
            height = MIN_HEIGHT;
        }
        if (resize) {
            setSize(width, height);
        }

    }

    @Override
    public void componentMoved(ComponentEvent e) {
        // Do nothing
    }

    @Override
    public void componentShown(ComponentEvent e) {
        // Do nothing
    }

    @Override
    public void componentHidden(ComponentEvent e) {
        // Do nothing
    }

}

class MagnitudeDiagramm extends JPanel implements MouseListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(MagnitudeDiagramm.class);

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private transient final DateManager dateManager = new DateManagerImpl();

    private transient final ResourceBundle bundle = ResourceBundle
            .getBundle("de.lehmannet.om.ui.extension.variableStars.VariableStar", Locale.getDefault());

    private static final int BORDER_TOP = 120;
    private static final int BORDER_BOTTOM = 120;
    private static final int BORDER_LEFT = 60;
    private static final int BORDER_RIGHT = 60;

    private static final int CIRCLE_DIAMETER = 6;

    // The data to be drawn
    private transient SortedSet<IObservation> observations = null;

    // The observer / color assignment
    private transient Map<IObserver, Color> colorMap = null;

    // The 2D Graphics object on which we'll paint
    private transient Graphics2D g2d = null;

    // Segment size of x and y axis (label steps on axis)
    private float xAxisSegmentSize = 0;
    private float yAxisSegmentSize = 0;

    // Start value of axis (y-Axis starts with highest number in origin, as
    // magnitude value increases with lesser brightness
    private float minXValue = 0.0f;
    private float maxYValue = 0.0f;

    // Transform values for x and y (Offset for origin, caused by borders, titles,
    // etc.)
    private float xTransformValue = 0;
    private float yTransformValue = 0;

    // Position where a popup should occur (NULL means no popup to paint)
    private Integer xPopup = null;
    private Integer yPopup = null;

    // In case the axis values delta (max-min) is too huge, use divisor to reduce
    // segment number
    private float xDivisor = 1.0f;
    private float yDivisor = 1.0f;

    // All spots which indicate a observation in the diagramm
    private DataSpot[] dataSpots = null;

    private transient final IConfiguration configuration;
    private transient final UserInterfaceHelper uiHelper;

    public MagnitudeDiagramm(IConfiguration configuration, UserInterfaceHelper uiHelper,
            SortedSet<IObservation> observations, Map<IObserver, Color> colorMap) {

        this.observations = this.getObservations(observations, colorMap);
        this.dataSpots = new DataSpot[observations.size()];
        this.colorMap = colorMap;

        this.configuration = configuration;
        this.uiHelper = uiHelper;

        // if (this.om.isNightVisionEnabled()) {
        // this.setBackground(new Color(255, 175, 175));
        // } else {
        // this.setBackground(Color.white);
        // }

        this.addMouseListener(this);

    }

    @Override
    public void paint(Graphics g) {

        this.paintComponent(g);
        this.g2d = (Graphics2D) g;

        this.paintTitle();
        this.paintColorCode();
        this.paintAxis();
        this.paintData();
        this.paintPopup();

    }

    @Override
    public void mouseClicked(MouseEvent e) {

        // Check only button
        if (e.getButton() == MouseEvent.BUTTON3) {
            new ChartPopupHandler(e.getX(), e.getY(), this);
        }

    }

    @Override
    public void mouseEntered(MouseEvent e) {

        // do nothing

    }

    @Override
    public void mouseExited(MouseEvent e) {

        // do nothing

    }

    @Override
    public void mousePressed(MouseEvent e) {

        // Convert coordinates
        float x = e.getX() - this.xTransformValue;
        float y = e.getY() - this.yTransformValue;

        this.xPopup = Math.round(x);
        this.yPopup = Math.round(y);

        this.updateUI();

    }

    @Override
    public void mouseReleased(MouseEvent e) {

        this.xPopup = null;
        this.yPopup = null;

        this.updateUI();

    }

    private void paintTitle() {

        // Set color for painting
        g2d.setPaint(Color.black);

        // Get informations
        TargetVariableStar variableStar = (TargetVariableStar) ((IObservation) this.observations.first()).getTarget();
        String starName = variableStar.getName();

        String magFromToLabel = this.bundle.getString("chart.label.magnitudeMinMax") + ": ";
        String magFromTo = null;
        if (!(Float.isNaN(variableStar.getMagnitudeApparent())) && !(Float.isNaN(variableStar.getMaxApparentMag()))) {
            magFromTo = variableStar.getMagnitudeApparent() + " - " + variableStar.getMaxApparentMag();
        }

        String origin = null;
        String originLabel = this.bundle.getString("chart.label.origin") + ": ";
        if (variableStar.getDatasource() != null) {
            origin = variableStar.getDatasource();
        } else {
            origin = variableStar.getObserver().getDisplayName();
        }

        String period = null;
        String periodLabel = this.bundle.getString("chart.label.period") + ": ";
        if (!Float.isNaN(variableStar.getPeriod())) {
            period = "" + variableStar.getPeriod();
        }

        String type = null;
        String typeLabel = this.bundle.getString("chart.label.type") + ": ";
        if (variableStar.getType() != null) {
            type = variableStar.getType();
        }

        String fromDateLabel = this.bundle.getString("chart.label.from");
        String toDateLabel = this.bundle.getString("chart.label.to");
        String fromDateJD = "JD: "
                + DateConverter.toJulianDate(((IObservation) (this.observations.first())).getBegin().toZonedDateTime());
        String fromDate = this.dateManager.zonedDateTimeToStringWithSeconds(
                ((IObservation) (this.observations.first())).getBegin().toZonedDateTime());
        String toDateJD = ""
                + DateConverter.toJulianDate(((IObservation) (this.observations.last())).getBegin().toZonedDateTime());
        String toDate = this.dateManager.zonedDateTimeToStringWithSeconds(
                ((IObservation) (this.observations.last())).getBegin().toZonedDateTime());

        // ---- Print large box as border
        this.g2d.drawRect(0, 0, (int) this.getSize().getWidth(), BORDER_TOP - 10);

        // ---- Print star name

        // Create font for star name
        Font starNameFont = new Font("Arial", Font.BOLD, 20);
        FontMetrics starNameFontMetric = this.getFontMetrics(starNameFont);
        g2d.setFont(starNameFont);

        // Get metrics
        int valueWidth = starNameFontMetric.stringWidth(starName);
        int valueHeight = starNameFontMetric.getHeight();

        // Paint box
        this.g2d.drawRect(0, 0, valueWidth + 10, valueHeight + 10);

        // Paint text
        this.g2d.drawString(starName, 5, valueHeight);

        // ---- Print From - To Date

        Font labelFont = new Font("Arial", Font.BOLD, 14);
        FontMetrics labelFontMetric = this.getFontMetrics(labelFont);
        g2d.setFont(labelFont);

        Font valueFont = new Font("Arial", Font.ITALIC, 14);
        FontMetrics valueFontMetric = this.getFontMetrics(valueFont);

        // Get metrics
        int valueWidthFromLabel = labelFontMetric.stringWidth(fromDateLabel);
        int valueHeightFromLabel = labelFontMetric.getHeight();

        this.g2d.drawString(fromDateLabel, valueWidth + 15, valueHeightFromLabel);

        // Get max length of FromDate values
        g2d.setFont(valueFont);
        int maxFromDateLength = Math.max(valueFontMetric.stringWidth(fromDateJD),
                valueFontMetric.stringWidth(fromDate));
        this.g2d.drawString(fromDateJD, valueWidth + 15 + valueWidthFromLabel + 10, valueHeightFromLabel);
        this.g2d.drawString(fromDate, valueWidth + 15 + valueWidthFromLabel + 10, valueHeightFromLabel * 2);

        g2d.setFont(labelFont);
        int valueWidthToLabel = labelFontMetric.stringWidth(toDateLabel);
        this.g2d.drawString(toDateLabel, valueWidth + 15 + valueWidthFromLabel + 10 + maxFromDateLength + 20,
                valueHeightFromLabel);

        g2d.setFont(valueFont);
        this.g2d.drawString(toDateJD,
                valueWidth + 15 + valueWidthFromLabel + 10 + maxFromDateLength + 20 + valueWidthToLabel + 10,
                valueHeightFromLabel);
        this.g2d.drawString(toDate,
                valueWidth + 15 + valueWidthFromLabel + 10 + maxFromDateLength + 20 + valueWidthToLabel + 10,
                valueHeightFromLabel * 2);

        // ---- Print # of Observations
        g2d.setFont(labelFont);
        String noOfObservationsLabel = this.bundle.getString("chart.label.observationsNumber") + ": ";
        int valueWidthNoOfObservation = labelFontMetric.stringWidth(noOfObservationsLabel);
        this.g2d.drawString(noOfObservationsLabel, valueWidth + 15 + valueWidthFromLabel + 10 + maxFromDateLength + 20
                + valueWidthToLabel + 10 + maxFromDateLength + 20, valueHeightFromLabel);

        g2d.setFont(valueFont);
        String noOfObservation = "" + this.observations.size();
        this.g2d.drawString(noOfObservation, valueWidth + 15 + valueWidthFromLabel + 10 + maxFromDateLength + 20
                + valueWidthToLabel + 10 + maxFromDateLength + 20 + valueWidthNoOfObservation + 10,
                valueHeightFromLabel);

        // ---- Print star data
        int xBorder = 5;
        int currentYPos = valueHeight;
        valueHeight = valueFontMetric.getHeight();
        int counter = 1;
        int xValuePos = Math.max(labelFontMetric.stringWidth(originLabel), labelFontMetric.stringWidth(magFromToLabel));
        xValuePos = Math.max(xValuePos, labelFontMetric.stringWidth(periodLabel));
        xValuePos = Math.max(xValuePos, labelFontMetric.stringWidth(typeLabel));
        xValuePos = xValuePos + 10 + xBorder;

        // ---- Print Origin
        if (origin != null) {
            g2d.setFont(labelFont);
            valueWidth = labelFontMetric.stringWidth(originLabel);
            this.g2d.drawString(originLabel, xBorder, currentYPos + ((valueHeight + 10) * counter));

            g2d.setFont(valueFont);
            this.g2d.drawString(origin, xValuePos, currentYPos + ((valueHeight + 10) * counter));

            counter++;
        }

        // ---- Print Magnitude
        if (magFromTo != null) {
            g2d.setFont(labelFont);
            valueWidth = labelFontMetric.stringWidth(magFromToLabel);
            this.g2d.drawString(magFromToLabel, xBorder, currentYPos + ((valueHeight * counter) + 10));

            g2d.setFont(valueFont);
            this.g2d.drawString(magFromTo, xValuePos, currentYPos + ((valueHeight * counter) + 10));

            counter++;
        }

        // ---- Print Periode
        if (period != null) {
            g2d.setFont(labelFont);
            valueWidth = labelFontMetric.stringWidth(periodLabel);
            this.g2d.drawString(periodLabel, xBorder, currentYPos + ((valueHeight * counter) + 10));

            g2d.setFont(valueFont);
            this.g2d.drawString(period, xValuePos, currentYPos + ((valueHeight * counter) + 10));

            counter++;
        }

        // ---- Print Type
        if (type != null) {
            g2d.setFont(labelFont);
            valueWidth = labelFontMetric.stringWidth(typeLabel);
            this.g2d.drawString(typeLabel, xBorder, currentYPos + ((valueHeight * counter) + 10));

            g2d.setFont(valueFont);
            this.g2d.drawString(type, xValuePos, currentYPos + ((valueHeight * counter) + 10));

            counter++;
        }

    }

    private void paintPopup() {

        if ((this.xPopup == null) || (this.yPopup == null)) {
            return; // Don't show popup
        }

        for (DataSpot dataSpot : this.dataSpots) {
            if (dataSpot.contains(this.xPopup, this.yPopup)) { // If click was inside
                // spot, paint popup

                // Create popup text and prepare font
                Font valueFont = new Font("Arial", Font.BOLD, 12);
                FontMetrics valueFontMetric = this.getFontMetrics(valueFont);
                g2d.setFont(valueFont);

                String sign = dataSpot.getResult().isMagnitudeFainterThan() ? "<" : "";
                String[] info = new String[] { (sign + dataSpot.getResult().getMagnitude() + "mag"),
                        ("JD: " + DateConverter.toJulianDate(dataSpot.getObservation().getBegin().toZonedDateTime())),
                        (this.bundle.getString("chart.popup.date") + ": "
                                + this.dateManager.zonedDateTimeToStringWithHour(
                                        dataSpot.getObservation().getBegin().toZonedDateTime())) };

                // Get largest popup text
                String maxInfoString = "";
                for (String s : info) {
                    maxInfoString = maxInfoString.length() < s.length() ? s : maxInfoString;
                }

                // Get metrics
                int valueWidth = valueFontMetric.stringWidth(maxInfoString);
                int valueHeight = valueFontMetric.getHeight();

                // Set color for painting
                g2d.setPaint(Color.BLUE);

                int width = valueWidth + 8;
                if ((Math.round(this.xPopup + dataSpot.width * 2) + width + 4) >= this.getWidth()) { // Popup doesn't
                                                                                                     // fit into frame
                    // Paint popup
                    this.g2d.drawRect(Math.round(this.xPopup - (dataSpot.width * 2) - width), this.yPopup + 2, width,
                            valueHeight * info.length + (valueHeight / 4 * info.length));

                    // Paint value into popup
                    for (int j = 0; j < info.length; j++) {
                        this.g2d.drawString(info[j], this.xPopup - (dataSpot.width * 2) - width + 4,
                                this.yPopup + ((j + 1) * valueHeight) + (valueHeight / 4 * j));
                    }
                } else {
                    // Paint popup
                    this.g2d.drawRect(Math.round(this.xPopup + dataSpot.width * 2), this.yPopup + 2, width,
                            valueHeight * info.length + (valueHeight / 4 * info.length));

                    // Paint value into popup
                    for (int j = 0; j < info.length; j++) {
                        this.g2d.drawString(info[j], this.xPopup + dataSpot.width * 2 + 4,
                                this.yPopup + ((j + 1) * valueHeight) + (valueHeight / 4 * j));
                    }
                }

                return;
            }
        }

    }

    private void paintData() {

        // We need to create dataSpots again with each repaint, as canvas/frame size
        // might change
        float currentValueXPos = 0;
        float currentValueYPos = 0;
        int i = 0;
        Iterator<IObservation> iterator = this.observations.iterator();
        IObservation current = null;
        while (iterator.hasNext()) {
            current = iterator.next();

            // Set color for painting
            g2d.setPaint((Color) this.colorMap.get(current.getObserver()));

            currentValueXPos = (float) (((DateConverter.toJulianDate(current.getBegin().toZonedDateTime())
                    - this.minXValue) / this.xDivisor) * this.xAxisSegmentSize) - (CIRCLE_DIAMETER / 2);
            currentValueYPos = (((this.maxYValue - ((FindingVariableStar) (current.getResults().get(0))).getMagnitude())
                    / this.yDivisor) * -this.yAxisSegmentSize * 10) - (CIRCLE_DIAMETER / 2);

            this.dataSpots[i] = new DataSpot(currentValueXPos, currentValueYPos, CIRCLE_DIAMETER, CIRCLE_DIAMETER,
                    current);
            g2d.draw(this.dataSpots[i]);
            if (!this.dataSpots[i].getResult().isMagnitudeFainterThan()) {
                g2d.fill(this.dataSpots[i]);
            }
            i++;
        }

    }

    private void paintColorCode() {

        String currentObserverLabel = "";
        Color currentColor = null;
        IObserver currentObserver = null;
        int gap = 25; // Between spot/Observer pairs
        int rowNo = 1; // Current row number

        // Create popup text and prepare font
        Font labelFont = new Font("Arial", Font.PLAIN, 12);
        FontMetrics labelFontMetric = this.getFontMetrics(labelFont);
        g2d.setFont(labelFont);

        int rowGap = labelFontMetric.getHeight() + 5; // Between rows

        int currentX = BORDER_LEFT + gap;

        for (Object o : this.colorMap.keySet()) {
            currentObserver = (IObserver) o;
            currentColor = (Color) this.colorMap.get(currentObserver);

            currentObserverLabel = currentObserver.getDisplayName();
            if ((currentObserver.getUsernameForAccount(Observer.ACCOUNT_AAVSO) != null)
                    && !("".equals(currentObserver.getUsernameForAccount(Observer.ACCOUNT_AAVSO).trim()))) {
                currentObserverLabel = currentObserverLabel + " ("
                        + currentObserver.getUsernameForAccount(Observer.ACCOUNT_AAVSO) + ")";
            }

            // Check if this line should start (make a forecast here)
            if ((currentX + CIRCLE_DIAMETER + 15 + labelFontMetric.stringWidth(currentObserverLabel)) >= this.getSize()
                    .getWidth() - BORDER_RIGHT) {
                rowNo++;
                currentX = BORDER_LEFT + gap;
            }

            // Set color for painting spot
            g2d.setPaint(currentColor);

            // Paint spot
            Shape spot = new Ellipse2D.Float(currentX,
                    (int) (this.getSize().getHeight() - (BORDER_BOTTOM / 1.4) + (rowGap * rowNo)), CIRCLE_DIAMETER,
                    CIRCLE_DIAMETER);
            g2d.draw(spot);
            g2d.fill(spot);

            currentX = currentX + 15; // Space between spot and label

            // Set color for painting label
            g2d.setPaint(Color.BLACK);

            // Paint observer name
            double drawY = this.getSize().getHeight() - BORDER_BOTTOM / 1.4d + rowGap * rowNo
                    + labelFontMetric.getHeight() / 2d;
            this.g2d.drawString(currentObserverLabel, currentX, (int) drawY);

            currentX = currentX + labelFontMetric.stringWidth(currentObserverLabel) + gap;
        }

    }

    private void paintAxis() {

        double width = this.getSize().getWidth();
        double height = this.getSize().getHeight();

        // Set axis origin
        this.xTransformValue = BORDER_LEFT;
        this.yTransformValue = (int) Math.round(height - BORDER_BOTTOM); // Use BORDER_BOTTOM as y axis counts ascending
                                                                         // downwards
        g2d.translate(xTransformValue, yTransformValue);

        // Set color for painting
        g2d.setColor(Color.black);

        // Calculate length of x and y axis
        int yAxisLength = (int) (height - (BORDER_TOP + BORDER_BOTTOM));
        int xAxisLength = (int) (width - (BORDER_LEFT + BORDER_RIGHT));

        // ----------- x - axis

        // Draw x axis
        g2d.drawLine(0, 0, xAxisLength, 0);

        // Calculate size if x axis segments // @todo This will fail on small xAxis
        this.minXValue = this.getMinX();
        float maxXValue = this.getMaxX();
        int xAxisSegmentNumber = (int) (maxXValue - minXValue) > 0 ? (int) (maxXValue - minXValue) : 1;
        if (xAxisSegmentNumber > 10) { // Too many segments
            this.xDivisor = (float) (xAxisSegmentNumber / 10.0);
            xAxisSegmentNumber = 10;
        }
        this.xAxisSegmentSize = xAxisLength / (float) xAxisSegmentNumber;

        // Draw x axis segments
        int lengthOfSegmentLines = yAxisLength / 50; // Make sure the small segment lines adopt to window size, too
        for (int x = 1; x <= xAxisSegmentNumber; x++) {
            g2d.drawLine(Math.round(xAxisSegmentSize * x), 0 - (lengthOfSegmentLines / 2),
                    Math.round(xAxisSegmentSize * x), (lengthOfSegmentLines / 2));
        }

        // Draw x axis labels (values)

        // Get width and height of (max) x axis label
        Font labelFont = new Font("Arial", Font.PLAIN, 12);
        FontMetrics labelFontMetric = this.getFontMetrics(labelFont);
        g2d.setFont(labelFont);

        DecimalFormat formatX = new DecimalFormat("0.0");
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        formatX.setDecimalFormatSymbols(dfs);

        int labelWidth = labelFontMetric.stringWidth(formatX.format(maxXValue));
        int labelHeight = labelFontMetric.getHeight();

        // Find out if we can draw all labels (respective to their width)
        // or if we've to skip some labels
        int skipLabel = 1; // 1 = draw all labels, 2 = draw each second label, ...
        while (((labelWidth + 10) > (xAxisSegmentSize * skipLabel)) // Does label (+5 pixels space on each side) not fit
                                                                    // into segment?
                && (skipLabel <= this.observations.size()) // Not possible to draw labels
        ) {
            skipLabel++;
        }

        // Draw labels
        double currentLabel = minXValue * 10.0;
        for (int x = 0; x <= xAxisSegmentNumber; x++) {
            if (((float) x / skipLabel) % 1 == 0.0f) { // Make sure we draw only the labels that fit (respective to
                                                       // their width) on the axis
                g2d.drawString(formatX.format(currentLabel / 10.0), xAxisSegmentSize * x - labelWidth / 2f,
                        labelHeight + lengthOfSegmentLines);
            }
            currentLabel = currentLabel + (this.xDivisor * 10.0);
        }

        // Draw X axis label
        Font axisLabelFont = new Font("Arial", Font.BOLD, 14);
        FontMetrics axisLabelFontMetric = this.getFontMetrics(axisLabelFont);
        g2d.setFont(axisLabelFont);

        String xAxisLabel = this.bundle.getString("chart.label.xAxis");
        int axisLabelHeight = axisLabelFontMetric.getHeight();
        int axisLabelWidth = axisLabelFontMetric.stringWidth(xAxisLabel);

        g2d.drawString(xAxisLabel, xAxisLength / 2 - axisLabelWidth / 2,
                labelHeight + lengthOfSegmentLines + axisLabelHeight);

        // ----------- y - axis

        // Draw y axis
        g2d.drawLine(0, 0, 0, -yAxisLength); // -yAxisLength as down is ascending the y value

        // Calculate site of y axis segments @todo This will fail on small xAxis
        float minYValue = this.getMinY();
        this.maxYValue = this.getMaxY();
        int yAxisSegmentNumber = Math.round((maxYValue - minYValue) * 10) > 0 ? Math.round((maxYValue - minYValue) * 10)
                : 10; // draw in 0.1 mag steps
        if (yAxisSegmentNumber > 20) { // Too many segments
            this.yDivisor = (float) (yAxisSegmentNumber / 20.0);
            yAxisSegmentNumber = 20;
        }
        this.yAxisSegmentSize = yAxisLength / (float) yAxisSegmentNumber;

        // Draw y axis segments
        for (int x = 1; x <= yAxisSegmentNumber; x++) {
            g2d.drawLine(0 - lengthOfSegmentLines / 2, Math.round(-yAxisSegmentSize * x), lengthOfSegmentLines / 2,
                    Math.round(-yAxisSegmentSize * x));
        }

        // Draw y axis labels (values)

        DecimalFormat formatY = new DecimalFormat("0.0");
        formatY.setDecimalFormatSymbols(dfs);

        // Find out if we can draw all labels (respective to their height)
        // or if we've to skip some labels
        int skipYLabel = 1; // 1 = draw all labels, 2 = draw each second label, ...
        while (((labelHeight + 10) > (yAxisSegmentSize * skipYLabel)) // Does label (+5 pixels space on top and bottom)
                                                                      // not fit into segment?
                && (skipYLabel <= this.observations.size()) // Not possible to draw labels
        ) {
            skipYLabel++;
        }

        // Draw labels
        // The magic in here is the currentYLabel incremental which doesn't work exactly
        // if we add 0.1 in each step, due to javas unprecise
        // floating point operations
        float currentYLabel = maxYValue * 10;
        labelWidth = labelFontMetric.stringWidth(formatY.format(maxYValue));
        g2d.setFont(labelFont);
        for (int x = 0; x <= yAxisSegmentNumber; x++) {
            if (((float) x / skipYLabel) % 1 == 0.0f) { // Make sure we draw only the labels that fit (respective to
                                                        // their height) on the axis
                String param1 = formatY.format(currentYLabel / 10.0d);
                float param2 = 0.0f - Math.round(labelWidth * 1.3f);
                float param3 = -yAxisSegmentSize * x + labelHeight / 2.0f;
                g2d.drawString(param1, param2, param3);
            }
            currentYLabel = currentYLabel - this.yDivisor;
        }

        // Draw Y axis label
        g2d.setFont(axisLabelFont);

        String yAxisLabel = this.bundle.getString("chart.label.yAxis");
        int yAxisLabelCharHeight = axisLabelFontMetric.getHeight();
        int yAxisLabelHeight = (yAxisLabelCharHeight + 5) * (yAxisLabel.length() - 1);
        int yAxisLabelWidth = axisLabelFontMetric.stringWidth(yAxisLabel.substring(0, 1));
        for (int i = 0; i < yAxisLabel.length(); i++) {
            g2d.drawString("" + yAxisLabel.charAt(i), (int) (0 - (labelWidth * 1.7) - yAxisLabelWidth),
                    (-yAxisLength / 2 - yAxisLabelHeight / 2) + i * (yAxisLabelCharHeight + 5));
        }

    }

    private float getMaxX() {

        return (float) Math.ceil(
                DateConverter.toJulianDate(((IObservation) (this.observations.last())).getBegin().toZonedDateTime()));

    }

    private float getMinX() {

        return (float) Math.floor(
                DateConverter.toJulianDate(((IObservation) (this.observations.first())).getBegin().toZonedDateTime()));

    }

    private float getMaxY() {

        float max = Float.MIN_VALUE;
        Iterator<IObservation> iterator = this.observations.iterator();
        float mag = 0.0f;
        while (iterator.hasNext()) {
            mag = ((FindingVariableStar) ((iterator.next()).getResults().get(0))).getMagnitude();
            max = Math.max(mag, max);
        }

        return max;

    }

    private float getMinY() {

        float min = Float.MAX_VALUE;
        Iterator<IObservation> iterator = this.observations.iterator();
        float mag = 0.0f;
        while (iterator.hasNext()) {
            mag = ((FindingVariableStar) ((iterator.next()).getResults().get(0))).getMagnitude();
            min = Math.min(mag, min);
        }

        return min;

    }

    public void saveImage() {

        // Get save path
        JFileChooser chooser = new JFileChooser();

        String last = this.configuration.getConfig(ConfigKey.CONFIG_LASTDIR);
        if (!StringUtils.isBlank(last)) {
            File dir = FileSystems.getDefault().getPath(last).toFile();
            if (dir.exists()) {
                chooser.setCurrentDirectory(dir);
            }
        }
        chooser.setDialogType(JFileChooser.SAVE_DIALOG);
        javax.swing.filechooser.FileFilter jpgFileFilter = new FileFilter() {

            @Override
            public boolean accept(File f) {

                return (f.getName().toLowerCase().endsWith(".jpeg")) || (f.getName().toLowerCase().endsWith(".jpg"))
                        || (f.isDirectory());

            }

            @Override
            public String getDescription() {

                return "JPEG Image files";

            }

        };

        chooser.setFileFilter(jpgFileFilter);
        int returnValue = chooser.showSaveDialog(this);
        File file = null;
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            file = chooser.getSelectedFile();
        } else {
            return;
        }
        if (file == null) {
            return;
        }
        if (!(file.getName().toLowerCase().endsWith(".jpg")) && !(file.getName().toLowerCase().endsWith(".jpeg"))) {
            file = FileSystems.getDefault().getPath(file.getAbsolutePath() + ".jpeg").toFile();
        }

        // Save Image
        Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
        setCursor(hourglassCursor);

        BufferedImage awtImage = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);

        Graphics g = awtImage.getGraphics();
        this.printAll(g);

        boolean error = false;
        try {
            ImageIO.write(awtImage, "jpg", file);
        } catch (IOException ioe) {
            LOGGER.error("Error while saving image", ioe);
            error = true;
        }

        /*
         * try { FileOutputStream out = new FileOutputStream(file); //JPEGImageEncoderImpl j = new
         * JPEGImageEncoderImpl(out); //j.encode(awtImage); out.close(); } catch (Exception e) {
         * LOGGER.error("Error while saving image", e); }
         */

        hourglassCursor = new Cursor(Cursor.DEFAULT_CURSOR);
        setCursor(hourglassCursor);

        if (error) {
            this.uiHelper.showInfo(this.bundle.getString("chart.image.savedNotOK"));
        } else {
            this.uiHelper.showInfo(this.bundle.getString("chart.image.savedOK"));
        }

    }

    private SortedSet<IObservation> getObservations(SortedSet<IObservation> observations,
            Map<IObserver, Color> colorMap) {

        Iterator<IObservation> iterator = observations.iterator();
        IObservation current = null;
        while (iterator.hasNext()) {
            current = (IObservation) iterator.next();
            if (null == colorMap.get(current.getObserver())) { // No color assigned...so remove observation
                iterator.remove();
            }
        }

        return observations;

    }

}

class DataSpot extends Ellipse2D.Float {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private IObservation observation = null;

    public DataSpot(float x, float y, float w, float h, IObservation observation) {

        super(x, y, w, h);
        this.observation = observation;

    }

    public IObservation getObservation() {

        return ICloneable.copyOrNull(this.observation);

    }

    public FindingVariableStar getResult() {

        return (FindingVariableStar) this.observation.getResults().get(0);

    }

}

class ChartPopupHandler implements ActionListener {

    private MagnitudeDiagramm chart = null;

    private JMenuItem exit = null;
    private JMenuItem save = null;

    public ChartPopupHandler(int x, int y, MagnitudeDiagramm chart) {

        this.chart = chart;

        JPopupMenu popupMenu = new JPopupMenu();

        ResourceBundle bundle = ResourceBundle.getBundle("de.lehmannet.om.ui.extension.variableStars.VariableStar",
                Locale.getDefault());
        this.save = new JMenuItem(bundle.getString("chart.popup.save"));
        this.save.addActionListener(this);
        popupMenu.add(this.save);

        this.exit = new JMenuItem(bundle.getString("chart.popup.exit"));
        this.exit.addActionListener(this);
        popupMenu.add(this.exit);

        popupMenu.setPopupSize(150, 50);
        popupMenu.show(chart, x, y);

    }

    // --------------
    // ActionListener ---------------------------------------------------------
    // --------------

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() instanceof JMenuItem) {
            JMenuItem source = (JMenuItem) e.getSource();
            if (source.equals(this.exit)) {
                this.chart.firePropertyChange("exit", false, true);
            } else if (source.equals(this.save)) {
                this.chart.saveImage();
            }
        }

    }

}
