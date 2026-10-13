package com.banistmo.automation.interactions;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ActivateExecutivePymeCtaTest {

    @Test
    void recognizes_the_current_branch_finder_route_as_a_successful_transition() {
        assertThat(ActivateExecutivePymeCta.isBranchFinderUrl(
                "https://www.banistmo.com/personas/buscador-sucursales"
        )).isTrue();
    }

    @Test
    void does_not_treat_the_assistance_page_as_the_branch_finder() {
        assertThat(ActivateExecutivePymeCta.isBranchFinderUrl(
                "https://www.banistmo.com/pymes/seguros/plan-asistencia"
        )).isFalse();
    }
}
