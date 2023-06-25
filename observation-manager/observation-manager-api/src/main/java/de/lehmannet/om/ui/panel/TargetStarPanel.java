/*
 * ====================================================================
 * /panel/TargetStarPanel.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.panel;

import static de.lehmannet.om.ICloneable.copyOrNull;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;

import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.TargetStar;
import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.container.TargetStarContainer;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.ui.util.IConfiguration;

public class TargetStarPanel extends AbstractPanel {

    private static final long serialVersionUID = -6410013808077608852L;

    private TargetStar target = null;

    private TargetStarContainer targetContainer = null;
    private final ObservationManagerModel model;
    private final IConfiguration configuration;

    public TargetStarPanel(IConfiguration configuration, ObservationManagerModel model, TargetStar target,
            Boolean editable) throws IllegalArgumentException {

        super(editable);

        this.target = copyOrNull(target);
        this.model = model;
        this.configuration = configuration;

        this.createPanel();

    }

    @Override
    public ISchemaElement getSchemaElement() {

        return copyOrNull(this.target);

    }

    @Override
    public ISchemaElement updateSchemaElement() {

        if (this.target == null) {
            return null;
        }

        ITarget t = this.targetContainer.updateTarget();
        if (t == null) {
            return null;
        } else {
            this.target = (TargetStar) t;
        }

        return this.target.copy();

    }

    @Override
    public ISchemaElement createSchemaElement() {

        return this.targetContainer.createSchemaElement();

    }

    private void createPanel() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        this.setLayout(gridbag);

        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 4, 1, 45, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.targetContainer = new TargetStarContainer(this.configuration, this.model, this.target, this.isEditable());
        gridbag.setConstraints(this.targetContainer, constraints);
        this.add(this.targetContainer);

        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 4, 1, 45, 99);
        constraints.fill = GridBagConstraints.BOTH;
        JLabel Lfill = new JLabel("");
        gridbag.setConstraints(Lfill, constraints);
        this.add(Lfill);

    }

}
