package com.banistmo.automation.questions;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BranchResultMetricsTest {

    @Test
    void counts_only_visible_branch_addresses_for_the_requested_province() {
        List<String> visibleResultTexts = List.of(
                "Colón - Calle 13 En Santa Isabel. Zona Libre De Colón. Plaza Cirebon Peak.",
                "Colón - Calle 11 Ave. Cristóbal. Barrio Sur Colón. Al Lado De Panama Ports Company.",
                "Panamá - Centro Comercial Multiplaza Centro Bancario.",
                "Colón Cristóbal"
        );

        assertThat(BranchResultMetrics.countForProvince(visibleResultTexts, "Colón"))
                .isEqualTo(2);
    }

    @Test
    void province_matching_is_case_and_accent_tolerant() {
        List<String> visibleResultTexts = List.of(
                "COLÓN - Dirección A",
                "colon - Dirección B",
                "Panamá - Dirección C"
        );

        assertThat(BranchResultMetrics.countForProvince(visibleResultTexts, "Colón"))
                .isEqualTo(2);
    }
}
