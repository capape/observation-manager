package de.lehmannet.om.ui.extension;

import javax.swing.JFrame;

import de.lehmannet.om.GenericTarget;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.TargetStar;
import de.lehmannet.om.ui.dialog.GenericTargetDialog;
import de.lehmannet.om.ui.dialog.ITargetDialog;
import de.lehmannet.om.ui.dialog.TargetStarDialog;

public class TargetDialogFactory {

    public static ITargetDialog newInstance(IExtensionContext extensionContext, String xsiType, JFrame parent,
            ITarget target, boolean editable) {
        switch (xsiType) {

            case GenericTarget.XML_XSI_TYPE_VALUE:
                return new GenericTargetDialog(parent, extensionContext.getConfiguration(),
                        extensionContext.getUserInterfaceHelper(), extensionContext.getModel(), target);

            case TargetStar.XML_XSI_TYPE_VALUE:
                return new TargetStarDialog(parent, extensionContext.getUserInterfaceHelper(),
                        extensionContext.getModel(), (TargetStar) target);

            default:
                throw new IllegalArgumentException("Invalid xsi:type");

        }

    }

}
