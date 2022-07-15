package org.optaplanner.core.impl.localsearch.decider.forager.privacypreserving;

import java.util.ArrayList;
import java.util.List;

import org.optaplanner.core.config.localsearch.decider.forager.EvaluationType;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchMoveScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;

/**
 * Forager implementing a Tabu Search algorithm.
 * All suggested moves are added to the tabu list after each step and therefore tabu for the next step(s).
 * 
 * @param <Solution_>
 */
public class PrivacyPreservingTabuSearchForager<Solution_> extends AbstractPrivacyPreservingForager<Solution_> {
    /**
     * Stores encountered candidates that are tabu.
     */
    private List<Solution_> tabuList;

    /**
     * The size of the tabuList.
     */
    private int tabuListSize;

    /**
     * Constructor
     * 
     * @param acceptedCountLimit neighbourhood size
     * @param neighbourhoodEvaluator evaluator of the neighbourhood
     * @param tabuListSize the size of the tabu list
     * @param evaluationType the type of the evaluation
     */
    public PrivacyPreservingTabuSearchForager(int acceptedCountLimit, NeighbourhoodEvaluator<Solution_> neighbourhoodEvaluator,
            int tabuListSize, EvaluationType evaluationType) {
        super(acceptedCountLimit, neighbourhoodEvaluator, evaluationType);
        tabuList = new ArrayList<>();
        this.tabuListSize = tabuListSize;
    }

    // TODO: Rething implementation (Tabulist in Acceptor? Tabulist in conjunction with addMove?)

    /**
     * Implements the Tabu Search algorithm.
     * A Move is accepted, if it is not on the tabu list,
     * or if its score is higher than the score of the current solution.
     * 
     * @param stepWinner the winner of the step
     * @return boolean indicating if winner is accepted
     */
    @Override
    protected boolean isAccepted(LocalSearchMoveScope<Solution_> stepWinner) {
        var undoMove = stepWinner.getMove().doMove(stepWinner.getScoreDirector());
        var winningSolution = stepWinner.getScoreDirector().cloneWorkingSolution();
        undoMove.doMove(stepWinner.getScoreDirector());

        // Move is not tabu
        if (!tabuList.contains(winningSolution)) {
            logger.debug("Accepted winner because it is not tabu.");
            return true;
        }

        // Move is tabu but aspiration
        if (stepWinner.getScore()
                .compareTo(stepWinner.getStepScope().getPhaseScope().getLastCompletedStepScope().getScore()) >= 0) {
            logger.debug("Accepted winner because of aspiration.");
            return true;
        }

        logger.debug("Could not accept winner.");
        return false;
    }

    /**
     * Updates tabu list.
     * 
     * @param stepScope the step scope
     */
    @Override
    public void stepEnded(LocalSearchStepScope<Solution_> stepScope) {
        super.stepEnded(stepScope);
        tabuList.addAll(this.solutionMoveScopeMap.keySet());
        if (tabuList.size() > tabuListSize) {
            tabuList = tabuList.subList(tabuList.size() - tabuListSize, tabuList.size());
        }
    }
}
