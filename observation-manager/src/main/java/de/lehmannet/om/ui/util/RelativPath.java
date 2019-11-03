/* ====================================================================
 * /util/RelativPath.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class RelativPath {

	/**
	 * Get relative path of File 'f' with respect to 'home' directory
	 * 
	 * @param home base path
	 * @param f file to generate path for
	 * @return path from home to f as a string
	 */
	public static String getRelativePath(File home, File f){
		
		if( home.isFile() ) {
			home = new File(home.getParent());
		}
		
		List homelist = RelativPath.getPathList(home);
		List filelist = RelativPath.getPathList(f);

		return RelativPath.matchPathLists(homelist,filelist);
		
	}
	
	/**
	 * Break a path down into individual elements and add to a list.
	 * example : if a path is /a/b/c/d.txt, the breakdown will be [d.txt,c,b,a]
	 * 
	 * @param f input file
	 * @return a List collection with the individual elements of the path in reverse order
	 */
	 private static List getPathList(File f) {
		 
		List l = new ArrayList();
		File r;
		
		try {
			r = f.getCanonicalFile();
			while(r != null) {
				l.add(r.getName());
				r = r.getParentFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
			l = null;
		}
		
		return l;
		
	 } 

	 /**
	  * Figure out a string representing the relative path of
	  * 'f' with respect to 'r'
	  * 
	  * @param r home path
	  * @param f path of file
	  */
	 private static String matchPathLists(List r, List f) {
		 
		int i;
		int j;
		String s;
		
		// start at the beginning of the lists
		// iterate while both lists are equal
		s = "";
		i = r.size()-1;
		j = f.size()-1;

		// first eliminate common root
		while( (i >= 0) && (j >= 0) && ( r.get(i).equals(f.get(j)) ) ) {
			i--;
			j--;
		}

		// for each remaining level in the home path, add a ..
		for( ; i >= 0; i-- ) {
			s += ".." + File.separator;
		}

		// for each level in the file path, add the path
		for( ; j >= 1; j-- ) {
			s += f.get(j) + File.separator;
		}

		// file name
		s += f.get(j);
		s = "." + File.separator + s;
		
		return s;
		
	}
	
}