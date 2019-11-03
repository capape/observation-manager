package de.lehmannet.om.ui.test;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import de.lehmannet.om.GenericFinding;
import de.lehmannet.om.IEyepiece;
import de.lehmannet.om.IFilter;
import de.lehmannet.om.IFinding;
import de.lehmannet.om.IImager;
import de.lehmannet.om.ILens;
import de.lehmannet.om.IObservation;
import de.lehmannet.om.IObserver;
import de.lehmannet.om.IScope;
import de.lehmannet.om.ISession;
import de.lehmannet.om.ISite;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.OALException;
import de.lehmannet.om.Observation;
import de.lehmannet.om.RootElement;
import de.lehmannet.om.extension.deepSky.DeepSkyFinding;
import de.lehmannet.om.extension.deepSky.DeepSkyFindingDS;
import de.lehmannet.om.extension.deepSky.DeepSkyFindingOC;
import de.lehmannet.om.ui.catalog.CatalogLoader;
import de.lehmannet.om.ui.catalog.IListableCatalog;
import de.lehmannet.om.util.SchemaException;
import de.lehmannet.om.util.SchemaLoader;

public class MassTest {

	File origXMLFile = null;
	File xsdFile = null;
	File newXMLFile = null;
	CatalogLoader cl = null;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		MassTest mt = new MassTest();
		mt.loadFiles(args[0], args[1]);
		//mt.cl = new CatalogLoader(new File(args[2]), null);
		mt.run(Integer.parseInt(args[3]));

	}
	
	public void run(int number) {
		
		SchemaLoader sl = new SchemaLoader();
		RootElement re = null;
		try {
			re = sl.load(this.origXMLFile, this.xsdFile);	
		} catch(SchemaException se) {
			System.err.println("Cannot load file: " + se);
			System.exit(1);
		} catch(OALException ce) {
			System.err.println("OAL Error: " + ce);
			System.exit(1);			
		}

		
		
		IEyepiece[] eyepieces = sl.getEyepieces();
		IFilter[] filters = sl.getFilters();
		IImager[] imagers = sl.getImagers();
		ILens[] lenses = sl.getLenses();
		IObserver[] observers = sl.getObservers();
		IScope[] scopes = sl.getScopes();
		ISession[] sessions = sl.getSessions();
		ISite[] sites = sl.getSites();
		ITarget[] tar = sl.getTargets();
		
		try {
			re.addEyepieces(Arrays.asList(eyepieces));
			re.addFilters(Arrays.asList(filters));
			re.addImagers(Arrays.asList(imagers));
			re.addLenses(Arrays.asList(lenses));
			re.addObservers(Arrays.asList(observers));
			re.addScopes(Arrays.asList(scopes));
			re.addSessions(Arrays.asList(sessions));
			re.addSites(Arrays.asList(sites));
			re.addTargets(Arrays.asList(tar));
		} catch(SchemaException se) {
			System.err.println("Cannot add existing element to Root Element: " + se);
			System.exit(1);
		}
		
		System.out.println("Start to create " + number + " randon observations...");
		IEyepiece eyepiece = null;
		IFilter filter = null;
		IImager imager = null;
		ILens lens = null;
		IObserver observer = null;
		IScope scope = null;
		ISession session = null;
		ISite site = null;
	    Random randomGenerator = new Random();	
	    String[] catalogs = this.cl.getListableCatalogNames();
	    IObservation newObs = null;
		for(int i=0; i < number; i++) {
		    int Reyepiece = randomGenerator.nextInt(eyepieces.length);
		    eyepiece = eyepieces[Reyepiece];

		    int Rfilter = randomGenerator.nextInt(filters.length);
		    filter = filters[Rfilter];
		    
		    int Rimager = randomGenerator.nextInt(imagers.length);
		    imager = imagers[Rimager];		    

		    int Rlens = randomGenerator.nextInt(lenses.length);
		    lens = lenses[Rlens];		    

		    int Robserver = randomGenerator.nextInt(observers.length);
		    observer = observers[Robserver];
		    
		    int Rscope = randomGenerator.nextInt(scopes.length);
		    scope = scopes[Rscope];		    

		    int Rsession = randomGenerator.nextInt(sessions.length);
		    session = sessions[Rsession];			    

		    int Rsite = randomGenerator.nextInt(sites.length);
		    site = sites[Rsite];		    
		    		    
		    IListableCatalog c = (IListableCatalog)this.cl.getCatalog(catalogs[randomGenerator.nextInt(catalogs.length)]);
		    ITarget[] targets = c.getTargets();
		    ITarget target = targets[randomGenerator.nextInt(targets.length)];
		    
		    IFinding finding = this.getFinding(target);
		    
		    newObs = new Observation(session.getBegin(), target, observer, finding);		    
		    newObs.setFilter(filter);
		    newObs.setImager(imager);
		    newObs.setLens(lens);
		    newObs.setScope(scope);
		    if( Float.isNaN(scope.getMagnification()) ) {
		    	newObs.setEyepiece(eyepiece);
		    }
		    newObs.setSession(session);
		    newObs.setSite(site);		    		    
		    
		    try {
		    	re.addTarget(target);
		    	re.addObservation(newObs);	
		    } catch(SchemaException se) {
				System.err.println("Cannot add observation: " + se + "\n" + newObs);
				System.exit(1);			    	
		    }		    
		    
		}
		
		try {
			System.out.print("Save start: " + new Date()) ;
			re.serializeAsXml(this.newXMLFile);
			System.out.print("Save end: " + new Date()) ;
		} catch(SchemaException se ) {
			System.err.println("Error during save: " + se);
			System.exit(1);
		}				
		
	}
	
	public void loadFiles(String xml, String xsd) {
		
		this.origXMLFile = new File(xml);
		this.xsdFile = new File(xsd);
		String newXML = xml.replaceAll(".xml", "_mass.xml");
		this.newXMLFile = new File(newXML);
		
	}
	
	private IFinding getFinding(ITarget t) {
		
		String desc = "Dies ist eine Beobachtung aus einer Massengenerierung.";
		
		String targetXSI = t.getXSIType();
		if("SolarSystemTargetType".equals(targetXSI)) {
			try {
				GenericFinding finding = new GenericFinding(desc);
				return finding;				
			} catch(SchemaException se) {
				System.err.println("Error in Finding creation. " + se);
			}
		} else if("deepSkyDS".equals(targetXSI)) {
			DeepSkyFindingDS ds = new DeepSkyFindingDS(desc, 3);
			return ds;			
		} else if("deepSkyOC".equals(targetXSI)) {
			DeepSkyFindingOC oc = new DeepSkyFindingOC(desc, 3);
			return oc;
		} else {
			DeepSkyFinding dsf = new DeepSkyFinding(desc, 3);
			return dsf;
		}		
		
		return null;
		
	}

}
