package de.lehmannet.om.ui.box;

import de.lehmannet.om.ISchemaElement;

public class NodeBox {

    private final ISchemaElement element;
    private final String value;
    private final boolean isEmpty;

    public ISchemaElement getElement() {
        return element;
    }

    public String getValue() {
        return value;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public static final NodeBox EMPTY_ENTRY = new NodeBox("----");

    public static NodeBox of(ISchemaElement element) {
        return new NodeBox(element);
    }

    private NodeBox(ISchemaElement paramElement) {
        this.element = paramElement;
        this.isEmpty = false;
        this.value = element.toString();
    }

    private NodeBox(String value) {
        this.isEmpty = true;
        this.value = value;
        this.element = null;
    }
}
