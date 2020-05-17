/* ====================================================================
 * /dialog/DidYouKnowDialog.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.util.ConfigKey;
import de.lehmannet.om.ui.util.ConstraintsBuilder;

public class DidYouKnowDialog extends OMDialog implements ActionListener {

    private static final long serialVersionUID = -8770733425729217836L;

    private static final String TEXT_PATH = "help" + File.separatorChar + "hints";

    private final PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle
            .getBundle("ObservationManager", Locale.getDefault());

    private ObservationManager om = null;

    private final JButton close = new JButton(this.bundle.getString("dialog.button.ok"));
    private final JButton next = new JButton(this.bundle.getString("didyouknow.button.next"));
    private final JCheckBox showOnStartup = new JCheckBox(this.bundle.getString("didyouknow.checkbox.showOnStartup"));
    private JTextArea text = null;

    public DidYouKnowDialog(ObservationManager om) {

        super(om);

        this.om = om;

        this.setTitle(this.bundle.getString("didyouknow.title"));
        this.setSize(DidYouKnowDialog.serialVersionUID, 440, 211);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(om);

        this.initDialog();

        this.pack();
        this.setVisible(true);

    }

    // --------------
    // ActionListener ---------------------------------------------------------
    // --------------

    @Override
    public void actionPerformed(ActionEvent e) {

        Object source = e.getSource();
        if (source instanceof JButton) {
            if (source.equals(this.close)) {
                // Save config value
                boolean show = this.showOnStartup.isSelected();
                this.om.getConfiguration().setConfig(ConfigKey.CONFIG_HELP_HINTS_STARTUP,
                        Boolean.toString(show));

                // Close UI;
                this.dispose();
            }
            if (source.equals(this.next)) {
                // Set new text
                this.text.setText(this.getText());
            }
        }

    }

    private void initDialog() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        this.getContentPane().setLayout(gridbag);

        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 1, 1, 10, 20);
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.NONE;
        Icon icon = UIManager.getIcon("OptionPane.questionIcon");
        JLabel iconLabel = new JLabel(icon);
        gridbag.setConstraints(iconLabel, constraints);
        this.getContentPane().add(iconLabel);

        ConstraintsBuilder.buildConstraints(constraints, 1, 0, 2, 3, 70, 70);
        constraints.anchor = GridBagConstraints.EAST;
        constraints.fill = GridBagConstraints.BOTH;
        this.text = new JTextArea();
        this.text.setLineWrap(true);
        this.text.setWrapStyleWord(true);
        this.text.setEditable(false);
        this.text.setText(this.getText());
        JScrollPane scrollPane = new JScrollPane(this.text, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        gridbag.setConstraints(scrollPane, constraints);
        this.getContentPane().add(scrollPane);

        ConstraintsBuilder.buildConstraints(constraints, 1, 3, 2, 1, 10, 5);
        constraints.anchor = GridBagConstraints.SOUTH;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.showOnStartup.setSelected(Boolean.parseBoolean(
                this.om.getConfiguration().getConfig(ConfigKey.CONFIG_HELP_HINTS_STARTUP, "true")));
        gridbag.setConstraints(this.showOnStartup, constraints);
        this.getContentPane().add(this.showOnStartup);

        ConstraintsBuilder.buildConstraints(constraints, 1, 4, 1, 1, 6, 5);
        constraints.anchor = GridBagConstraints.SOUTH;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.next.addActionListener(this);
        gridbag.setConstraints(this.next, constraints);
        this.getContentPane().add(this.next);

        ConstraintsBuilder.buildConstraints(constraints, 2, 4, 1, 1, 10, 5);
        constraints.anchor = GridBagConstraints.SOUTH;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.close.addActionListener(this);
        gridbag.setConstraints(this.close, constraints);
        this.getContentPane().add(this.close);

    }

    private String getText() {

        final String fileHintsFolder = DidYouKnowDialog.TEXT_PATH  + File.separatorChar + Locale.getDefault().getLanguage().toLowerCase() + File.separatorChar;
        final URL resource = DidYouKnowDialog.class.getClassLoader().getResource(fileHintsFolder);
        
      
        File textDir = new File(resource.getPath());

        File[] files = textDir.listFiles(); // Get all files in dir
        if ((files == null) // No files found...
                || (files.length == 0)) {
            return "No hint files found ";
        }

        int iNumber = 0;
        do {
            // Select random text
            double dNumber = Math.random(); // Between 0.1 and 1.0
            iNumber = (int) Math.round(dNumber * (files.length - 1)); // Expand random number to max length (number of
                                                                      // files found -1 as array start with 0)
        } while (!files[iNumber].isFile()); // Make sure we've found a file...this might be dangerous if there's no file
                                            // at all!

        String current = null;
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(files[iNumber]));
            current = br.readLine();
            while (current != null) {
                current = this.loadConvert(current);
                text.append(current);
                current = br.readLine();
            }
        } catch (FileNotFoundException fnfe) {
            System.err.println("Cannot find hint file: " + files[iNumber]);
        } catch (IOException ioe) {
            System.err.println("Error while reading file: " + files[iNumber] + "\nLast read line was: " + current);
        }

        return text.toString();

    }

    private String loadConvert(String theString) {

        char aChar;
        int len = theString.length();
        StringBuilder outBuffer = new StringBuilder(len);

        for (int x = 0; x < len;) {
            aChar = theString.charAt(x++);
            if (aChar == '\\') {
                aChar = theString.charAt(x++);
                if (aChar == 'u') {
                    // Read the xxxx
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = theString.charAt(x++);
                        switch (aChar) {
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            value = (value << 4) + aChar - '0';
                            break;
                        case 'a':
                        case 'b':
                        case 'c':
                        case 'd':
                        case 'e':
                        case 'f':
                            value = (value << 4) + 10 + aChar - 'a';
                            break;
                        case 'A':
                        case 'B':
                        case 'C':
                        case 'D':
                        case 'E':
                        case 'F':
                            value = (value << 4) + 10 + aChar - 'A';
                            break;
                        default:
                            throw new IllegalArgumentException("Malformed \\uxxxx encoding.");
                        }
                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't')
                        aChar = '\t';
                    else if (aChar == 'r')
                        aChar = '\r';
                    else if (aChar == 'n')
                        aChar = '\n';
                    else if (aChar == 'f')
                        aChar = '\f';
                    outBuffer.append(aChar);
                }
            } else
                outBuffer.append(aChar);
        }

        return outBuffer.toString();

    }

}
