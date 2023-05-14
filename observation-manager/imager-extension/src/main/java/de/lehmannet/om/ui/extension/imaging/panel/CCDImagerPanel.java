/*
 * ====================================================================
 * /panel/CCDImagerPanel.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.extension.imaging.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import de.lehmannet.om.IImager;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.extension.imaging.CCDImager;
import de.lehmannet.om.ui.panel.AbstractPanel;
import de.lehmannet.om.ui.panel.IImagerPanel;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.ui.util.EditPopupHandler;
import de.lehmannet.om.ui.util.OMLabel;
import de.lehmannet.om.util.FloatUtil;

public class CCDImagerPanel extends AbstractPanel implements MouseListener, IImagerPanel {

    private static final long serialVersionUID = 6535037526525557736L;

    private final ResourceBundle bundle = ResourceBundle.getBundle("de.lehmannet.om.ui.extension.imaging.Imaging",
            Locale.getDefault());

    private CCDImager imager = null;

    private JTextField xPixels = new JTextField(5);
    private JTextField yPixels = new JTextField(5);
    private JTextField xPixelSize = new JTextField(5);
    private JTextField yPixelSize = new JTextField(5);
    private JTextField model = new JTextField();
    private JTextField vendor = new JTextField();
    private JTextArea remarks = new JTextArea();
    private JTextField binning = new JTextField();

    public CCDImagerPanel(IImager imager, Boolean editable) {

        super(editable);

        if (imager != null) {
            if (!(imager instanceof CCDImager)) {
                throw new IllegalArgumentException(
                        "Passed Imager must be of type: de.lehmannet.om.extension.imaging.CCDImager");
            }

            this.imager = (CCDImager) imager;
        }

        this.createPanel();

        if (imager != null) {
            this.loadSchemaElement();
        }

    }

    @Override
    public ISchemaElement createSchemaElement() {

        // Check mandatory fields
        String modelName = this.getModelName();
        if (modelName == null) {
            return null;
        }

        int x = this.getXPixels();
        if (x == -1) {
            return null;
        }

        int y = this.getYPixels();
        if (y == -1) {
            return null;
        }

        // Create ccd imager
        this.imager = new CCDImager(modelName, x, y);

        // Add optional attributes
        // Add optional fields
        this.imager.setVendor(this.vendor.getText());

        this.imager.setRemarks(this.remarks.getText());

        if (!"".equals(this.binning.getText().trim())) {
            try {
                byte binning = Byte.parseByte(this.binning.getText());
                if ((binning < 1) || (binning > 9)) {
                    this.createWarning(this.bundle.getString("panel.ccdimager.warning.invalidBinning"));
                    return null;
                }
                this.imager.setBinning(binning);
            } catch (NumberFormatException nfe) {
                this.createWarning(this.bundle.getString("panel.ccdimager.warning.binning.numberFormat"));
                return null;
            }
        }

        try {
            float xSize = this.getXPixelSize();
            this.imager.setXPixelSize(xSize);
        } catch (NumberFormatException nfe) {
            return null;
        }

        try {
            float ySize = this.getYPixelSize();
            this.imager.setYPixelSize(ySize);
        } catch (NumberFormatException nfe) {
            return null;
        }

        return this.imager;

    }

    @Override
    public ISchemaElement getSchemaElement() {

        return this.imager;

    }

    @Override
    public ISchemaElement updateSchemaElement() {

        if (this.imager == null) {
            return null;
        }

        // Check mandatory fields
        String modelName = this.getModelName();
        if (modelName == null) {
            return null;
        }

        int x = this.getXPixels();
        if (x == -1) {
            return null;
        }

        int y = this.getYPixels();
        if (y == -1) {
            return null;
        }

        this.imager.setModel(modelName);
        this.imager.setXPixels(x);
        this.imager.setYPixels(y);

        // Add optional fields
        this.imager.setVendor(this.vendor.getText());

        this.imager.setRemarks(this.remarks.getText());

        if (!"".equals(this.binning.getText().trim())) {
            try {
                byte binning = Byte.parseByte(this.binning.getText());
                if ((binning < 1) || (binning > 9)) {
                    this.createWarning(this.bundle.getString("panel.ccdimager.warning.invalidBinning"));
                    return null;
                }
                this.imager.setBinning(binning);
            } catch (NumberFormatException nfe) {
                this.createWarning(this.bundle.getString("panel.ccdimager.warning.binning.numberFormat"));
                return null;
            }
        }

        try {
            float xSize = this.getXPixelSize();
            this.imager.setXPixelSize(xSize);
        } catch (NumberFormatException nfe) {
            return null;
        }

        try {
            float ySize = this.getYPixelSize();
            this.imager.setYPixelSize(ySize);
        } catch (NumberFormatException nfe) {
            return null;
        }

        return this.imager;

    }

    @Override
    public void mouseClicked(MouseEvent e) {

        // Check only button
        // Source component is always the JTextArea
        if (e.getButton() == MouseEvent.BUTTON3) {
            new EditPopupHandler(e.getX(), e.getY(), this.remarks);
        }

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // Do nothing
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // Do nothing
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // Do nothing
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // Do nothing
    }

    private String getModelName() {

        String modelName = this.model.getText();
        if ((modelName == null) || ("".equals(modelName))) {
            this.createWarning(bundle.getString("panel.ccd.warning.noModel"));
            return null;
        }

        return modelName;

    }

    private int getXPixels() {

        String xPix = this.xPixels.getText();
        if ((xPix == null) || ("".equals(xPix))) {
            this.createWarning(bundle.getString("panel.ccd.warning.noXAxis"));
            return -1;
        }
        int x = -1;
        try {
            x = Integer.parseInt(xPix);
        } catch (NumberFormatException nfe) {
            this.createWarning(bundle.getString("panel.ccd.warning.XAxisNumeric"));
            return -1;
        }

        if (x < 1) {
            this.createWarning(bundle.getString("panel.ccd.warning.XAxisNegative"));
            return -1;
        }

        return x;

    }

    private int getYPixels() {

        String yPix = this.yPixels.getText();
        if ((yPix == null) || ("".equals(yPix))) {
            this.createWarning(bundle.getString("panel.ccd.warning.noYAxis"));
            return -1;
        }
        int y = -1;
        try {
            y = Integer.parseInt(yPix);
        } catch (NumberFormatException nfe) {
            this.createWarning(bundle.getString("panel.ccd.warning.YAxisNumeric"));
            return -1;
        }

        if (y < 1) {
            this.createWarning(bundle.getString("panel.ccd.warning.YAxisNegative"));
            return -1;
        }

        return y;

    }

    private float getXPixelSize() throws NumberFormatException {

        String xPix = this.xPixelSize.getText();
        if ((xPix == null) || ("".equals(xPix.trim()))) {
            return 0.0f;
        }

        float x = -1;
        try {
            x = FloatUtil.parseFloat(xPix);
        } catch (NumberFormatException nfe) {
            this.createWarning(bundle.getString("panel.ccd.warning.XAxisSizeNumeric"));
            throw nfe;
        }

        if (x < 0.0) {
            this.createWarning(bundle.getString("panel.ccd.warning.XAxisSizeNegative"));
            throw new NumberFormatException("X Axis pixel size cannot be < 0.0");
        }

        return x;

    }

    private float getYPixelSize() throws NumberFormatException {

        String yPix = this.yPixelSize.getText();
        if ((yPix == null) || ("".equals(yPix.trim()))) {
            return 0.0f;
        }

        float y = -1;
        try {
            y = FloatUtil.parseFloat(yPix);
        } catch (NumberFormatException nfe) {
            this.createWarning(bundle.getString("panel.ccd.warning.YAxisSizeNumeric"));
            throw nfe;
        }

        if (y < 0.0) {
            this.createWarning(bundle.getString("panel.ccd.warning.YAxisSizeNegative"));
            throw new NumberFormatException("Y Axis pixel size cannot be < 0.0");
        }

        return y;

    }

    private void loadSchemaElement() {

        // Set mandatory

        this.model.setText(this.imager.getModel());
        this.model.setEditable(this.isEditable());

        this.xPixels.setText("" + this.imager.getXPixels());
        this.xPixels.setEditable(this.isEditable());

        this.yPixels.setText("" + this.imager.getYPixels());
        this.yPixels.setEditable(this.isEditable());

        // Set optional

        this.vendor.setText(this.imager.getVendor());
        this.vendor.setEditable(this.isEditable());

        this.remarks.setText(this.imager.getRemarks());
        this.remarks.setEditable(this.isEditable());
        this.remarks.setLineWrap(true);
        if (!this.isEditable()) {
            this.remarks.setBackground(Color.LIGHT_GRAY);
        }

        if (!Float.isNaN(this.imager.getXPixelSize())) {
            this.xPixelSize.setText("" + this.imager.getXPixelSize());
        }
        this.xPixelSize.setEditable(this.isEditable());

        if (!Float.isNaN(this.imager.getYPixelSize())) {
            this.yPixelSize.setText("" + this.imager.getYPixelSize());
        }
        this.yPixelSize.setEditable(this.isEditable());

        this.binning.setText("" + this.imager.getBinning());
        this.binning.setEditable(this.isEditable());

    }

    private void createPanel() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.setLayout(gridbag);

        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 1, 1, 5, 1);
        OMLabel LmodelName = new OMLabel(bundle.getString("panel.ccd.label.model"), true);
        LmodelName.setToolTipText(bundle.getString("panel.ccd.tooltip.model"));
        gridbag.setConstraints(LmodelName, constraints);
        this.add(LmodelName);
        ConstraintsBuilder.buildConstraints(constraints, 1, 0, 1, 1, 45, 1);
        this.model = new JTextField();
        this.model.setToolTipText(bundle.getString("panel.ccd.tooltip.model"));
        gridbag.setConstraints(this.model, constraints);
        this.add(this.model);

        ConstraintsBuilder.buildConstraints(constraints, 2, 0, 1, 1, 5, 1);
        OMLabel LvendorName = new OMLabel(bundle.getString("panel.ccd.label.vendor"), false);
        LvendorName.setToolTipText(bundle.getString("panel.ccd.tooltip.vendor"));
        gridbag.setConstraints(LvendorName, constraints);
        this.add(LvendorName);
        ConstraintsBuilder.buildConstraints(constraints, 3, 0, 1, 1, 45, 1);
        this.vendor = new JTextField();
        this.vendor.setToolTipText(bundle.getString("panel.ccd.tooltip.vendor"));
        gridbag.setConstraints(this.vendor, constraints);
        this.add(this.vendor);

        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 1, 1, 5, 1);
        OMLabel LxPix = new OMLabel(bundle.getString("panel.ccd.label.pixelX"), true);
        LxPix.setToolTipText(bundle.getString("panel.ccd.tooltip.pixelX"));
        gridbag.setConstraints(LxPix, constraints);
        this.add(LxPix);
        ConstraintsBuilder.buildConstraints(constraints, 1, 1, 1, 1, 45, 1);
        this.xPixels = new JTextField();
        this.xPixels.setToolTipText(bundle.getString("panel.ccd.tooltip.pixelX"));
        gridbag.setConstraints(this.xPixels, constraints);
        this.add(this.xPixels);

        ConstraintsBuilder.buildConstraints(constraints, 2, 1, 1, 1, 5, 1);
        OMLabel LyPix = new OMLabel(bundle.getString("panel.ccd.label.pixelY"), true);
        LyPix.setToolTipText(bundle.getString("panel.ccd.tooltip.pixelY"));
        gridbag.setConstraints(LyPix, constraints);
        this.add(LyPix);
        ConstraintsBuilder.buildConstraints(constraints, 3, 1, 1, 1, 45, 1);
        this.yPixels = new JTextField();
        this.yPixels.setToolTipText(bundle.getString("panel.ccd.tooltip.pixelY"));
        gridbag.setConstraints(this.yPixels, constraints);
        this.add(this.yPixels);

        ConstraintsBuilder.buildConstraints(constraints, 0, 2, 1, 1, 5, 1);
        OMLabel LxPixSize = new OMLabel(bundle.getString("panel.ccd.label.pixelXSize"), true);
        LxPixSize.setFont(new Font("sansserif", Font.ITALIC + Font.BOLD, 12));
        LxPixSize.setToolTipText(bundle.getString("panel.ccd.tooltip.pixelXSize"));
        gridbag.setConstraints(LxPixSize, constraints);
        this.add(LxPixSize);
        ConstraintsBuilder.buildConstraints(constraints, 1, 2, 1, 1, 45, 1);
        this.xPixelSize = new JTextField();
        this.xPixelSize.setToolTipText(bundle.getString("panel.ccd.tooltip.pixelXSize"));
        gridbag.setConstraints(this.xPixelSize, constraints);
        this.add(this.xPixelSize);

        ConstraintsBuilder.buildConstraints(constraints, 2, 2, 1, 1, 5, 1);
        OMLabel LyPixSize = new OMLabel(bundle.getString("panel.ccd.label.pixelYSize"), true);
        LyPixSize.setFont(new Font("sansserif", Font.ITALIC + Font.BOLD, 12));
        LyPixSize.setToolTipText(bundle.getString("panel.ccd.tooltip.pixelYSize"));
        gridbag.setConstraints(LyPixSize, constraints);
        this.add(LyPixSize);
        ConstraintsBuilder.buildConstraints(constraints, 3, 2, 1, 1, 45, 1);
        this.yPixelSize = new JTextField();
        this.yPixelSize.setToolTipText(bundle.getString("panel.ccd.tooltip.pixelYSize"));
        gridbag.setConstraints(this.yPixelSize, constraints);
        this.add(this.yPixelSize);

        ConstraintsBuilder.buildConstraints(constraints, 0, 3, 1, 1, 5, 1);
        OMLabel Lbinning = new OMLabel(bundle.getString("panel.ccd.label.binning"), false);
        Lbinning.setToolTipText(bundle.getString("panel.ccd.tooltip.binning"));
        gridbag.setConstraints(Lbinning, constraints);
        this.add(Lbinning);
        ConstraintsBuilder.buildConstraints(constraints, 1, 3, 1, 1, 45, 1);
        this.binning = new JTextField();
        this.binning.setToolTipText(bundle.getString("panel.ccd.tooltip.binning"));
        gridbag.setConstraints(this.binning, constraints);
        this.add(this.binning);

        ConstraintsBuilder.buildConstraints(constraints, 0, 4, 4, 1, 100, 1);
        OMLabel Lremarks = new OMLabel(bundle.getString("panel.ccd.label.remarks"), false);
        Lremarks.setToolTipText(bundle.getString("panel.ccd.tooltip.remarks"));
        gridbag.setConstraints(Lremarks, constraints);
        this.add(Lremarks);
        ConstraintsBuilder.buildConstraints(constraints, 0, 5, 4, 1, 100, 1);
        this.remarks = new JTextArea(3, 30);
        this.remarks.setToolTipText(bundle.getString("panel.ccd.tooltip.remarks"));
        this.remarks.addMouseListener(this);
        this.remarks.setLineWrap(true);
        JScrollPane remarksScroll = new JScrollPane(this.remarks);
        remarksScroll.setMinimumSize(new Dimension(300, 60));
        constraints.fill = GridBagConstraints.BOTH;
        gridbag.setConstraints(remarksScroll, constraints);
        this.add(remarksScroll);

        ConstraintsBuilder.buildConstraints(constraints, 0, 6, 4, 1, 100, 90);
        constraints.fill = GridBagConstraints.BOTH;
        JLabel Lfill = new JLabel("");
        gridbag.setConstraints(Lfill, constraints);
        this.add(Lfill);

    }

}
