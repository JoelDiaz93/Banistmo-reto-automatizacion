package com.banistmo.automation.interactions;

/**
 * Defines the viewport area considered suitable for Serenity evidence.
 *
 * <p>The margins keep the first branch result away from sticky headers and
 * the browser edge, so the screenshot shows the business result instead of
 * only the map/filter area.</p>
 */
public final class BranchResultViewportPolicy {

    static final double TOP_SAFE_MARGIN = 120.0;
    static final double BOTTOM_SAFE_MARGIN = 80.0;

    private BranchResultViewportPolicy() {
    }

    public static boolean isEvidenceVisible(double top, double bottom, double viewportHeight) {
        return viewportHeight > 0
                && top >= TOP_SAFE_MARGIN
                && bottom <= viewportHeight - BOTTOM_SAFE_MARGIN
                && bottom > top;
    }
}
