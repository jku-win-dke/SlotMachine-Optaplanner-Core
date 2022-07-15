package org.optaplanner.core.impl.localsearch.decider.forager.privacypreserving;

import java.util.ArrayList;
import java.util.List;

public class LocalSearchStatistics {
    private List<StepStatistic> steps;
    private Integer iterations;

    public LocalSearchStatistics() {
        this.steps = new ArrayList<>();
    }

    public List<StepStatistic> getSteps() {
        return steps;
    }

    public void setSteps(List<StepStatistic> steps) {
        this.steps = steps;
    }

    public Integer getIterations() {
        return iterations;
    }

    public void setIterations(Integer iterations) {
        this.iterations = iterations;
    }
}
