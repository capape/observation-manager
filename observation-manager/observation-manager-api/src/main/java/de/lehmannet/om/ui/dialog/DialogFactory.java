package de.lehmannet.om.ui.dialog;

import de.lehmannet.om.ITarget;
import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.util.UserInterfaceHelper;
import javax.swing.JFrame;

public interface DialogFactory<T extends AbstractDialog> {

    T newInstance(JFrame om, UserInterfaceHelper uiHelper, ObservationManagerModel model, ITarget editableTarget);
}
