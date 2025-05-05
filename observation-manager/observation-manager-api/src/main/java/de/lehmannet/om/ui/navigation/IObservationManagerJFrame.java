package de.lehmannet.om.ui.navigation;

import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ui.util.IConfiguration;
import de.lehmannet.om.ui.util.Worker;

public interface IObservationManagerJFrame {

    ISchemaElement getSelectedTableElement();

    void createProgressDialog(Worker worker, String title, String loadingMessage);

    IConfiguration getConfiguration();
}
