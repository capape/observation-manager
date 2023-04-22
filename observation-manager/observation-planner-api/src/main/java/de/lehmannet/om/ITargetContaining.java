/* ====================================================================
 * /TargetContaining.java
 *
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om;

import java.util.List;

/**
 * Basically a marker interface that indicates that this ITarget implementation depends on other ITargets.<br>
 * Example: MultipleStarSystems A MultipleStarSystem should implement this interface as it refers to other ITargets
 * (TargetStar) elements.
 *
 * @author D036774
 *
 * @since 2.0_p1
 *
 */
public interface ITargetContaining {

    List<ITarget> getComponentTargets(ITarget... targets);

}
