package de.lehmannet.om.ui.extension.variableStars;

import javax.swing.JFrame;

import de.lehmannet.om.ITarget;
import de.lehmannet.om.ui.dialog.ITargetDialog;
import de.lehmannet.om.ui.extension.IExtensionContext;
import de.lehmannet.om.ui.extension.variableStars.dialog.VariableStarTargetDialog;
import de.lehmannet.om.extension.variableStars.TargetVariableStar;

public class TargetDialogFactory {

    public static ITargetDialog newInstance(IExtensionContext extensionContext, String xsiType, JFrame parent,
            ITarget target, boolean editable) {

        switch (xsiType) {

        case TargetVariableStar.XML_XSI_TYPE_VALUE:
            return new VariableStarTargetDialog(parent, extensionContext.getUserInterfaceHelper(),
                    extensionContext.getModel(), target);

        default:
            throw new IllegalArgumentException("Invalid xsi:type");

        }

    }

}
