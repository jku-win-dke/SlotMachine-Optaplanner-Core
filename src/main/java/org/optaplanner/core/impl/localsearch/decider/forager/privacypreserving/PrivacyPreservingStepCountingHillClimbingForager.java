package org.optaplanner.core.impl.localsearch.decider.forager.privacypreserving;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.config.localsearch.decider.acceptor.stepcountinghillclimbing.StepCountingHillClimbingType;
import org.optaplanner.core.config.localsearch.decider.forager.EvaluationType;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchMoveScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;

public class PrivacyPreservingStepCountingHillClimbingForager<Solution_> extends AbstractPrivacyPreservingForager<Solution_> {
    /**
     * Defines after how many steps the threshold is updated.
     */
    private final int stepCountingHillClimbingSize;

    /**
     * The current threshold.
     */
    private Score thresholdScore;

    /**
     * Counts the scores since the last update of the threshold.
     */
    private int count = -1;

    /**
     * The type of update.
     */
    protected StepCountingHillClimbingType stepCountingHillClimbingType;

    /**
     * Constructor
     * 
     * @param acceptedCountLimit_ neighbourhood size
     * @param stepCountingHillClimbingSize size of step counting
     * @param stepCountingHillClimbingType_ type of step counting
     * @param evaluator evaluator of neighbourhood
     * @param evaluationType the type of evaluation
     */
    public PrivacyPreservingStepCountingHillClimbingForager(int acceptedCountLimit_, int stepCountingHillClimbingSize,
            StepCountingHillClimbingType stepCountingHillClimbingType_, NeighbourhoodEvaluator<Solution_> evaluator,
            EvaluationType evaluationType) {
        super(acceptedCountLimit_, evaluator, evaluationType);
        this.stepCountingHillClimbingSize = stepCountingHillClimbingSize;
        this.stepCountingHillClimbingType = stepCountingHillClimbingType_;
    }

    /**
     * Implements the Step Counting Hill Climbing algorithm.
     * 
     * @param winner the winner of the step.
     * @return boolean indicating if winner can be accepted.
     */
    @Override
    protected boolean isAccepted(LocalSearchMoveScope<Solution_> winner) {
        if (iterations == 1) {
            thresholdScore = winner.getScore();
            return true;
        }

        currentStepStatistic.setThresholdScore(thresholdScore);

        var score = winner.getScore();
        // Pick move if score gets improved or thresholdScore does not get violated
        if (score.compareTo(winner.getStepScope().getPhaseScope().getLastCompletedStepScope().getScore()) >= 0
                || score.compareTo(thresholdScore) >= 0) {
            logger.debug("Found new winner with score: " + score);
            return true;
        }
        return false;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void phaseStarted(LocalSearchPhaseScope<Solution_> phaseScope) {
        super.phaseStarted(phaseScope);

        // Initialize thresholdscore
        thresholdScore = phaseScope.getBestScore();
        count = 0;
    }

    /**
     * Updates the threshold.
     * 
     * @param stepScope
     */
    @Override
    public void stepEnded(LocalSearchStepScope<Solution_> stepScope) {
        super.stepEnded(stepScope);
        // Update thresholdscore and reset count if necessary
        count += determineCountIncrement(stepScope);
        if (count >= stepCountingHillClimbingSize) {
            thresholdScore = stepScope.getScore();
            count = 0;
        }
    }

    @Override
    public void phaseEnded(LocalSearchPhaseScope<Solution_> phaseScope) {
        super.phaseEnded(phaseScope);
        thresholdScore = null;
        count = -1;
    }

    /**
     * Determines the count increment for every step according to the type.
     * 
     * @param stepScope the step scope
     * @return the increment
     */
    private int determineCountIncrement(LocalSearchStepScope<Solution_> stepScope) {
        switch (stepCountingHillClimbingType) {
            case SELECTED_MOVE: // Increase count by number of selected moves
                long selectedMoveCount = stepScope.getSelectedMoveCount();
                return selectedMoveCount > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) selectedMoveCount;
            case ACCEPTED_MOVE: // Increase count by number of accepted moves
                long acceptedMoveCount = stepScope.getAcceptedMoveCount();
                return acceptedMoveCount > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) acceptedMoveCount;
            case STEP: // Increse count by 1
                return 1;
            case EQUAL_OR_IMPROVING_STEP: // Increase count if score got improved or equal
                return ((Score) stepScope.getScore()).compareTo(
                        stepScope.getPhaseScope().getLastCompletedStepScope().getScore()) >= 0 ? 1 : 0;
            case IMPROVING_STEP: // Increase count by 1 if score got improved
                return ((Score) stepScope.getScore()).compareTo(
                        stepScope.getPhaseScope().getLastCompletedStepScope().getScore()) > 0 ? 1 : 0;
            default:
                throw new IllegalStateException("The stepCountingHillClimbingType (" + stepCountingHillClimbingType
                        + ") is not implemented.");
        }
    }
}
