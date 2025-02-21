package de.lehmannet.om.ui.extension.variableStars;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

public class ColorEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private final ResourceBundle bundle =
            ResourceBundle.getBundle("de.lehmannet.om.ui.extension.variableStars.VariableStar", Locale.getDefault());

    private Color currentColor;
    private final JButton button;
    private final JColorChooser colorChooser;
    private final JDialog dialog;

    private final String EDIT = "edit";

    public ColorEditor() {

        this.button = new JButton();
        this.button.setActionCommand(EDIT);
        this.button.addActionListener(this);
        this.button.setBorderPainted(false);

        // Set up the dialog that the button brings up.
        this.colorChooser = new JColorChooser();
        this.dialog = JColorChooser.createDialog(
                button,
                this.bundle.getString("popup.observerColor.colorEditor.title"),
                true, // modal
                colorChooser,
                this, // OK button handler
                null); // no CANCEL button handler
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (EDIT.equals(e.getActionCommand())) {
            // The user has clicked the cell, so bring up the dialog.
            this.button.setBackground(currentColor);
            this.colorChooser.setColor(currentColor);
            this.dialog.setVisible(true);

            fireEditingStopped(); // Make the renderer reappear.

        } else { // User pressed dialog's "OK" button.
            currentColor = colorChooser.getColor();
        }
    }

    // Implement the one CellEditor method that AbstractCellEditor doesn't.
    @Override
    public Object getCellEditorValue() {

        return currentColor;
    }

    // Implement the one method defined by TableCellEditor.
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {

        if (value != null) {
            currentColor = (Color) value;
            this.button.setBackground(currentColor);
            this.button.setText("");
        } else {
            currentColor = null;
            this.button.setText(this.bundle.getString("popup.observerColor.noColorSelection"));
            this.button.setBackground(Color.LIGHT_GRAY);
        }

        return this.button;
    }
}
