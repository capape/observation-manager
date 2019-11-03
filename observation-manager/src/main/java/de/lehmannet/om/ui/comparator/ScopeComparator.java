/* ====================================================================
 * /comparator/ScopeComparator.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.comparator;

import java.util.Comparator;

import de.lehmannet.om.IScope;

public class ScopeComparator implements Comparator {
  													
	public int compare(Object o1, Object o2) {

		if(   (o1 instanceof IScope)
		   && (o2 instanceof IScope)
		   ) {
			IScope s1 = (IScope)o1;
			IScope s2 = (IScope)o2;																																		
			
			float s1a = s1.getAperture();
			float s2a = s2.getAperture();
			
			return Math.round((float)Math.ceil(s1a - s2a));
				
		}
			
		return 0;
			
   }    		

}
