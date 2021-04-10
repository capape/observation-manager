package de.lehmannet.om.ui.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.lehmannet.om.ISchemaElement;

// One cache entry per file
// Stores an ISchemaElement and list of refering elements
// The refering elements will be the observations, beloning to the corresponding
// schemaElement
// Or in case the element is an IObservation, then the refering elements are all
// elements
// beloning to the IObservation (at that point in time)
// Storing the refered IObservation elements makes sense in the
// updateSchemaElement method, as
// the passed IObservation is already changed. Therefore we keep the last know
// references of an
// IObservation here. Think of it like a double linked list.
class SchemaElementCacheEntry<T extends ISchemaElement> {

    private T element = null;
    private final List<ISchemaElement> referenceList = new ArrayList<>();

    public SchemaElementCacheEntry(T element) {

        this.element = element;
    }

    public List<ISchemaElement> getReferencedElements() {

        /*
         * ISchemaElement[] result = (ISchemaElement[])this.referenceList.toArray(new ISchemaElement[] {});
         * Arrays.sort(result, new ObservationComparator());
         * 
         * return result;
         */
        // this.referenceList.

        return this.referenceList;

    }

    public T getSchemaElement() {

        return this.element;

    }

    public void addReferencedElement(ISchemaElement se) {

        if (se == null) {
            return;
        }

        this.referenceList.add(se);

    }

    public void addReferencedElements(Collection<ISchemaElement> collection) {

        if (collection == null) {
            return;
        }

        this.referenceList.addAll(collection);

    }

    public void removeReferencedElement(ISchemaElement se) {

        this.referenceList.remove(se);

    }

    public void removeReferencedElements(Collection<ISchemaElement> collection) {

        this.referenceList.removeAll(collection);

    }

    public int getNumberOfReferences() {

        return this.referenceList.size();

    }

    public boolean contains(T se) {

        return this.referenceList.contains(se);

    }

    public void clearAllReferences() {

        this.referenceList.clear();

    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((element == null) ? 0 : element.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SchemaElementCacheEntry other = (SchemaElementCacheEntry) obj;
        if (element == null) {
            if (other.element != null) {
                return false;
            }
        } else if (!element.equals(other.element)) {
            return false;
        }
        return true;
    }

}
