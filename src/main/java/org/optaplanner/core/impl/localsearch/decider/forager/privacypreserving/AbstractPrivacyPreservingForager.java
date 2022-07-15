package org.optaplanner.core.impl.localsearch.decider.forager.privacypreserving;

import java.util.*;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.config.localsearch.decider.forager.EvaluationType;
import org.optaplanner.core.impl.localsearch.decider.forager.LocalSearchForager;
import org.optaplanner.core.impl.localsearch.event.LocalSearchPhaseLifecycleListenerAdapter;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchMoveScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A privacy preserving forager that uses the {@link NeighbourhoodEvaluator} to evaluate the accumulated candidates of the
 * search steps.
 * This class can be extended to implement different selection mechanisms, by implementing
 * {@link #isAccepted(LocalSearchMoveScope)}.
 * 
 * @param <Solution_> generic solution
 */
public abstract class AbstractPrivacyPreservingForager<Solution_> extends LocalSearchPhaseLifecycleListenerAdapter<Solution_>
        implements LocalSearchForager<Solution_> {
    protected final transient Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * The statistics that are generated during the search.
     */
    private LocalSearchStatistics localSearchStatistics;
    /**
     * The statistic of the current step.
     */
    protected StepStatistic currentStepStatistic;
    /**
     * The maximum fitness which can be set and is passed to the Evaluation.
     */
    private Double terminationFitness = Double.MAX_VALUE;

    /**
     * Specifies how many moves are gathered before a winner is picked.
     */
    private final int acceptedCountLimit;

    /**
     * Used to count all moves suggested to the forager in one step.
     */
    private long selectedMoveCount;
    /**
     * Used to count all moves accepted by the forager in one step.
     */
    private long acceptedMoveCount;

    /**
     * Used to evaluate the neighbourhood in each step.
     */
    private final NeighbourhoodEvaluator<Solution_> neighbourhoodEvaluator;

    /**
     * Used to execute suggested moves to retain a solution that can be evaluated by the {@link NeighbourhoodEvaluator}
     */
    private InnerScoreDirector<Solution_, ?> scoreDirector;

    /**
     * Helper field storing the move-scope that has been picked latest.
     */
    private LocalSearchMoveScope<Solution_> lastPickedMoveScope;

    /**
     * Stores the suggested moves in each step.
     */
    private List<LocalSearchMoveScope<Solution_>> candidateMoveScopes;

    /**
     * Maps the solution(required by evaluator) to corresponding moves(required internally).
     */
    protected Map<Solution_, LocalSearchMoveScope<Solution_>> solutionMoveScopeMap;

    /**
     * Helper field counting overall iterations.
     */
    protected int iterations;

    /**
     * Sets the {@link EvaluationType}
     */
    private final EvaluationType evaluationType;

    /**
     * Threshold for the {@link EvaluationType#ABOVE_THRESHOLD}
     */
    private double evaluationThreshold;

    /**
     * Threshold for the {@link EvaluationType#TOP}
     */
    private double topThreshold;

    protected AbstractPrivacyPreservingForager() {
        this(50, null, EvaluationType.BEST_CANDIDATE);
    }

    /**
     * Constructor
     * 
     * @param acceptedCountLimit size of neighbourhood
     * @param neighbourhoodEvaluator evaluator of neighbourhood
     * @param evaluationType type of evaluation
     */
    protected AbstractPrivacyPreservingForager(int acceptedCountLimit,
            NeighbourhoodEvaluator<Solution_> neighbourhoodEvaluator, EvaluationType evaluationType) {
        logger.debug("Initialized " + this.getClass());
        this.acceptedCountLimit = acceptedCountLimit;

        this.candidateMoveScopes = new ArrayList<>();
        this.solutionMoveScopeMap = new HashMap<>();
        this.lastPickedMoveScope = null;
        this.neighbourhoodEvaluator = neighbourhoodEvaluator;
        this.evaluationType = evaluationType;
        this.evaluationThreshold = 0.98; // default
        this.topThreshold = 0.02; // default
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************
    @Override
    public void phaseStarted(LocalSearchPhaseScope<Solution_> phaseScope) {
        super.phaseStarted(phaseScope);
        this.iterations = 0;
        scoreDirector = phaseScope.getScoreDirector();
    }

    /**
     * Resets the move counts and clears collection before each step.
     * 
     * @param stepScope the step scope
     */
    @Override
    public void stepStarted(LocalSearchStepScope<Solution_> stepScope) {
        super.stepStarted(stepScope);
        selectedMoveCount = 0L;
        acceptedMoveCount = 0L;

        candidateMoveScopes.clear();
        solutionMoveScopeMap.clear();
        iterations++;
        currentStepStatistic = new StepStatistic();
        localSearchStatistics.getSteps().add(currentStepStatistic);
    }

    /**
     * Required to check compliance with a never ending move selector, otherwise steps would never end.
     * 
     * @return boolean indicating if acceptedCountLimit has been set
     */
    @Override
    public boolean supportsNeverEndingMoveSelector() {
        // TODO FIXME magical value Integer.MAX_VALUE coming from ForagerConfig
        return acceptedCountLimit < Integer.MAX_VALUE;
    }

    /**
     * Adds the move if accepted by the {@link org.optaplanner.core.impl.localsearch.decider.acceptor.Acceptor}.
     * 
     * @param moveScope never null
     */
    @Override
    public void addMove(LocalSearchMoveScope<Solution_> moveScope) {
        selectedMoveCount++;
        if (Boolean.TRUE.equals(moveScope.getAccepted())) {
            acceptedMoveCount++;
            candidateMoveScopes.add(moveScope);
        }
    }

    /**
     * Checks if enough moves have been gathered according to the limit of accepted moves.
     * 
     * @return boolean indicating if limit has been reached
     */
    @Override
    public boolean isQuitEarly() {
        return acceptedMoveCount >= acceptedCountLimit;
    }

    /**
     * Picks a move from all the candidates for the next step.
     * 
     * @param stepScope the scope of the step
     * @return the winning move
     */
    @Override
    public LocalSearchMoveScope<Solution_> pickMove(LocalSearchStepScope<Solution_> stepScope) {
        stepScope.setSelectedMoveCount(selectedMoveCount);
        stepScope.setAcceptedMoveCount(acceptedMoveCount);

        // Request the evaluation from the privacy engine
        LocalSearchMoveScope<Solution_> winner = getStepWinner();

        // Initialize statistic for step
        currentStepStatistic.setStepIndex(stepScope.getStepIndex());

        // Return winner if accepted
        if (this.isAccepted(winner)) {
            this.lastPickedMoveScope = winner;
            currentStepStatistic.setFoundNewSolution(true);
            currentStepStatistic.setStepScore(winner != null ? winner.getScore() : null);
            return winner;
        }
        currentStepStatistic.setFoundNewSolution(false);
        currentStepStatistic.setStepScore(lastPickedMoveScope.getScore());

        // Else return current winner
        logger.error("REACHED LOCAL OPTIMUM, RESTARTING THE STEP!");
        return lastPickedMoveScope;
    }

    @Override
    public void phaseEnded(LocalSearchPhaseScope<Solution_> phaseScope) {
        super.phaseEnded(phaseScope);
        selectedMoveCount = 0L;
        acceptedMoveCount = 0L;
        this.localSearchStatistics.setIterations(iterations);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + acceptedCountLimit + ")";
    }

    // ************************************************************************
    // Custom Methods
    // ************************************************************************

    /**
     * Abstract method that is used to determine whether the winning move of a step,
     * can be accepted as basis for the next step.
     * 
     * @return the winning move scope or null
     */
    protected abstract boolean isAccepted(LocalSearchMoveScope<Solution_> stepWinner);

    /**
     * Initializes required collections, evaluates the neighbourhood and returns the winning move in each step.
     */
    private LocalSearchMoveScope<Solution_> getStepWinner() {
        mapCandidateSolutionsToMoveScopes();
        return evaluateStepCandidates();
    }

    /**
     * Evaluates the neighbourhood and returns the winning move.
     * 
     * @return the winning move
     */
    private LocalSearchMoveScope<Solution_> evaluateStepCandidates() {
        List<Solution_> candidates = new ArrayList<>(solutionMoveScopeMap.keySet());
        Map<Score, Solution_> stepWinner;
        // Evaluate neighbourhood according to type of evaluation.
        if (evaluationType == EvaluationType.ABOVE_THRESHOLD) {
            // Trigger evaluation
            Map<Score, List<Solution_>> aboveThresholdMap =
                    neighbourhoodEvaluator.getCandidatesAboveThreshold(candidates, evaluationThreshold, terminationFitness);
            List<Solution_> aboveThresholdCandidates = aboveThresholdMap.entrySet().stream().findFirst().get().getValue();
            Score averageScore = aboveThresholdMap.entrySet().stream().findFirst().get().getKey();

            // Chose random candidate above threshold
            Random random = new Random();
            int index = random.nextInt(aboveThresholdCandidates.size());
            Solution_ randomCandidate = aboveThresholdCandidates.get(index);

            stepWinner = new HashMap<>();
            stepWinner.put(averageScore, randomCandidate);

            logger.debug("Picked candidate " + (index + 1) + " of " + aboveThresholdCandidates.size()
                    + " candidates above threshold as step winner.");
            currentStepStatistic.setBucketSize(aboveThresholdCandidates.size());
        } else if (evaluationType == EvaluationType.BEST_CANDIDATE) {
            // Get best candidate
            stepWinner = neighbourhoodEvaluator.getBestSolutionFromNeighbourhood(candidates);
        } else { // TOP
            // Trigger evaluation
            Map<Score, List<Solution_>> topCandidatesMap =
                    neighbourhoodEvaluator.getTopCandidatesAndAverageScore(candidates, topThreshold, terminationFitness);
            List<Solution_> topCandidates = topCandidatesMap.entrySet().stream().findFirst().get().getValue();
            Score averageScore = topCandidatesMap.entrySet().stream().findFirst().get().getKey();

            // Get random candidate of top-bucket
            Random random = new Random();
            int index = random.nextInt(topCandidates.size());
            Solution_ randomCandidate = topCandidates.get(index);

            stepWinner = new HashMap<>();
            stepWinner.put(averageScore, randomCandidate);

            logger.debug("Picked candidate " + (index + 1) + " of " + topCandidates.size() + " top candidates as step winner.");
            currentStepStatistic.setBucketSize(topCandidates.size());
        }

        Optional<Map.Entry<Score, Solution_>> entry = stepWinner.entrySet().stream().findFirst();

        if (!entry.isPresent())
            return null;

        Solution_ solution = entry.get().getValue();
        Score score = entry.get().getKey();

        // Get move scope corresponding to winning solution (data transformation).
        LocalSearchMoveScope<Solution_> moveScope = this.solutionMoveScopeMap.get(solution);
        moveScope.setScore(score);

        return moveScope;
    }

    /**
     * Initializes the collections for each step required for communication with the privacy-engine
     */
    private void mapCandidateSolutionsToMoveScopes() {
        // Iterate over the accepted move-scopes of the step
        for (var moveScope : candidateMoveScopes) {
            // Execute the move
            var undoMove = moveScope.getMove().doMove(scoreDirector);

            // Map the flight-prioritization to the index of the move
            solutionMoveScopeMap.put(scoreDirector.cloneWorkingSolution(), moveScope);

            // Undo the move
            undoMove.doMove(scoreDirector);
        }
    }

    public double getEvaluationThreshold() {
        return evaluationThreshold;
    }

    public void setEvaluationThreshold(double evaluationThreshold) {
        this.evaluationThreshold = evaluationThreshold;
    }

    public double getTopThreshold() {
        return topThreshold;
    }

    public void setTopThreshold(double topThreshold) {
        this.topThreshold = topThreshold;
    }

    public void setLocalSearchStatistics(LocalSearchStatistics localSearchStatistics) {
        this.localSearchStatistics = localSearchStatistics;
    }

    public void setTerminationFitness(Double terminationFitness) {
        this.terminationFitness = terminationFitness;
    }
}
