/* ====================================================================
 * /comparator/ObserverComparator.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.comparator;

import java.util.Comparator;

import de.lehmannet.om.IObserver;

public class ObserverComparator implements Comparator {
  													
	public int compare(Object o1, Object o2) {

		if(   (o1 instanceof IObserver)
		   && (o2 instanceof IObserver)
		   ) {
			IObserver ob1 = (IObserver)o1;
			IObserver ob2 = (IObserver)o2;																																		
			
			String n1 = ob1.getName().trim() + ob1.getSurname().trim();
			String n2 = ob2.getName().trim() + ob2.getSurname().trim();
			
			return n1.compareToIgnoreCase(n2);
				
		}
			
		return 0;
			
   }    		

}
