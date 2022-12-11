package de.lehmannet.om.mapper;

import java.io.File;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.lehmannet.om.IEyepiece;
import de.lehmannet.om.IFilter;
import de.lehmannet.om.IFinding;
import de.lehmannet.om.IImager;
import de.lehmannet.om.ILens;
import de.lehmannet.om.IObservation;
import de.lehmannet.om.IObserver;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.IScope;
import de.lehmannet.om.ISession;
import de.lehmannet.om.ISite;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.SurfaceBrightness;
import de.lehmannet.om.util.FloatUtil;
import de.lehmannet.om.util.SchemaException;
import de.lehmannet.om.util.SchemaLoader;

public class ObservationMapper {

    public static IImager getOptionalImager(IImager[] imagers, Element observationElement) throws SchemaException {

        // Get optional imager link

        NodeList children = observationElement.getElementsByTagName(IImager.XML_ELEMENT_IMAGER);
        if (children.getLength() == 1) {
            Element child = (Element) children.item(0);
            if (child != null) {
                String imagerID = child.getFirstChild().getNodeValue();

                if ((imagers != null) && (imagers.length > 0)) {
                    for (IImager iImager : imagers) {
                        if (iImager.getID().equals(imagerID)) {
                            return iImager;
                        }
                    }

                    throw new SchemaException("Observation imager links to not existing imager element. ");

                } else {
                    throw new IllegalArgumentException("Parameter IImager array cannot be NULL or empty. ");
                }
            } else {
                throw new SchemaException("Problem retrieving imager from observation. ");
            }
        } else if (children.getLength() > 1) {
            throw new SchemaException("Observation can have exact one imager. ");
        }

        return null;
    }

    public static IFilter getOptionalFilter(IFilter[] filters, Element observationElement) throws SchemaException {

        // Get optional filter link

        NodeList children = observationElement.getElementsByTagName(IFilter.XML_ELEMENT_FILTER);
        if (children.getLength() == 1) {
            Element child = (Element) children.item(0);
            if (child != null) {
                String filterID = child.getFirstChild().getNodeValue();

                if ((filters != null) && (filters.length > 0)) {
                    for (IFilter iFilter : filters) {
                        if (iFilter.getID().equals(filterID)) {
                            return iFilter;
                        }
                    }

                    throw new SchemaException("Observation filter links to not existing filter element. ");

                } else {
                    throw new IllegalArgumentException("Parameter IFilter array cannot be NULL or empty. ");
                }
            } else {
                throw new SchemaException("Problem retrieving filter from observation. ");
            }
        } else if (children.getLength() > 1) {
            throw new SchemaException("Observation can have exact one filter. ");
        }
        return null;
    }

    public static ILens getOptionalLens(Element observationElement, ILens... lenses) throws SchemaException {

        // Get optional lens link

        NodeList children = observationElement.getElementsByTagName(ILens.XML_ELEMENT_LENS);
        if (children.getLength() == 1) {
            Element child = (Element) children.item(0);
            if (child != null) {
                String lensID = child.getFirstChild().getNodeValue();

                if ((lenses != null) && (lenses.length > 0)) {
                    for (ILens iLens : lenses) {
                        if (iLens.getID().equals(lensID)) {
                            return iLens;
                        }
                    }
                    throw new SchemaException("Observation lens links to not existing lens element. ");

                } else {
                    throw new IllegalArgumentException("Parameter ILens array cannot be NULL or empty. ");
                }
            } else {
                throw new SchemaException("Problem retrieving lens from observation. ");
            }
        } else if (children.getLength() > 1) {
            throw new SchemaException("Observation can have exact one lens. ");
        }
        return null;
    }

    public static IEyepiece getOptionalEyepiece(IEyepiece[] eyepieces, Element observationElement)
            throws SchemaException {

        // Get optional eyepiece link

        NodeList children = observationElement.getElementsByTagName(IEyepiece.XML_ELEMENT_EYEPIECE);
        if (children.getLength() == 1) {
            Element child = (Element) children.item(0);
            if (child != null) {
                String eyepieceID = child.getFirstChild().getNodeValue();

                if ((eyepieces != null) && (eyepieces.length > 0)) {
                    for (IEyepiece iEyepiece : eyepieces) {
                        if (iEyepiece.getID().equals(eyepieceID)) {
                            return iEyepiece;
                        }
                    }
                    throw new SchemaException("Observation eyepiece links to not existing eyepiece element. ");

                } else {
                    throw new IllegalArgumentException("Parameter IEyepiece array cannot be NULL or empty. ");
                }
            } else {
                throw new SchemaException("Problem retrieving eyepiece from observation. ");
            }
        } else if (children.getLength() > 1) {
            throw new SchemaException("Observation can have exact one eyepiece. ");
        }
        return null;
    }

    public static ISession getOptionalSession(ISession[] sessions, Element observationElement) throws SchemaException {

        // Get optional session link

        NodeList children = observationElement.getElementsByTagName(ISession.XML_ELEMENT_SESSION);
        if (children.getLength() == 1) {
            Element child = (Element) children.item(0);
            if (child != null) {
                String sessionID = child.getFirstChild().getNodeValue();

                if ((sessions != null) && (sessions.length > 0)) {
                    // Check if session exists
                    for (ISession iSession : sessions) {
                        if (iSession.getID().equals(sessionID)) {
                            return iSession;
                        }
                    }

                } else {
                    throw new IllegalArgumentException("Parameter ISession array cannot be NULL or empty. ");
                }
            } else {
                throw new SchemaException("Problem retrieving session from observation. ");
            }
        } else if (children.getLength() > 1) {
            throw new SchemaException("Observation can have only one session. ");
        }
        return null;
    }

    public static int getOptionalSeeing(Element observationElement) throws SchemaException {

        // Get optional seeing

        NodeList children = observationElement.getElementsByTagName(IObservation.XML_ELEMENT_SEEING);
        String seeing = null;
        if (children.getLength() == 1) {
            Element child = (Element) children.item(0);
            if (child != null) {
                seeing = child.getFirstChild().getNodeValue();
                return Integer.parseInt(seeing);
            } else {
                throw new SchemaException("Problem while retrieving seeing of observation. ");
            }
        } else if (children.getLength() > 1) {
            throw new SchemaException("Observation can have only one seeing value. ");
        }
        return -1;
    }

    public static String getOptionalAccesories(Element observationElement) throws SchemaException {

        // Get optional accessories

        NodeList children = observationElement.getElementsByTagName(IObservation.XML_ELEMENT_ACCESSORIES);
        StringBuilder accessories = new StringBuilder();
        if (children.getLength() == 1) {
            Element child = (Element) children.item(0);
            if (child != null) {
                NodeList textElements = child.getChildNodes();
                if ((textElements != null) && (textElements.getLength() > 0)) {
                    for (int te = 0; te < textElements.getLength(); te++) {
                        accessories.append(textElements.item(te).getNodeValue());
                    }
                    return accessories.toString();
                }
                // accessories = child.getFirstChild().getNodeValue();
            } else {
                throw new SchemaException("Problem while retrieving accessories of observation. ");
            }
        } else if (children.getLength() > 1) {
            throw new SchemaException("Observation can have only one accessories entry. ");
        }
        return null;
    }

    public static float getOptionalMagnification(Element observationElement) throws SchemaException {

        // Get optional magnification

        NodeList children = observationElement.getElementsByTagName(IObservation.XML_ELEMENT_MAGNIFICATION);
        String mag = null;
        if (children.getLength() == 1) {
            Element child = (Element) children.item(0);
            if (child != null) {
                mag = child.getFirstChild().getNodeValue();
                return FloatUtil.parseFloat(mag);
            } else {
                throw new SchemaException("Problem while retrieving magnification of observation. ");
            }
        } else if (children.getLength() > 1) {
            throw new SchemaException("Observation can have only one magnification value. ");
        }
        return Float.NaN;
    }

    public static SurfaceBrightness getNewSkyQualityMeter(Element observationElement) throws SchemaException {

        // Get optional sky quality meter value (by new (2.0) name)

        NodeList children = observationElement.getElementsByTagName(IObservation.XML_ELEMENT_SKYQUALITY_NEW);
        if (children.getLength() == 1) {
            Element child = (Element) children.item(0);
            if (child != null) {
                String sqm = child.getFirstChild().getNodeValue();
                String unit = child.getAttribute(SurfaceBrightness.XML_ATTRIBUTE_UNIT);
                return new SurfaceBrightness(FloatUtil.parseFloat(sqm), unit);
            } else {
                throw new SchemaException("Problem while retrieving sky quality meter value of observation. ");
            }
        } else if (children.getLength() > 1) {
            throw new SchemaException("Observation can have only one sky quality meter value. ");
        }
        return null;
    }

    public static SurfaceBrightness getSkyQualityMeter(Element observationElement) throws SchemaException {

        // Get optional sky quality meter value (via deprecated element name)

        NodeList children = observationElement.getElementsByTagName(IObservation.XML_ELEMENT_SKYQUALITY);
        String sqm = null;
        if (children.getLength() == 1) {
            Element child = (Element) children.item(0);
            if (child != null) {
                sqm = child.getFirstChild().getNodeValue();
                return new SurfaceBrightness(FloatUtil.parseFloat(sqm), SurfaceBrightness.MAGS_SQR_ARC_SEC);
            } else {
                throw new SchemaException("Problem while retrieving sky quality meter value of observation. ");
            }
        } else if (children.getLength() > 1) {
            throw new SchemaException("Observation can have only one sky quality meter value. ");
        }
        return null;
    }

    public static float getOptionalFaintestStar(Element observationElement) throws SchemaException {

        // Get optional faintest star

        NodeList children = observationElement.getElementsByTagName(IObservation.XML_ELEMENT_FAINTESTSTAR);
        String faintestStar = null;
        if (children.getLength() == 1) {
            Element child = (Element) children.item(0);
            if (child != null) {
                faintestStar = child.getFirstChild().getNodeValue();
                return FloatUtil.parseFloat(faintestStar);
            } else {
                throw new SchemaException("Problem while retrieving faintest star of observation. ");
            }
        } else if (children.getLength() > 1) {
            throw new SchemaException("Observation can have only one faintest star. ");
        }
        return Float.NaN;
    }

    public static List<String> getOptionalImages(Element observationElement) throws SchemaException {

        // Get optional images

        NodeList children = observationElement.getElementsByTagName(IObservation.XML_ELEMENT_IMAGE);
        StringBuilder image = new StringBuilder();
        List<String> images = new ArrayList<>();

        for (int i = 0; i < children.getLength(); i++) {
            Element child = (Element) children.item(i);
            if (child != null) {
                NodeList textElements = child.getChildNodes();
                if ((textElements != null) && (textElements.getLength() > 0)) {
                    for (int te = 0; te < textElements.getLength(); te++) {
                        image.append(textElements.item(te).getNodeValue());
                    }
                    // Make sure to use the local file separator character during runtime
                    // When saving the file, we'll convert it back to /
                    image = new StringBuilder(image.toString().replace('/', File.separatorChar));
                    image = new StringBuilder(image.toString().replace('\\', File.separatorChar));
                    images.add(image.toString());
                    image = new StringBuilder();
                }
                // image = child.getFirstChild().getNodeValue();
            } else {
                throw new SchemaException("Problem while retrieving image of observation. ");
            }
        }
        return images;
    }

    public static IScope getOptionalScope(IScope[] scopes, Element observationElement) throws SchemaException {

        // Get optional scope link

        NodeList children = observationElement.getElementsByTagName(IScope.XML_ELEMENT_SCOPE);
        if (children.getLength() == 1) {
            Element child = (Element) children.item(0);
            if (child != null) {
                String scopeID = child.getFirstChild().getNodeValue();

                if ((scopes != null) && (scopes.length > 0)) {

                    for (IScope iScope : scopes) {
                        if (iScope.getID().equals(scopeID)) {
                            return iScope;
                        }
                    }
                    throw new SchemaException("Observation scope links to not existing scope element. ");
                } else {
                    throw new IllegalArgumentException("Parameter IScope array cannot be NULL or empty. ");
                }
            } else {
                throw new SchemaException("Problem retrieving scope from observation. ");
            }
        } else if (children.getLength() > 1) {
            throw new SchemaException("Observation can have only one scope. (Or none at all)");
        }
        return null;
    }

    public static ISite getOptionalSite(ISite[] sites, Element observationElement) throws SchemaException {

        // Get optional site link

        NodeList children = observationElement.getElementsByTagName(ISite.XML_ELEMENT_SITE);
        if (children.getLength() == 1) {
            Element child = (Element) children.item(0);
            if (child != null) {
                String siteID = child.getFirstChild().getNodeValue();

                if ((sites != null) && (sites.length > 0)) {
                    for (ISite iSite : sites) {
                        if (iSite.getID().equals(siteID)) {
                            return iSite;
                        }
                    }

                } else {
                    throw new IllegalArgumentException("Parameter ISite array cannot be NULL or empty. ");
                }
            } else {
                throw new SchemaException("Problem retrieving site from observation. ");
            }
        } else if (children.getLength() > 1) {
            throw new SchemaException("Observation can have only one site. (Or none at all)");
        }
        return null;
    }

    public static ZonedDateTime getOptionalEndDate(Element observationElement) throws SchemaException {

        NodeList children = observationElement.getElementsByTagName(IObservation.XML_ELEMENT_END);

        if (children.getLength() == 1) {
            Element child = (Element) children.item(0);
            if (child == null) {
                throw new SchemaException("Problem retrieving observations end date. ");
            } else {
                String ISO8601End = child.getFirstChild().getNodeValue();
                try {
                    return ZonedDateTime.parse(ISO8601End);

                } catch (NumberFormatException nfe) {
                    throw new SchemaException("End date is malformed. ", nfe);
                }
            }
        } else if (children.getLength() > 1) {
            throw new SchemaException("Observation can have exact one end date. ");
        }
        return null;
    }

    public static List<IFinding> getMandatoryResult(Element observationElement, ITarget target) throws SchemaException {

        // Get mandatory result
        NodeList children = observationElement.getElementsByTagName(IFinding.XML_ELEMENT_FINDING);
        if (children.getLength() == 0) {
            throw new SchemaException("Observation must have one or more results. ");
        }
        IFinding[] results = new IFinding[children.getLength()];
        for (int j = 0; j < children.getLength(); j++) {
            results[j] = createFindingElements(children.item(j), target);
        }
        List<IFinding> find = Arrays.asList(results);
        return find;
    }

    public static IObserver getMandatoryObserver(IObserver[] observers, Element observationElement)
            throws SchemaException {

        // Get mandatory observer link

        NodeList children = observationElement.getElementsByTagName(IObserver.XML_ELEMENT_OBSERVER);
        if (children.getLength() != 1) {
            throw new SchemaException("Observation must have exact one observer. ");
        }
        Element child = (Element) children.item(0);
        if (child == null) {
            throw new SchemaException("Observation must have observer. ");
        } else {
            String observerID = child.getFirstChild().getNodeValue();

            if ((observers != null) && (observers.length > 0)) {
                // Check if observer exists
                for (IObserver iObserver : observers) {
                    if (iObserver.getID().equals(observerID)) {
                        return iObserver;
                    }
                }

                throw new SchemaException("Observation observer links to not existing observer element. ");

            } else {
                throw new IllegalArgumentException("Parameter IObserver array cannot be NULL or empty. ");
            }
        }
    }

    public static ITarget getMandatoryTarget(ITarget[] targets, Element observationElement) throws SchemaException {

        // Get mandatory target link

        NodeList children = observationElement.getElementsByTagName(ITarget.XML_ELEMENT_TARGET);
        if (children.getLength() != 1) {
            throw new SchemaException("Observation must have exact one target. ");
        }
        Element child = (Element) children.item(0);
        if (child == null) {
            throw new SchemaException("Observation must have target. ");
        } else {
            String targetID = child.getFirstChild().getNodeValue();

            if ((targets != null) && (targets.length > 0)) {
                for (ITarget iTarget : targets) {
                    if (iTarget.getID().equals(targetID)) {
                        return iTarget;
                    }
                }

                throw new SchemaException("Observation  links to not existing target element!");

            } else {
                throw new IllegalArgumentException("Parameter ITarget array cannot be NULL or empty. ");
            }

        }
    }

    public static ZonedDateTime getMandatoryBeginDate(Element observationElement) throws SchemaException {

        // Get mandatory begin date
        NodeList children = observationElement.getElementsByTagName(IObservation.XML_ELEMENT_BEGIN);
        if (children.getLength() != 1) {
            throw new SchemaException("Observation must have exact one begin date. ");
        }
        Element child = (Element) children.item(0);
        if (child == null) {
            throw new SchemaException("Observation must have begin date. ");
        } else {
            String ISO8601Begin = null;
            if (child.getFirstChild() != null) {
                ISO8601Begin = child.getFirstChild().getNodeValue();
            } else {
                throw new SchemaException("Begin date is empty. ");
            }
            try {
                return ZonedDateTime.parse(ISO8601Begin);

            } catch (NumberFormatException nfe) {
                throw new SchemaException("Begin date is malformed. ", nfe);
            }
        }

    }

    public static String getMandatoryID(Element observationElement) throws SchemaException {
        // Get ID from element
        NamedNodeMap attributes = observationElement.getAttributes();
        if (attributes.getLength() == 0) {
            throw new SchemaException("Observation must have a unique ID. ");
        }
        String ID = observationElement.getAttribute(ISchemaElement.XML_ELEMENT_ATTRIBUTE_ID);
        return ID;
    }

    // ---------------
    // Private Methods ---------------------------------------------------
    // ---------------

    private static IFinding createFindingElements(Node result, ITarget target) throws SchemaException {

        Element finding = (Element) result;

        // Get classname from xsi:type
        // String xsiType = finding.getAttribute(IFinding.XML_XSI_TYPE);
        String xsiType = target.getXSIType(); // Use XSI:Type from target to determin Finding type
        IFinding object = SchemaLoader.getFindingFromXSIType(xsiType, finding);
        if (object != null) {
            IFinding currentFinding = null;
            currentFinding = object;
            return currentFinding;
        } else {
            throw new SchemaException("Unable to load class of type: " + xsiType);
        }

    }
}