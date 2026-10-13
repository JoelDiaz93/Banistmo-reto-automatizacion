package com.banistmo.automation.questions;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BranchResultMetricsTest {

    @Test
    void counts_only_visible_branch_results_for_the_requested_province() {
        List<BranchResult> visibleResults = List.of(
                new BranchResult("Zona Libre", "Colón - Calle 13 En Santa Isabel. Zona Libre De Colón. Plaza Cirebon Peak."),
                new BranchResult("Colón Cristóbal", "Colón - Calle 11 Ave. Cristóbal. Barrio Sur Colón. Al Lado De Panama Ports Company."),
                new BranchResult("Multiplaza", "Panamá - Centro Comercial Multiplaza Centro Bancario.")
        );

        assertThat(BranchResultMetrics.countForProvince(visibleResults, "Colón"))
                .isEqualTo(2);
    }

    @Test
    void province_matching_is_case_and_accent_tolerant() {
        List<BranchResult> visibleResults = List.of(
                new BranchResult("A", "COLÓN - Dirección A"),
                new BranchResult("B", "colon - Dirección B"),
                new BranchResult("C", "Panamá - Dirección C")
        );

        assertThat(BranchResultMetrics.countForProvince(visibleResults, "Colón"))
                .isEqualTo(2);
    }

    @Test
    void validates_branch_name_and_province_in_the_same_result() {
        List<BranchResult> visibleResults = List.of(
                new BranchResult("Zona Libre", "Colón - Calle 13 En Santa Isabel. Zona Libre De Colón. Plaza Cirebon Peak."),
                new BranchResult("Colón Cristóbal", "Colón - Calle 11 Ave. Cristóbal. Barrio Sur Colón.")
        );

        assertThat(BranchResultMetrics.containsBranch(visibleResults, "Zona Libre", "Colón")).isTrue();
        assertThat(BranchResultMetrics.containsBranch(visibleResults, "Zona Libre", "Panamá")).isFalse();
        assertThat(BranchResultMetrics.containsBranch(visibleResults, "Multiplaza", "Colón")).isFalse();
    }
}
