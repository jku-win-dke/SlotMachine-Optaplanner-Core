package org.optaplanner.core.impl.localsearch.decider.forager.privacypreserving;

import org.optaplanner.core.config.localsearch.decider.forager.EvaluationType;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchMoveScope;

/**
 * Forager implementing the Hill Climbing algorithm
 * 
 * @param <Solution_> generic solution
 */
public class PrivacyPreservingHillClimbingForager<Solution_> extends AbstractPrivacyPreservingForager<Solution_> {

    /**
     * Constructor
     * 
     * @param acceptedCountLimit_ neighbourhood size
     * @param evaluator the NeighbourhoodEvaluator
     * @param evaluationType the type of the evaluation
     */
    public PrivacyPreservingHillClimbingForager(int acceptedCountLimit_, NeighbourhoodEvaluator<Solution_> evaluator,
            EvaluationType evaluationType) {
        super(acceptedCountLimit_, evaluator, evaluationType);
    }

    /**
     * Implements a selection mechanism representing the Hill-Climbing idea by picking a move if the
     * highest score of the candidates is higher than the score of the current solution.
     * 
     * @return the winning move of the step
     */
    @Override
    protected boolean isAccepted(LocalSearchMoveScope<Solution_> winner) {
        if (iterations == 1)
            return true;

        var score = winner.getScore();

        // Pick move only if score gets improved
        if (score.compareTo(winner.getStepScope().getPhaseScope().getLastCompletedStepScope().getScore()) >= 0) {
            logger.debug("Found new winner with score: " + score);
            return true;
        }
        return false;
    }
}
