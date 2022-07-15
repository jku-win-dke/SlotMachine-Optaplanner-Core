package org.optaplanner.core.impl.localsearch.decider.forager.privacypreserving;

import java.util.List;
import java.util.Map;

import org.optaplanner.core.api.score.Score;

/**
 * Interface used to evaluate the neighbourhood gathered by a {@link AbstractPrivacyPreservingForager}
 * 
 * @param <Solution_> generic solution
 */
public interface NeighbourhoodEvaluator<Solution_> {
    /**
     * Returns the best solution in the neighbourhood.
     * 
     * @param candidates the candidates
     * @return the best {@link Solution_} and its {@link Score}
     */
    Map<Score, Solution_> getBestSolutionFromNeighbourhood(List<Solution_> candidates);

    /**
     * Returns the candidates above a defined threshold compared to the maximum fitness of the neighbourhood.
     * 
     * @param candidates the neighbourhood
     * @param threshold the threshold (0-1)
     * @param terminationFitness
     * @return the {@link Solution_}s above the threshold and the AVERAGE score of the solutions.
     */
    Map<Score, List<Solution_>> getCandidatesAboveThreshold(List<Solution_> candidates, double threshold,
            Double terminationFitness);

    /**
     * Returns the Top candidates of the neighbourhood and the average score of these.
     * 
     * @param candidates the neighbourhood
     * @param threshold the percentage of the neighbourhood size that shall be returned
     * @param terminationFitness
     * @return the top {@link Solution_} and the AVERAGE score of these solutions
     */
    Map<Score, List<Solution_>> getTopCandidatesAndAverageScore(List<Solution_> candidates, double threshold,
            Double terminationFitness);
}
