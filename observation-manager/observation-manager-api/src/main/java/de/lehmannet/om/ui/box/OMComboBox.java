package de.lehmannet.om.ui.box;

import de.lehmannet.om.ISchemaElement;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComboBox;

public class OMComboBox<T extends ISchemaElement> extends JComboBox<String> {

    private final Map<String, NodeBox> map = new HashMap<>();

    public T getSelectedSchemaElement() {

        String si = (String) this.getSelectedItem();

        NodeBox node = map.get(si);

        if (node == null) {
            return null;
        }

        return (T) node.getElement();
    }

    public void addEmptyItem() {

        this.addItem(NodeBox.EMPTY_ENTRY.getValue());
        this.setSelectedItem(NodeBox.EMPTY_ENTRY.getValue());
    }

    public void selectEmptyItem() {

        this.setSelectedItem(NodeBox.EMPTY_ENTRY.getValue());
    }

    public void setSelectedItem(T element) {

        if (element == null) {
            this.addEmptyItem();
        } else {
            this.setSelectedItem(this.getKey(element));
        }
    }

    public void addItem(T element) {

        if (element == null) {
            return;
        }

        String key = this.getKey(element);

        addItem(key, NodeBox.of(element));
    }

    protected String getKey(T element) {

        return element.getDisplayName();
    }

    private void addItem(String key, NodeBox element) {

        if (this.map.containsKey(key)) {
            this.setSelectedItem(key);
            return;
        }

        this.map.put(key, element);
        super.addItem(key);
        this.setSelectedItem(key);
    }
}
