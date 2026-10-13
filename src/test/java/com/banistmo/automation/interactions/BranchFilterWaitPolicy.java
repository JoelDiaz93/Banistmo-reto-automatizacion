package com.banistmo.automation.interactions;

import java.time.Duration;

/**
 * Defines bounded, filter-specific synchronization budgets.
 *
 * The point-type filter is immediately available once the branch finder is
 * visible. The province filter is dependent on the point-type selection and
 * Banistmo refreshes its options asynchronously, so it gets a short first
 * attempt plus a recovery attempt that reopens the control if necessary.
 */
final class BranchFilterWaitPolicy {

    private BranchFilterWaitPolicy() {
    }

    static Plan forFilterName(String filterName) {
        if ("PROVINCE".equalsIgnoreCase(filterName)) {
            return new Plan(Duration.ofSeconds(4), Duration.ofSeconds(8));
        }
        return new Plan(Duration.ofSeconds(5), Duration.ZERO);
    }

    record Plan(Duration initialAttempt, Duration recoveryAttempt) {
        boolean hasRecoveryAttempt() {
            return !recoveryAttempt.isZero() && !recoveryAttempt.isNegative();
        }
    }
}
