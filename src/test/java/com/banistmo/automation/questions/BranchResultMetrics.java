package com.banistmo.automation.questions;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;

public final class BranchResultMetrics {

    private BranchResultMetrics() {
    }

    public static int countForProvince(List<String> visibleResultTexts, String province) {
        String expectedPrefix = normalize(province) + " -";

        return (int) visibleResultTexts.stream()
                .map(BranchResultMetrics::normalize)
                .filter(text -> text.startsWith(expectedPrefix))
                .count();
    }

    private static String normalize(String value) {
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
