package com.banistmo.automation.interactions;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class BranchFilterWaitPolicyTest {

    @Test
    void province_gets_an_adaptive_recovery_window_because_it_is_loaded_after_point_type() {
        BranchFilterWaitPolicy.Plan plan = BranchFilterWaitPolicy.forFilterName("PROVINCE");

        assertThat(plan.initialAttempt()).isEqualTo(Duration.ofSeconds(4));
        assertThat(plan.recoveryAttempt()).isEqualTo(Duration.ofSeconds(8));
        assertThat(plan.hasRecoveryAttempt()).isTrue();
    }

    @Test
    void point_type_keeps_a_short_single_attempt() {
        BranchFilterWaitPolicy.Plan plan = BranchFilterWaitPolicy.forFilterName("POINT_TYPE");

        assertThat(plan.initialAttempt()).isEqualTo(Duration.ofSeconds(5));
        assertThat(plan.recoveryAttempt()).isZero();
        assertThat(plan.hasRecoveryAttempt()).isFalse();
    }
}
