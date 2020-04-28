package de.lehmannet.om.model;

public class ObservationManagerModelImpl implements ObservationManagerModel {

    private boolean changed = false;

    private static final String CHANGED_SUFFIX = " *";
    private String title = "";
    private String titleWhenChanges = CHANGED_SUFFIX;

    @Override
    public boolean hasChanged() {
        return changed;
    }

    @Override
    public void setChanged(boolean b) {
        this.changed = true;

    }

    @Override
    public void setTitle(String title) {
        if (!this.title.equals(title)) {
            this.changed = true;
            this.title = title;
            this.titleWhenChanges  = this.title + CHANGED_SUFFIX;

        }

    }

    @Override
    public String getTittle() {
        if (changed) {
            return titleWhenChanges;
        } else {
            return title;
        }
    }

    

}