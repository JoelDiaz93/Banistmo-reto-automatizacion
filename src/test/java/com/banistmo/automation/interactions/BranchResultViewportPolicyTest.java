package com.banistmo.automation.interactions;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BranchResultViewportPolicyTest {

    @Test
    void accepts_a_result_positioned_inside_the_evidence_safe_area() {
        assertThat(BranchResultViewportPolicy.isEvidenceVisible(220, 250, 1000)).isTrue();
    }

    @Test
    void rejects_a_result_hidden_under_the_upper_safe_margin() {
        assertThat(BranchResultViewportPolicy.isEvidenceVisible(40, 70, 1000)).isFalse();
    }

    @Test
    void rejects_a_result_too_close_to_the_bottom_edge() {
        assertThat(BranchResultViewportPolicy.isEvidenceVisible(930, 990, 1000)).isFalse();
    }
}
