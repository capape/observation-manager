/*
 * ====================================================================
 * /panel/GenericFindingPanel.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.panel;

import static de.lehmannet.om.ICloneable.copyOrNull;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;

import de.lehmannet.om.GenericFinding;
import de.lehmannet.om.IFinding;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ISession;
import de.lehmannet.om.ui.container.FindingContainer;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.ui.util.IConfiguration;

public class GenericFindingPanel extends AbstractPanel implements IFindingPanel {

    private static final long serialVersionUID = -6156286102135355620L;

    private static final String XSI_TYPE = "oal:findingsType";

    private IFinding finding = null;
    private ISession session = null;
    private final IConfiguration configuration;

    private FindingContainer findingContainer = null;

    public GenericFindingPanel(IConfiguration configuration, IFinding finding, ISession s, Boolean editable)
            throws IllegalArgumentException {

        super(editable);

        this.finding = copyOrNull(finding);
        this.session = copyOrNull(s);
        this.configuration = configuration;

        this.createPanel();

    }

    @Override
    public String getXSIType() {

        return GenericFindingPanel.XSI_TYPE;

    }

    // ------
    // JPanel -----------------------------------------------------------------
    // ------

    @Override
    public String getName() {

        return AbstractPanel.bundle.getString("panel.finding.name");

    }

    @Override
    public ISchemaElement updateSchemaElement() {

        if (this.finding == null) {
            return null;
        }

        this.finding.setDescription(this.findingContainer.getDescription());

        if (this.findingContainer.getLanguage() != null) {
            this.finding.setLanguage(this.findingContainer.getLanguage());
        }

        return this.finding.copy();

    }

    @Override
    public ISchemaElement createSchemaElement() {

        GenericFinding gf = new GenericFinding(this.findingContainer.getDescription());

        if (this.findingContainer.getLanguage() != null) {
            gf.setLanguage(this.findingContainer.getLanguage());
        }

        return gf.copy();

    }

    @Override
    public ISchemaElement getSchemaElement() {

        return this.finding.copy();

    }

    private void createPanel() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        this.setLayout(gridbag);

        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 1, 1, 45, 50);
        constraints.fill = GridBagConstraints.BOTH;
        this.findingContainer = new FindingContainer(this.configuration, this.finding, this.session, this.isEditable());
        gridbag.setConstraints(this.findingContainer, constraints);
        this.add(this.findingContainer);

        ConstraintsBuilder.buildConstraints(constraints, 0, 2, 1, 1, 45, 50);
        constraints.fill = GridBagConstraints.BOTH;
        JLabel lFill = new JLabel("");
        gridbag.setConstraints(lFill, constraints);
        this.add(lFill);

    }

}
