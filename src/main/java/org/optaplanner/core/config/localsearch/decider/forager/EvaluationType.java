package org.optaplanner.core.config.localsearch.decider.forager;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum EvaluationType {
    BEST_CANDIDATE,
    ABOVE_THRESHOLD,
    TOP
}
