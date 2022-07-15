package org.optaplanner.core.impl.localsearch.decider.acceptor;

import java.util.Collection;

/**
 * Interface that has to be implemented in conjunction with the {@link ConstraintAwareAcceptor},
 * in order to validate if a {@link org.optaplanner.core.impl.heuristic.move.Move} satisfies some constraints.
 * 
 * @param <Solution_>
 */
public interface ConstraintValidator<Solution_> {
    /***
     * Checks if the planning entities satisfy some constraints.
     * 
     * @param entities the planning entities collection.
     * @return boolean indicating if constraints are met.
     */
    boolean satisfiesConstraints(Collection<?> entities);
}
