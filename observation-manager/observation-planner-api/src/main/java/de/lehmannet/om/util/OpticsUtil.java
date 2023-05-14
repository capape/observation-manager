/*
 * ====================================================================
 * /util/OpticsUtil.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.util;

import de.lehmannet.om.Angle;
import de.lehmannet.om.IEyepiece;
import de.lehmannet.om.ILens;
import de.lehmannet.om.IScope;

/**
 * Simple calculations on eyepieces, scopes, cameras.
 *
 * @author doergn@users.sourceforge.net
 * @since 1.7
 */
public class OpticsUtil {

    // --------------
    // Public Methods ----------------------------------------------------
    // --------------

    /**
     * Calculates the exit pupil of a given scope eyepiece lens combination.
     *
     * @return The exit pupil of this scope/eyepiece combination, or Float.NaN if no magnification could be accessed or
     *         calculated
     */
    public static float getExitPupil(IScope scope, float eyepieceFL, ILens lens) {

        // We require a scope...
        if (scope == null) {
            return Float.NaN;
        }

        float mag = OpticsUtil.getMagnification(scope, eyepieceFL, lens);
        if (Float.isNaN(mag)) {
            return Float.NaN;
        }

        return scope.getAperture() / mag;

    }

    /**
     * Calculates the exit pupil of a given scope
     *
     * @return The exit pupil of this scope, or Float.NaN if no magnification could be accessed
     */
    public static float getExitPupil(IScope scope) {

        return OpticsUtil.getExitPupil(scope, -1.0f, null);

    }

    /**
     * Calculates the true field of view
     * In case the scope has a fixed magnification and the true field of view was set, returns this true field of view
     * In case the scope has a focal length set, the eyepieces apparent field of view is divided by the magnification
     * (which is calculated (together with the eyepiece))
     *
     * @param scope
     *            A scope with fixed or variable magnification
     * @param peyepieceFL
     *            Eyepiece focal length which was used
     * @param eyepiece
     *            The eyepiece used
     * @param lens
     *            Lens used (can be null)
     * @return The true field of view or null on case it could not be calculated
     */
    public static Angle getTrueFieldOfView(IScope scope, float peyepieceFL, IEyepiece eyepiece, ILens lens) {

        if (scope == null) {
            return null;
        }

        if ((Float.isNaN(peyepieceFL)) && (eyepiece == null)) {
            // Without eyepiece we can only return the trueFoV from the scope (if it has a
            // fixed magnification)
            if (scope.getTrueFieldOfView() == null) {
                return null;
            }
        }

        // Most simple way....scope has a fixed magnification and has an true FoV
        // set....
        if (scope.getTrueFieldOfView() != null) {
            return new Angle(scope.getTrueFieldOfView().toDegree(), Angle.DEGREE);
        }

        // Nothing to calculate
        if (eyepiece == null) {
            return null;
        }

        float eyepieceFL;
        if (Float.isNaN(peyepieceFL)) {
            eyepieceFL = eyepiece.getFocalLength();
        } else {
            eyepieceFL = peyepieceFL;
        }

        float mag = OpticsUtil.getMagnification(scope, eyepieceFL, lens);
        if (Float.isNaN(mag)) { // Check if magnification could be calculated
            return null;
        } else {
            Angle aFoV = eyepiece.getApparentFOV(); // Get apparent field of view
            if (aFoV != null) {
                return new Angle((aFoV.toArcMin() / mag), Angle.ARCMINUTE); // Calculate true FoV
            }
        }

        return null;

    }

    /**
     * Calculates the magnification of a eyepiece used at a given scope.
     *
     * @return The magnification or Float.NaN if one of both parameters was <b>NULL</b>
     */
    public static float getMagnification(IScope scope, float eyepieceFL) {

        if (scope == null) {
            return Float.NaN;
        }

        if (Float.isNaN(scope.getFocalLength())) {
            // Fixed magnification
            return scope.getMagnification();
        } else {
            if (Float.isNaN(eyepieceFL)) {
                return Float.NaN;
            }
            return scope.getFocalLength() / eyepieceFL;
        }

    }

    /**
     * Calculates the magnification of a eyepiece used at a given scope, and a used lens (barlow or focal reducer).
     *
     * @param scope
     *            The scope used (shouldn't have fixed magnification :-) )
     * @param eyepieceFL
     *            The eyepiece focal length used
     * @param lens
     *            The lens used (can be null if no lens was used)
     * @return The magnification or Float.NaN if one of required parameters was <b>NULL</b>
     */
    public static float getMagnification(IScope scope, float eyepieceFL, ILens lens) {

        if (scope == null) {
            return Float.NaN;
        }

        if (Float.isNaN(scope.getFocalLength())) {
            // Fixed magnification
            return scope.getMagnification();
        } else {
            if (Float.isNaN(eyepieceFL)) {
                return Float.NaN;
            }
            if (lens != null) {
                return scope.getFocalLength() * lens.getFactor() / eyepieceFL;
            } else {
                return scope.getFocalLength() / eyepieceFL;
            }
        }

    }

    /**
     * Calculates the actual focal length of an eyepiece used on a scope together with a lens (barlow or focal
     * reducer).<br>
     * This method is used to get the actual focal length used in case an zoom eyepiece was used for an observation.<br>
     *
     * @param scope
     *            The scope used (must not have fixed magnification )
     * @param lens
     *            The lens used (can be null if no lens was used)
     * @param magnification
     *            The magnification of the observation
     * @return The actual focal length of the (zoom-)eyepiece used for that observation or Float.NaN if one of required
     *         parameters was <b>NULL</b>
     */
    public static float getActualFocalLength(IScope scope, ILens lens, float magnification) {

        if ((scope == null) || (Float.isNaN(scope.getFocalLength()))) {
            return Float.NaN;
        }

        float F = scope.getFocalLength();

        float p = 1;
        if (lens != null) {
            p = lens.getFactor();
        }

        return F * p / magnification;

    }

}
