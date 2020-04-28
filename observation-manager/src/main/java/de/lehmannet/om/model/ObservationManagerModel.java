package de.lehmannet.om.model;

/**
 * Model for observation manager.
 * @autor capapegil
 */
public interface ObservationManagerModel {

    
    boolean hasChanged();

	void setChanged(boolean b);


    void setTitle(String title);
    String getTittle();

    
}