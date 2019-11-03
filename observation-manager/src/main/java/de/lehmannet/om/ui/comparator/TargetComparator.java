/* ====================================================================
 * /comparator/TargetComparator.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.comparator;

import java.util.Comparator;

import de.lehmannet.om.ITarget;

public class TargetComparator implements Comparator {
  													
	public int compare(Object o1, Object o2) {

		if(   (o1 instanceof ITarget)
		   && (o2 instanceof ITarget)
		   ) {
			ITarget t1 = (ITarget)o1;
			ITarget t2 = (ITarget)o2;																																		
			
			String t1Name = t1.getName().toLowerCase().trim();																	
			
			// Are names equal?
			if( t1Name.equals(t2.getName().toLowerCase().trim()) ) {
				return 0;
			}
			
			// Maybe we find t1 name in t2 alias names?
			String[] an = t2.getAliasNames();
			if(   (an != null)
			   && (an.length > 0)
			   ) {
				for(int i=0; i < an.length; i++) {
					if( t1Name.equals(an[i].toLowerCase().trim()) ) {
						return 0;
					}																				
				}
			}
			
			// No equal name found....sort using native String method
				return t1Name.compareToIgnoreCase(t2.getName().trim());
				
		}
			
		return 0;
			
   }    		

}
