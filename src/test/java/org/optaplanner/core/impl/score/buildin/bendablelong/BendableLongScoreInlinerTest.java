/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.score.buildin.bendablelong;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.bendablelong.BendableLongScore;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.buildin.AbstractScoreInlinerTest;
import org.optaplanner.core.impl.score.inliner.JustificationsSupplier;
import org.optaplanner.core.impl.score.inliner.UndoScoreImpacter;
import org.optaplanner.core.impl.score.inliner.WeightedScoreImpacter;
import org.optaplanner.core.impl.testdata.domain.score.TestdataBendableLongScoreSolution;

public class BendableLongScoreInlinerTest
        extends AbstractScoreInlinerTest<TestdataBendableLongScoreSolution, BendableLongScore> {

    private static final JustificationsSupplier EMPTY_JUSTIFICATIONS_SUPPLIER = Collections::emptyList;

    @Test
    public void defaultScore() {
        TestConstraint<TestdataBendableLongScoreSolution, BendableLongScore> constraint =
                buildConstraint(buildScore(1, 1, 1));
        BendableLongScoreInliner scoreInliner =
                new BendableLongScoreInliner(getConstaintToWeightMap(constraint), constraintMatchEnabled, 1, 2);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(buildScore(0, 0, 0));
    }

    @Test
    public void impactHard() {
        TestConstraint<TestdataBendableLongScoreSolution, BendableLongScore> constraint =
                buildConstraint(buildScore(90, 0, 0));
        BendableLongScoreInliner scoreInliner =
                new BendableLongScoreInliner(getConstaintToWeightMap(constraint), constraintMatchEnabled, 1, 2);

        WeightedScoreImpacter hardImpacter = scoreInliner.buildWeightedScoreImpacter(constraint);
        UndoScoreImpacter undo1 = hardImpacter.impactScore(1, EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(buildScore(90, 0, 0));

        UndoScoreImpacter undo2 = hardImpacter.impactScore(2, EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(buildScore(270, 0, 0));

        undo2.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(buildScore(90, 0, 0));

        undo1.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(buildScore(0, 0, 0));
    }

    @Test
    public void impactSoft1() {
        TestConstraint<TestdataBendableLongScoreSolution, BendableLongScore> constraint =
                buildConstraint(buildScore(0, 90, 0));
        BendableLongScoreInliner scoreInliner =
                new BendableLongScoreInliner(getConstaintToWeightMap(constraint), constraintMatchEnabled, 1, 2);

        WeightedScoreImpacter hardImpacter = scoreInliner.buildWeightedScoreImpacter(constraint);
        UndoScoreImpacter undo1 = hardImpacter.impactScore(1, EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(buildScore(0, 90, 0));

        UndoScoreImpacter undo2 = hardImpacter.impactScore(2, EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(buildScore(0, 270, 0));

        undo2.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(buildScore(0, 90, 0));

        undo1.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(buildScore(0, 0, 0));
    }

    @Test
    public void impactSoft2() {
        TestConstraint<TestdataBendableLongScoreSolution, BendableLongScore> constraint =
                buildConstraint(buildScore(0, 0, 90));
        BendableLongScoreInliner scoreInliner =
                new BendableLongScoreInliner(getConstaintToWeightMap(constraint), constraintMatchEnabled, 1, 2);

        WeightedScoreImpacter hardImpacter = scoreInliner.buildWeightedScoreImpacter(constraint);
        UndoScoreImpacter undo1 = hardImpacter.impactScore(1, EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(buildScore(0, 0, 90));

        UndoScoreImpacter undo2 = hardImpacter.impactScore(2, EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(buildScore(0, 0, 270));

        undo2.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(buildScore(0, 0, 90));

        undo1.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(buildScore(0, 0, 0));
    }

    @Test
    public void impactAll() {
        TestConstraint<TestdataBendableLongScoreSolution, BendableLongScore> constraint =
                buildConstraint(buildScore(10, 100, 1_000));
        BendableLongScoreInliner scoreInliner =
                new BendableLongScoreInliner(getConstaintToWeightMap(constraint), constraintMatchEnabled, 1, 2);

        WeightedScoreImpacter hardImpacter = scoreInliner.buildWeightedScoreImpacter(constraint);
        UndoScoreImpacter undo1 = hardImpacter.impactScore(10, EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(buildScore(100, 1_000, 10_000));

        UndoScoreImpacter undo2 = hardImpacter.impactScore(20, EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(buildScore(300, 3_000, 30_000));

        undo2.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(buildScore(100, 1_000, 10_000));

        undo1.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(buildScore(0, 0, 0));
    }

    @Override
    protected SolutionDescriptor<TestdataBendableLongScoreSolution> buildSolutionDescriptor() {
        return TestdataBendableLongScoreSolution.buildSolutionDescriptor();
    }

    private BendableLongScore buildScore(long hard, long soft1, long soft2) {
        return BendableLongScore.of(
                new long[] { hard },
                new long[] { soft1, soft2 });
    }

}
