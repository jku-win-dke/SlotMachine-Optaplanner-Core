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

package org.optaplanner.core.config.localsearch.decider.forager;

import java.util.function.Consumer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.optaplanner.core.config.AbstractConfig;
import org.optaplanner.core.config.localsearch.decider.acceptor.stepcountinghillclimbing.StepCountingHillClimbingType;
import org.optaplanner.core.config.util.ConfigUtils;

@XmlType(propOrder = {
        "pickEarlyType",
        "acceptedCountLimit",
        "foragerType",
        "evaluationType",
        "neighbourhoodEvaluatorClass",
        "finalistPodiumType",
        "breakTieRandomly",
        "stepCountingHillClimbingSize",
        "stepCountingHillClimbingType",
        "greatDelugeWaterLevelIncrementRatio",
        "simulatedAnnealingStartingTemperature",
        "evaluationThreshold",
        "tabuListSize",
        "topBucketRelativeSize"
})
public class LocalSearchForagerConfig extends AbstractConfig<LocalSearchForagerConfig> {

    protected LocalSearchPickEarlyType pickEarlyType = null;
    protected Integer acceptedCountLimit = null;
    protected FinalistPodiumType finalistPodiumType = null;
    protected Boolean breakTieRandomly = null;
    protected Integer stepCountingHillClimbingSize = null;
    protected StepCountingHillClimbingType stepCountingHillClimbingType = null;
    protected Double evaluationThreshold = null;
    protected Double topBucketRelativeSize = null;
    protected Integer tabuListSize = null;

    protected String simulatedAnnealingStartingTemperature = null;
    protected Double greatDelugeWaterLevelIncrementRatio = null;

    @XmlElement(name = "foragerType")
    private ForagerType foragerType = null;

    @XmlElement(name = "evaluationType")
    private EvaluationType evaluationType = null;

    public String neighbourhoodEvaluatorClass;

    public ForagerType getForagerType() {
        return foragerType;
    }

    public String getNeighbourhoodEvaluatorClass() {
        return neighbourhoodEvaluatorClass;
    }

    public void setNeighbourhoodEvaluatorClass(String neighbourhoodEvaluatorClass) {
        this.neighbourhoodEvaluatorClass = neighbourhoodEvaluatorClass;
    }

    public Double getTopBucketRelativeSize() {
        return topBucketRelativeSize;
    }

    public void setTopBucketRelativeSize(Double topBucketRelativeSize) {
        this.topBucketRelativeSize = topBucketRelativeSize;
    }

    public Integer getTabuListSize() {
        return tabuListSize;
    }

    public void setTabuListSize(Integer tabuListSize) {
        this.tabuListSize = tabuListSize;
    }

    public Double getEvaluationThreshold() {
        return evaluationThreshold;
    }

    public void setEvaluationThreshold(Double evaluationThreshold) {
        this.evaluationThreshold = evaluationThreshold;
    }

    public EvaluationType getEvaluationType() {
        return evaluationType;
    }

    public void setEvaluationType(EvaluationType evaluationType) {
        this.evaluationType = evaluationType;
    }

    public void setForagerType(ForagerType foragerType) {
        this.foragerType = foragerType;
    }

    public LocalSearchPickEarlyType getPickEarlyType() {
        return pickEarlyType;
    }

    public void setPickEarlyType(LocalSearchPickEarlyType pickEarlyType) {
        this.pickEarlyType = pickEarlyType;
    }

    public Integer getAcceptedCountLimit() {
        return acceptedCountLimit;
    }

    public void setAcceptedCountLimit(Integer acceptedCountLimit) {
        this.acceptedCountLimit = acceptedCountLimit;
    }

    public FinalistPodiumType getFinalistPodiumType() {
        return finalistPodiumType;
    }

    public void setFinalistPodiumType(FinalistPodiumType finalistPodiumType) {
        this.finalistPodiumType = finalistPodiumType;
    }

    public Boolean getBreakTieRandomly() {
        return breakTieRandomly;
    }

    public void setBreakTieRandomly(Boolean breakTieRandomly) {
        this.breakTieRandomly = breakTieRandomly;
    }

    public Integer getStepCountingHillClimbingSize() {
        return stepCountingHillClimbingSize;
    }

    public void setStepCountingHillClimbingSize(Integer stepCountingHillClimbingSize) {
        this.stepCountingHillClimbingSize = stepCountingHillClimbingSize;
    }

    public StepCountingHillClimbingType getStepCountingHillClimbingType() {
        return stepCountingHillClimbingType;
    }

    public void setStepCountingHillClimbingType(StepCountingHillClimbingType stepCountingHillClimbingType) {
        this.stepCountingHillClimbingType = stepCountingHillClimbingType;
    }

    public String getSimulatedAnnealingStartingTemperature() {
        return simulatedAnnealingStartingTemperature;
    }

    public void setSimulatedAnnealingStartingTemperature(String simulatedAnnealingStartingTemperature) {
        this.simulatedAnnealingStartingTemperature = simulatedAnnealingStartingTemperature;
    }

    public Double getGreatDelugeWaterLevelIncrementRatio() {
        return greatDelugeWaterLevelIncrementRatio;
    }

    public void setGreatDelugeWaterLevelIncrementRatio(Double greatDelugeWaterLevelIncrementRatio) {
        this.greatDelugeWaterLevelIncrementRatio = greatDelugeWaterLevelIncrementRatio;
    }

    // ************************************************************************
    // With methods
    // ************************************************************************
    public LocalSearchForagerConfig withTopBucketRealtiveSize(Double topBucketRealtiveSize) {
        this.topBucketRelativeSize = topBucketRealtiveSize;
        return this;
    }

    public LocalSearchForagerConfig withTabuListSize(Integer tabuListSize) {
        this.tabuListSize = tabuListSize;
        return this;
    }

    public LocalSearchForagerConfig withPickEarlyType(LocalSearchPickEarlyType pickEarlyType) {
        this.pickEarlyType = pickEarlyType;
        return this;
    }

    public LocalSearchForagerConfig withEvaluationThreshold(Double evaluationThreshold) {
        this.evaluationThreshold = evaluationThreshold;
        return this;
    }

    public LocalSearchForagerConfig withForagerType(ForagerType foragerType) {
        this.foragerType = foragerType;
        return this;
    }

    public LocalSearchForagerConfig withEvaluationType(EvaluationType evaluationType) {
        this.evaluationType = evaluationType;
        return this;
    }

    public LocalSearchForagerConfig
            withNeighbourhoodEvaluatorClass(String neighbourhoodEvaluatorClass) {
        this.neighbourhoodEvaluatorClass = neighbourhoodEvaluatorClass;
        return this;
    }

    public LocalSearchForagerConfig withAcceptedCountLimit(int acceptedCountLimit) {
        this.acceptedCountLimit = acceptedCountLimit;
        return this;
    }

    public LocalSearchForagerConfig withFinalistPodiumType(FinalistPodiumType finalistPodiumType) {
        this.finalistPodiumType = finalistPodiumType;
        return this;
    }

    public LocalSearchForagerConfig withBreakTieRandomly(boolean breakTieRandomly) {
        this.breakTieRandomly = breakTieRandomly;
        return this;
    }

    public LocalSearchForagerConfig withStepCountingHillClimbingSize(Integer stepCountingHillClimbingSize) {
        this.stepCountingHillClimbingSize = stepCountingHillClimbingSize;
        return this;
    }

    public LocalSearchForagerConfig
            withStepCountingHillClimbingType(StepCountingHillClimbingType stepCountingHillClimbingType) {
        this.stepCountingHillClimbingType = stepCountingHillClimbingType;
        return this;
    }

    public LocalSearchForagerConfig withSimulatedAnnealingStartingTemperature(String simulatedAnnealingStartingTemperature) {
        this.simulatedAnnealingStartingTemperature = simulatedAnnealingStartingTemperature;
        return this;
    }

    @Override
    public LocalSearchForagerConfig inherit(LocalSearchForagerConfig inheritedConfig) {
        foragerType = ConfigUtils.inheritOverwritableProperty(foragerType,
                inheritedConfig.getForagerType());
        neighbourhoodEvaluatorClass = ConfigUtils.inheritOverwritableProperty(neighbourhoodEvaluatorClass,
                inheritedConfig.getNeighbourhoodEvaluatorClass());
        pickEarlyType = ConfigUtils.inheritOverwritableProperty(pickEarlyType,
                inheritedConfig.getPickEarlyType());
        acceptedCountLimit = ConfigUtils.inheritOverwritableProperty(acceptedCountLimit,
                inheritedConfig.getAcceptedCountLimit());
        finalistPodiumType = ConfigUtils.inheritOverwritableProperty(finalistPodiumType,
                inheritedConfig.getFinalistPodiumType());
        breakTieRandomly = ConfigUtils.inheritOverwritableProperty(breakTieRandomly,
                inheritedConfig.getBreakTieRandomly());
        stepCountingHillClimbingSize = ConfigUtils.inheritOverwritableProperty(stepCountingHillClimbingSize,
                inheritedConfig.getStepCountingHillClimbingSize());
        stepCountingHillClimbingType = ConfigUtils.inheritOverwritableProperty(stepCountingHillClimbingType,
                inheritedConfig.getStepCountingHillClimbingType());
        greatDelugeWaterLevelIncrementRatio = ConfigUtils.inheritOverwritableProperty(greatDelugeWaterLevelIncrementRatio,
                inheritedConfig.getGreatDelugeWaterLevelIncrementRatio());
        simulatedAnnealingStartingTemperature = ConfigUtils.inheritOverwritableProperty(simulatedAnnealingStartingTemperature,
                inheritedConfig.getSimulatedAnnealingStartingTemperature());
        evaluationType = ConfigUtils.inheritOverwritableProperty(evaluationType,
                inheritedConfig.getEvaluationType());
        evaluationThreshold = ConfigUtils.inheritOverwritableProperty(evaluationThreshold,
                inheritedConfig.getEvaluationThreshold());
        topBucketRelativeSize = ConfigUtils.inheritOverwritableProperty(topBucketRelativeSize,
                inheritedConfig.getTopBucketRelativeSize());
        tabuListSize = ConfigUtils.inheritOverwritableProperty(tabuListSize,
                inheritedConfig.getTabuListSize());
        return this;
    }

    @Override
    public LocalSearchForagerConfig copyConfig() {
        return new LocalSearchForagerConfig().inherit(this);
    }

    @Override
    public void visitReferencedClasses(Consumer<Class<?>> classVisitor) {
        // No referenced classes
    }

}
