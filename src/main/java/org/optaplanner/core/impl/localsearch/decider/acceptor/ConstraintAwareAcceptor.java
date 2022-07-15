package org.optaplanner.core.impl.localsearch.decider.acceptor;

import org.optaplanner.core.impl.heuristic.selector.move.generic.SwapMove;
import org.optaplanner.core.impl.localsearch.AssignmentProblemType;
import org.optaplanner.core.impl.localsearch.event.LocalSearchPhaseLifecycleListenerAdapter;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchMoveScope;

/**
 * Acceptor that accepts moves based on the compliance with constraints that are evaluated
 * by an implementation of the
 * {@link org.optaplanner.core.impl.localsearch.decider.forager.privacypreserving.NeighbourhoodEvaluator}
 * 
 * @param <Solution_>
 */
public class ConstraintAwareAcceptor<Solution_> extends LocalSearchPhaseLifecycleListenerAdapter<Solution_>
        implements Acceptor<Solution_> {
    private ConstraintValidator<Solution_> constraintValidator;
    private AssignmentProblemType assignmentProblemType = AssignmentProblemType.UNBALANCED;

    public ConstraintAwareAcceptor(ConstraintValidator<Solution_> constraintValidator,
            AssignmentProblemType assignmentProblemType) {
        this.assignmentProblemType = assignmentProblemType;
        this.constraintValidator = constraintValidator;
    }

    /**
     * Triggers the {@link org.optaplanner.core.impl.localsearch.decider.forager.privacypreserving.NeighbourhoodEvaluator}
     * and accepts moves accordingly. If the {@link AssignmentProblemType} is set to BALANCED, only {@link SwapMove} is
     * accepted,
     * to increase performance.
     * 
     * @param moveScope the move in question
     * @return boolean indicating if move is accepted
     */
    @Override
    public boolean isAccepted(LocalSearchMoveScope<Solution_> moveScope) {
        boolean satisfiesConstraints = constraintValidator.satisfiesConstraints(moveScope.getMove().getPlanningEntities());
        if (assignmentProblemType == AssignmentProblemType.BALANCED) {
            return satisfiesConstraints && moveScope.getMove() instanceof SwapMove;
        }
        return satisfiesConstraints;
    }
}
