/* ====================================================================
 * /comparator/LensComparator.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.comparator;

import java.util.Comparator;

import de.lehmannet.om.ILens;

public class LensComparator implements Comparator {
  													
	public int compare(Object o1, Object o2) {

		if(   (o1 instanceof ILens)
		   && (o2 instanceof ILens)
		   ) {
			ILens l1 = (ILens)o1;
			ILens l2 = (ILens)o2;																																		
			
			float l1a = l1.getFactor();
			float l2a = l2.getFactor();
			
			return Math.round((float)Math.ceil(l1a - l2a));			
				
		}
			
		return 0;
			
   }    		

}
