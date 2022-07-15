package org.optaplanner.core.impl.localsearch.decider.forager.privacypreserving;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.config.localsearch.decider.forager.EvaluationType;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchMoveScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;

/**
 * Forager implementing the GreatDeluge logarithm.
 * 
 * @param <Solution_>
 */
public class PrivacyPreservingGreatDelugeForager<Solution_> extends AbstractPrivacyPreservingForager<Solution_> {
    /**
     * The configured initial threshold.
     */
    private Score initialWaterLevel;

    /**
     * The increment for each step.
     */
    private Score waterLevelIncrementScore;

    /**
     * The ratio of the initialWaterLevel used as increment each step.
     */
    private Double waterLevelIncrementRatio;

    /**
     * The starting threshold.
     */
    private Score startingWaterLevel = null;

    /**
     * The current threshold.
     */
    private Score currentWaterLevel = null;

    /**
     * The current ratio used to determine the next increase of the threshold.
     */
    private Double currentWaterLevelRatio = null;

    /**
     * Flag indicating if starting level has been initialized.
     */
    boolean initializedStartingLevel = false;

    public PrivacyPreservingGreatDelugeForager(int acceptedCountLimit_,
            NeighbourhoodEvaluator<Solution_> neighbourhoodEvaluator, EvaluationType evaluationType) {
        super(acceptedCountLimit_, neighbourhoodEvaluator, evaluationType);
    }

    public Score getWaterLevelIncrementScore() {
        return this.waterLevelIncrementScore;
    }

    public void setWaterLevelIncrementScore(Score waterLevelIncrementScore) {
        this.waterLevelIncrementScore = waterLevelIncrementScore;
    }

    public Score getInitialWaterLevel() {
        return this.initialWaterLevel;
    }

    public void setInitialWaterLevel(Score initialLevel) {
        this.initialWaterLevel = initialLevel;
    }

    public Double getWaterLevelIncrementRatio() {
        return this.waterLevelIncrementRatio;
    }

    public void setWaterLevelIncrementRatio(Double waterLevelIncrementRatio) {
        this.waterLevelIncrementRatio = waterLevelIncrementRatio;
    }

    /**
     * Implements the GreatDeluge logarithm.
     * The winner of the step is accepted if its score surpasses the current threshold/waterlevel,
     * or if its score surpasses the score of the last step.
     * 
     * @param stepWinner the winner of the step
     * @return boolean indicating if step winner is accepted.
     */
    @Override
    protected boolean isAccepted(LocalSearchMoveScope<Solution_> stepWinner) {
        if (!initializedStartingLevel) {
            startingWaterLevel = stepWinner.getScore();
            currentWaterLevel = startingWaterLevel;
            waterLevelIncrementScore = startingWaterLevel.multiply(waterLevelIncrementRatio);
            var scoreLevels = waterLevelIncrementScore.toLevelNumbers();
            var isNegative = false;
            for (int i = 0; i < scoreLevels.length; i++) {
                if (scoreLevels[i].doubleValue() < 0)
                    isNegative = true;
            }
            if (isNegative)
                waterLevelIncrementScore = waterLevelIncrementScore.negate();
            initializedStartingLevel = true;
            return true;
        }
        currentStepStatistic.setThresholdScore(currentWaterLevel);

        Score moveScore = stepWinner.getScore();
        if (moveScore.compareTo(currentWaterLevel) >= 0) {
            logger.debug("Found new winner with score " + moveScore + "(waterlevel was " + currentWaterLevel + ")");
            return true;
        }
        Score lastStepScore = stepWinner.getStepScope().getPhaseScope().getLastCompletedStepScope().getScore();
        if (moveScore.compareTo(lastStepScore) > 0) {
            // Aspiration
            logger.debug("Accepted aspirating step.");
            return true;
        }
        return false;
    }

    /**
     * Initializes the fields
     * 
     * @param phaseScope the phase scope
     */
    @Override
    public void phaseStarted(LocalSearchPhaseScope<Solution_> phaseScope) {
        super.phaseStarted(phaseScope);
        if (initialWaterLevel != null) {
            startingWaterLevel = initialWaterLevel;
            currentWaterLevel = startingWaterLevel;
            initializedStartingLevel = true;
            waterLevelIncrementScore = startingWaterLevel.multiply(waterLevelIncrementRatio);
        }
        if (waterLevelIncrementRatio != null) {
            currentWaterLevelRatio = 0.0;
        }
    }

    @Override
    public void phaseEnded(LocalSearchPhaseScope<Solution_> phaseScope) {
        super.phaseEnded(phaseScope);
        startingWaterLevel = null;
        if (waterLevelIncrementRatio != null) {
            currentWaterLevelRatio = null;
        }
        currentWaterLevel = null;
    }

    /**
     * Updates the current threshold.
     * 
     * @param stepScope the step scope
     */
    @Override
    public void stepEnded(LocalSearchStepScope<Solution_> stepScope) {
        super.stepEnded(stepScope);
        if (waterLevelIncrementScore != null) {
            currentWaterLevel = currentWaterLevel.add(waterLevelIncrementScore);
        } else {
            // Avoid numerical instability: SimpleScore.of(500).multiply(0.000_001) underflows to zero
            currentWaterLevelRatio += waterLevelIncrementRatio;
            currentWaterLevel = startingWaterLevel.add(
                    // TODO targetWaterLevel.subtract(startingWaterLevel).multiply(waterLevelIncrementRatio);
                    // The startingWaterLevel.negate() is short for zeroScore.subtract(startingWaterLevel)
                    startingWaterLevel.negate().multiply(currentWaterLevelRatio));
        }
    }

}
