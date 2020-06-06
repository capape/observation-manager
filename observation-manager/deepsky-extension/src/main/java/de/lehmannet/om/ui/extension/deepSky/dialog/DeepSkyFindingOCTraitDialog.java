/* ====================================================================
 * extension/deepSky/dialog/DeepSkyFindingOCTraitDialog.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.extension.deepSky.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import de.lehmannet.om.extension.deepSky.DeepSkyFindingOC;
import de.lehmannet.om.ui.dialog.OMDialog;

import de.lehmannet.om.ui.util.ConstraintsBuilder;

public class DeepSkyFindingOCTraitDialog extends OMDialog {

    private static final long serialVersionUID = -162741988653614067L;

    final PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle
            .getBundle("de.lehmannet.om.ui.extension.deepSky.DeepSky", Locale.getDefault());

    private TraitPanel panel = null;

    public DeepSkyFindingOCTraitDialog(JFrame om, String imagePath, Character character) {

        super(om);

        this.panel = new TraitPanel(this, imagePath, character);
        this.initDialog();

        this.setModal(true);

        this.setTitle(this.bundle.getString("dialog.oc.trait.title"));
        this.setSize(DeepSkyFindingOCTraitDialog.serialVersionUID, 770, 710);

        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(om);

        this.setVisible(true);

    }

    public Character getCharacter() {

        return this.panel.getCharacter();

    }

    private void initDialog() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        this.getContentPane().setLayout(gridbag);

        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 4, 1, 100, 90);
        constraints.fill = GridBagConstraints.BOTH;
        gridbag.setConstraints(this.panel, constraints);
        this.getContentPane().add(this.panel);

    }

}

class TraitPanel extends JPanel implements ActionListener {

    private JButton a = null;
    private JButton b = null;
    private JButton c = null;
    private JButton d = null;
    private JButton e = null;
    private JButton f = null;
    private JButton g = null;
    private JButton h = null;
    private JButton i = null;
    private JButton none = null;

    private Character character = null;
    private DeepSkyFindingOCTraitDialog dialog = null;
    private String path = null;

    public TraitPanel(DeepSkyFindingOCTraitDialog dialog, String imagePath, Character character) {

        this.path = imagePath;
        this.dialog = dialog;

        this.character = character;

        this.createPanel();

    }

    public Character getCharacter() {

        return this.character;

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        // Change table if different catalog is selected
        if (e.getSource() instanceof JButton) {
            JButton button = (JButton) e.getSource();
            if (this.a.equals(button)) {
                this.character = DeepSkyFindingOC.CHARACTER_A;
            } else if (this.b.equals(button)) {
                this.character = DeepSkyFindingOC.CHARACTER_B;
            } else if (this.c.equals(button)) {
                this.character = DeepSkyFindingOC.CHARACTER_C;
            } else if (this.d.equals(button)) {
                this.character = DeepSkyFindingOC.CHARACTER_D;
            } else if (this.e.equals(button)) {
                this.character = DeepSkyFindingOC.CHARACTER_E;
            } else if (this.f.equals(button)) {
                this.character = DeepSkyFindingOC.CHARACTER_F;
            } else if (this.g.equals(button)) {
                this.character = DeepSkyFindingOC.CHARACTER_G;
            } else if (this.h.equals(button)) {
                this.character = DeepSkyFindingOC.CHARACTER_H;
            } else if (this.i.equals(button)) {
                this.character = DeepSkyFindingOC.CHARACTER_I;
            } else if (this.none.equals(button)) {
                this.character = null;
            } // Do nothing. Leave old value untouched

            this.dialog.dispose();
        }

    }

    private void createPanel() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();

        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 2, 1, 33, 33);
        this.a = new JButton("A", new ImageIcon(Toolkit.getDefaultToolkit().getImage(this.path + "a.png")));
        this.a.setToolTipText(this.dialog.bundle.getString("dialog.oc.trait.explanation.a"));
        this.a.addActionListener(this);
        constraints.fill = GridBagConstraints.BOTH;
        gridbag.setConstraints(this.a, constraints);
        this.add(this.a);

        ConstraintsBuilder.buildConstraints(constraints, 2, 0, 2, 1, 33, 33);
        this.b = new JButton("B", new ImageIcon(Toolkit.getDefaultToolkit().getImage(this.path + "b.png")));
        this.b.setToolTipText(this.dialog.bundle.getString("dialog.oc.trait.explanation.b"));
        this.b.addActionListener(this);
        constraints.fill = GridBagConstraints.BOTH;
        gridbag.setConstraints(this.b, constraints);
        this.add(this.b);

        ConstraintsBuilder.buildConstraints(constraints, 4, 0, 2, 1, 33, 33);
        this.c = new JButton("C", new ImageIcon(Toolkit.getDefaultToolkit().getImage(this.path + "c.png")));
        this.c.setToolTipText(this.dialog.bundle.getString("dialog.oc.trait.explanation.c"));
        this.c.addActionListener(this);
        constraints.fill = GridBagConstraints.BOTH;
        gridbag.setConstraints(this.c, constraints);
        this.add(this.c);

        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 2, 1, 33, 33);
        this.d = new JButton("D", new ImageIcon(Toolkit.getDefaultToolkit().getImage(this.path + "d.png")));
        this.d.setToolTipText(this.dialog.bundle.getString("dialog.oc.trait.explanation.d"));
        this.d.addActionListener(this);
        constraints.fill = GridBagConstraints.BOTH;
        gridbag.setConstraints(this.d, constraints);
        this.add(this.d);

        ConstraintsBuilder.buildConstraints(constraints, 2, 1, 2, 1, 33, 33);
        this.e = new JButton("E", new ImageIcon(Toolkit.getDefaultToolkit().getImage(this.path + "e.png")));
        this.e.setToolTipText(this.dialog.bundle.getString("dialog.oc.trait.explanation.e"));
        this.e.addActionListener(this);
        constraints.fill = GridBagConstraints.BOTH;
        gridbag.setConstraints(this.e, constraints);
        this.add(this.e);

        ConstraintsBuilder.buildConstraints(constraints, 4, 1, 2, 1, 33, 33);
        this.f = new JButton("F", new ImageIcon(Toolkit.getDefaultToolkit().getImage(this.path + "f.png")));
        this.f.setToolTipText(this.dialog.bundle.getString("dialog.oc.trait.explanation.f"));
        this.f.addActionListener(this);
        constraints.fill = GridBagConstraints.BOTH;
        gridbag.setConstraints(this.f, constraints);
        this.add(this.f);

        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 2, 1, 33, 33);
        this.g = new JButton("G", new ImageIcon(Toolkit.getDefaultToolkit().getImage(this.path + "g.png")));
        this.g.setToolTipText(this.dialog.bundle.getString("dialog.oc.trait.explanation.g"));
        this.g.addActionListener(this);
        constraints.fill = GridBagConstraints.BOTH;
        gridbag.setConstraints(this.g, constraints);
        this.add(this.g);

        ConstraintsBuilder.buildConstraints(constraints, 2, 1, 2, 1, 33, 33);
        this.h = new JButton("H", new ImageIcon(Toolkit.getDefaultToolkit().getImage(this.path + "h.png")));
        this.h.setToolTipText(this.dialog.bundle.getString("dialog.oc.trait.explanation.h"));
        this.h.addActionListener(this);
        constraints.fill = GridBagConstraints.BOTH;
        gridbag.setConstraints(this.h, constraints);
        this.add(this.h);

        ConstraintsBuilder.buildConstraints(constraints, 4, 1, 2, 1, 33, 33);
        this.i = new JButton("I", new ImageIcon(Toolkit.getDefaultToolkit().getImage(this.path + "i.png")));
        this.i.setToolTipText(this.dialog.bundle.getString("dialog.oc.trait.explanation.i"));
        this.i.addActionListener(this);
        constraints.fill = GridBagConstraints.BOTH;
        gridbag.setConstraints(this.i, constraints);
        this.add(this.i);

        ConstraintsBuilder.buildConstraints(constraints, 0, 2, 3, 1, 50, 50);
        this.none = new JButton(this.dialog.bundle.getString("dialog.oc.trait.button.none"));
        this.none.addActionListener(this);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(this.none, constraints);
        this.add(this.none);

        ConstraintsBuilder.buildConstraints(constraints, 3, 2, 3, 1, 50, 50);
        JButton cancel = new JButton(this.dialog.bundle.getString("dialog.oc.trait.button.cancel"));
        cancel.addActionListener(this);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(cancel, constraints);
        this.add(cancel);

    }

}
