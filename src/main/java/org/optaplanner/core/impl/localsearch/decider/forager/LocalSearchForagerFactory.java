/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.localsearch.decider.forager;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.config.localsearch.decider.acceptor.stepcountinghillclimbing.StepCountingHillClimbingType;
import org.optaplanner.core.config.localsearch.decider.forager.*;
import org.optaplanner.core.impl.localsearch.decider.forager.privacypreserving.*;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;

public class LocalSearchForagerFactory<Solution_> {

    public static <Solution_> LocalSearchForagerFactory<Solution_> create(LocalSearchForagerConfig foragerConfig) {
        return new LocalSearchForagerFactory<>(foragerConfig);
    }

    private final LocalSearchForagerConfig foragerConfig;
    private NeighbourhoodEvaluator<Solution_> neighbourhoodEvaluator;
    private ScoreDefinition scoreDefinition;
    private LocalSearchStatistics localSearchStatistics;
    private Double terminationFitness = Double.MAX_VALUE;

    public LocalSearchForagerFactory(LocalSearchForagerConfig foragerConfig) {
        this.foragerConfig = foragerConfig;
    }

    public LocalSearchForager<Solution_> buildForager() {
        if (foragerConfig.getForagerType() != null)
            return buildCustomForager();
        LocalSearchPickEarlyType pickEarlyType_ =
                Objects.requireNonNullElse(foragerConfig.getPickEarlyType(), LocalSearchPickEarlyType.NEVER);
        int acceptedCountLimit_ = Objects.requireNonNullElse(foragerConfig.getAcceptedCountLimit(), Integer.MAX_VALUE);
        FinalistPodiumType finalistPodiumType_ =
                Objects.requireNonNullElse(foragerConfig.getFinalistPodiumType(), FinalistPodiumType.HIGHEST_SCORE);
        // Breaking ties randomly leads statistically to much better results
        boolean breakTieRandomly_ = Objects.requireNonNullElse(foragerConfig.getBreakTieRandomly(), true);
        return new AcceptedLocalSearchForager<>(finalistPodiumType_.buildFinalistPodium(), pickEarlyType_,
                acceptedCountLimit_, breakTieRandomly_);
    }

    private LocalSearchForager<Solution_> buildCustomForager() {
        int acceptedCountLimit_ = Objects.requireNonNullElse(foragerConfig.getAcceptedCountLimit(), 50);
        EvaluationType evaluationType =
                Objects.requireNonNullElse(foragerConfig.getEvaluationType(), EvaluationType.BEST_CANDIDATE);

        try {
            neighbourhoodEvaluator = (NeighbourhoodEvaluator<Solution_>) Class
                    .forName(foragerConfig.getNeighbourhoodEvaluatorClass()).getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException
                | InstantiationException e) {
            throw new IllegalArgumentException(
                    "Could not load NeighbourhoodEvaluator-class although forager type has been set. Nested exception is:"
                            + e.getMessage());
        }
        AbstractPrivacyPreservingForager forager = null;
        if (foragerConfig.getForagerType() == ForagerType.PP_HILL_CLIMBING) {
            forager = new PrivacyPreservingHillClimbingForager<>(acceptedCountLimit_, neighbourhoodEvaluator, evaluationType);
        } else if (foragerConfig.getForagerType() == ForagerType.PP_STEP_COUNTING_HILL_CLIMBING) {
            forager = new PrivacyPreservingStepCountingHillClimbingForager<>(acceptedCountLimit_,
                    Objects.requireNonNullElse(foragerConfig.getStepCountingHillClimbingSize(), 20),
                    Objects.requireNonNullElse(foragerConfig.getStepCountingHillClimbingType(),
                            StepCountingHillClimbingType.STEP),
                    neighbourhoodEvaluator, evaluationType);
        } else if (foragerConfig.getForagerType() == ForagerType.PP_SIMULATED_ANNEALING) {
            Score startingTemperature =
                    foragerConfig.getSimulatedAnnealingStartingTemperature() != null && scoreDefinition != null
                            ? scoreDefinition.parseScore(foragerConfig.getSimulatedAnnealingStartingTemperature())
                            : HardSoftScore.of(0, 500);
            forager = new PrivacyPreservingSimulatedAnnealingForager<>(acceptedCountLimit_, startingTemperature,
                    neighbourhoodEvaluator, evaluationType);
        } else if (foragerConfig.getForagerType() == ForagerType.PP_GREAT_DELUGE) {
            forager = new PrivacyPreservingGreatDelugeForager<>(acceptedCountLimit_, neighbourhoodEvaluator, evaluationType);
            ((PrivacyPreservingGreatDelugeForager) forager).setWaterLevelIncrementRatio(
                    Objects.requireNonNullElse(foragerConfig.getGreatDelugeWaterLevelIncrementRatio(), 0.005));
        } else {
            Integer tabuListSize = Objects.requireNonNullElse(foragerConfig.getTabuListSize(), 1000);
            forager = new PrivacyPreservingTabuSearchForager<>(acceptedCountLimit_, neighbourhoodEvaluator, tabuListSize,
                    evaluationType);
        }
        var aboveAbsoluteThreshold = Objects.requireNonNullElse(foragerConfig.getEvaluationThreshold(), 0.0);
        var topBucketSize = Objects.requireNonNullElse(foragerConfig.getTopBucketRelativeSize(), 0.1);
        forager.setTopThreshold(topBucketSize);
        forager.setEvaluationThreshold(aboveAbsoluteThreshold);
        forager.setLocalSearchStatistics(localSearchStatistics);
        forager.setTerminationFitness(terminationFitness);
        return forager;
    }

    public ScoreDefinition getScoreDefinition() {
        return scoreDefinition;
    }

    public void setScoreDefinition(ScoreDefinition scoreDefinition) {
        this.scoreDefinition = scoreDefinition;
    }

    public void setLocalSearchStatistics(LocalSearchStatistics localSearchStatistics) {
        this.localSearchStatistics = localSearchStatistics;
    }

    public Double getTerminationFitness() {
        return terminationFitness;
    }

    public void setTerminationFitness(Double terminationFitness) {
        this.terminationFitness = terminationFitness;
    }
}
