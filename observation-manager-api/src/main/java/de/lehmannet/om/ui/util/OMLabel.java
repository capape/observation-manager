package de.lehmannet.om.ui.util;

import java.awt.Font;

import javax.swing.JLabel;

public class OMLabel extends JLabel {

    private static final long serialVersionUID = 6023705893875362065L;

    private boolean mandatoryField = false;

    public OMLabel() {

        super();

    }

    public OMLabel(boolean mandatoryField) {

        this.mandatoryField = mandatoryField;

    }

    public OMLabel(String labelText, int horizontalAlignment, boolean mandatoryField) {

        this(labelText, mandatoryField);
        this.setHorizontalAlignment(horizontalAlignment);

    }

    public OMLabel(String labelText, boolean mandatoryField) {

        super();

        this.mandatoryField = mandatoryField;

        this.setText(labelText);

    }

    @Override
    public void setText(String text) {
//TODO avoid break api
        if (this.mandatoryField) { // Underline mandatory fields
            this.setFont(new Font("sansserif", Font.BOLD, 12));
            super.setText("<html><u>" + text + "</u></html>");
        } else { // Write optional field italic
            super.setText(text);
            this.setFont(new Font("sansserif", Font.ITALIC + Font.BOLD, 12));
        }

    }

}
