package com.banistmo.automation.questions;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;

public final class BranchResultMetrics {

    private BranchResultMetrics() {
    }

    public static int countForProvince(List<BranchResult> visibleResults, String province) {
        String expectedPrefix = normalize(province) + " -";

        return (int) visibleResults.stream()
                .map(BranchResult::address)
                .map(BranchResultMetrics::normalize)
                .filter(text -> text.startsWith(expectedPrefix))
                .count();
    }

    public static boolean containsBranch(List<BranchResult> visibleResults, String branchName, String province) {
        String expectedBranch = normalize(branchName);
        String expectedProvincePrefix = normalize(province) + " -";

        return visibleResults.stream().anyMatch(result ->
                normalize(result.name()).equals(expectedBranch)
                        && normalize(result.address()).startsWith(expectedProvincePrefix)
        );
    }

    static String normalize(String value) {
        if (value == null) {
            return "";
        }

        String withoutAccents = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");

        return withoutAccents
                .replace('\u00A0', ' ')
                .trim()
                .replaceAll("\\s+", " ")
                .toLowerCase(Locale.ROOT);
    }
}
