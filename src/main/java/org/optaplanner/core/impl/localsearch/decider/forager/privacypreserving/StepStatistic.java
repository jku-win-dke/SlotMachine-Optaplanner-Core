package org.optaplanner.core.impl.localsearch.decider.forager.privacypreserving;

import org.optaplanner.core.api.score.Score;

public class StepStatistic {
    private Score stepScore;
    private Score thresholdScore;
    private Integer bucketSize;
    private Integer stepIndex;
    private boolean foundNewSolution;

    public StepStatistic() {

    }

    public Score getStepScore() {
        return stepScore;
    }

    public void setStepScore(Score stepScore) {
        this.stepScore = stepScore;
    }

    public Score getThresholdScore() {
        return thresholdScore;
    }

    public void setThresholdScore(Score thresholdScore) {
        this.thresholdScore = thresholdScore;
    }

    public Integer getBucketSize() {
        return bucketSize;
    }

    public void setBucketSize(Integer bucketSize) {
        this.bucketSize = bucketSize;
    }

    public Integer getStepIndex() {
        return stepIndex;
    }

    public void setStepIndex(Integer stepIndex) {
        this.stepIndex = stepIndex;
    }

    public boolean isFoundNewSolution() {
        return foundNewSolution;
    }

    public void setFoundNewSolution(boolean foundNewSolution) {
        this.foundNewSolution = foundNewSolution;
    }
}
