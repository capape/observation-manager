/* ====================================================================
 * /comparator/SessionComparator.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.comparator;

import java.util.Calendar;
import java.util.Comparator;

import de.lehmannet.om.ISession;

public class SessionComparator implements Comparator {
  	
	private boolean reverse = false;
	
    public SessionComparator() {
    }

    public SessionComparator(boolean reverse) {

        this.reverse = reverse;
        
    }
	
	public int compare(Object o1, Object o2) {

		if(   (o1 instanceof ISession)
		   && (o2 instanceof ISession)
		   ) {
            ISession s1 = null;
            ISession s2 = null;
            if(reverse) {
                s1 = (ISession)o1;
                s2 = (ISession)o2;
            } else {
                s2 = (ISession)o1;
                s1 = (ISession)o2;
            }																																	
			
			Calendar s1Begin = s1.getBegin(); 
			Calendar s2Begin = s2.getBegin();
			
			if( s1Begin.before(s2Begin) ) {
				return -1;
			} else if( s1Begin.after(s2Begin) ) {
				return 1;
			} 
				
		}
			
		return 0;
			
   }    		

}
