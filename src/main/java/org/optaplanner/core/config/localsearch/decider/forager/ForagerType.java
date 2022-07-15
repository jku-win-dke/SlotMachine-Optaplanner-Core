package org.optaplanner.core.config.localsearch.decider.forager;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum ForagerType {
    PP_HILL_CLIMBING,
    PP_STEP_COUNTING_HILL_CLIMBING,
    PP_SIMULATED_ANNEALING,
    PP_GREAT_DELUGE,
    PP_TABU_SEARCH
}
