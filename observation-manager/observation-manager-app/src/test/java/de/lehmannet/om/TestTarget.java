package de.lehmannet.om;

import org.w3c.dom.Element;

public class TestTarget extends Target {

    public TestTarget(String name, String datasource) {

        super(name, datasource);

    }
    @Override
    public String getXSIType() {
        return "TestTargetType";
    }

    @Override
    public void addToXmlElement(Element element) {
        
    }

    
}
