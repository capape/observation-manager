/* ====================================================================
 * /dialog/LogDialog.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.dialog;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.util.ConstraintsBuilder;

public class LogDialog extends OMDialog implements ActionListener {

    private static final long serialVersionUID = 3508562400111692974L;

    private final PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle
            .getBundle("ObservationManager", Locale.getDefault());

    private JTextPane text = null;
    private final JButton close = new JButton(this.bundle.getString("log.button.close"));

    private File logfile = null;
    private BufferedReader bufferedReader = null;
    private ObservationManager om = null;

    public LogDialog(ObservationManager om, File logFile) {

        super(om);

        this.om = om;

        this.logfile = logFile;

        super.setTitle(this.bundle.getString("log.title"));
        super.setSize(LogDialog.serialVersionUID, 630, 370);
        super.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        super.setLocationRelativeTo(om);

        this.initDialog();

        Reader reader = null;
        try {
            reader = new InputStreamReader(new FileInputStream(this.logfile));
            this.bufferedReader = new BufferedReader(reader);
        } catch (FileNotFoundException fnfe) {
            System.out.println("File not found: " + this.logfile);
            return;
        }

        this.setText();

        this.setVisible(true);
        // this.pack();

    }

    // --------------
    // ActionListener ---------------------------------------------------------
    // --------------

    @Override
    public void actionPerformed(ActionEvent e) {

        Object source = e.getSource();
        if (source instanceof JButton) {
            if (source.equals(this.close)) {
                this.dispose();
            }
        }

    }

    private void initDialog() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        super.getContentPane().setLayout(gridbag);

        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 1, 5, 1, 99);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.BOTH;
        this.text = new JTextPane();
        if (this.om.isNightVisionEnabled()) {
            this.text.setBackground(new Color(255, 175, 175));
        } else {
            this.text.setBackground(Color.WHITE);
        }
        this.text.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(this.text);
        gridbag.setConstraints(scrollPane, constraints);
        super.getContentPane().add(scrollPane);

        ConstraintsBuilder.buildConstraints(constraints, 0, 5, 1, 1, 1, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.close.addActionListener(this);
        gridbag.setConstraints(this.close, constraints);
        super.getContentPane().add(this.close);

    }

    private void setText() {

        int no = 1;
        try {
            String line = null;

            Document doc = this.text.getStyledDocument();
            SimpleAttributeSet attri = new SimpleAttributeSet();
            while ((line = this.bufferedReader.readLine()) != null) {

                if (line.startsWith(ObservationManager.LOG_ERROR_PREFIX)) {
                    StyleConstants.setForeground(attri, Color.RED);
                } else if (line.startsWith(ObservationManager.LOG_DEFAULT_PREFIX)) {
                    StyleConstants.setForeground(attri, Color.BLACK);
                }

                doc.insertString(doc.getLength(), line + "\n", attri);
                no++;
            }
            this.bufferedReader.close();
        } catch (IOException ioe) {
            System.err.println("Error reading line " + no + " from log " + this.logfile + "\n" + ioe);
        } catch (BadLocationException ble) {
            System.err.println("Error setting log text\n" + ble);
        }

    }

}
