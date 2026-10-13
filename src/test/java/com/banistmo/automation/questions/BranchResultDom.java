package com.banistmo.automation.questions;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Small, bounded DOM reader for the branch cards rendered below the map.
 *
 * <p>Banistmo exposes each branch name as an h5. Reading only these headings
 * and their nearest result container is considerably cheaper than evaluating
 * a page-wide XPath over every DOM node.</p>
 */
public final class BranchResultDom {

    private static final By BRANCH_HEADINGS = By.cssSelector("h5");
    private static final By RESULT_CONTAINER = By.xpath(
            "ancestor::*[self::button or self::article or self::li or self::div]" +
            "[count(.//h5)=1 and contains(normalize-space(.),' - ')][1]"
    );

    private BranchResultDom() {
    }

    public static List<BranchResult> visibleResults(WebDriver driver) {
        List<BranchResult> results = new ArrayList<>();

        for (WebElement heading : driver.findElements(BRANCH_HEADINGS)) {
            try {
                if (!heading.isDisplayed()) {
                    continue;
                }

                String name = safeText(heading);
                if (name.isBlank()) {
                    continue;
                }

                String address = findAddress(heading);
                if (!address.isBlank()) {
                    results.add(new BranchResult(name, address));
                }
            } catch (StaleElementReferenceException ignored) {
                // Results can refresh immediately after changing the province.
                // The next polling cycle will read the stable DOM.
            }
        }

        return results;
    }

    public static List<BranchResult> visibleResultsForProvince(WebDriver driver, String province) {
        String expectedPrefix = BranchResultMetrics.normalize(province) + " -";
        return visibleResults(driver).stream()
                .filter(result -> BranchResultMetrics.normalize(result.address()).startsWith(expectedPrefix))
                .toList();
    }

    public static WebElement firstVisibleHeadingForProvince(WebDriver driver, String province) {
        String expectedPrefix = BranchResultMetrics.normalize(province) + " -";

        for (WebElement heading : driver.findElements(BRANCH_HEADINGS)) {
            try {
                if (heading.isDisplayed()
                        && BranchResultMetrics.normalize(findAddress(heading)).startsWith(expectedPrefix)) {
                    return heading;
                }
            } catch (StaleElementReferenceException ignored) {
                // Caller will retry through WebDriverWait.
            }
        }
        return null;
    }

    private static String findAddress(WebElement heading) {
        List<WebElement> containers = heading.findElements(RESULT_CONTAINER);
        if (!containers.isEmpty()) {
            String address = firstAddressLine(safeText(containers.get(0)));
            if (!address.isBlank()) {
                return address;
            }
        }

        // Defensive fallback for markup where title and address are siblings.
        List<WebElement> siblings = heading.findElements(By.xpath("following-sibling::*[1][not(.//h5) and contains(normalize-space(.),' - ')]"));
        return siblings.isEmpty() ? "" : firstAddressLine(safeText(siblings.get(0)));
    }

    private static String firstAddressLine(String text) {
        return Arrays.stream(text.split("\\R"))
                .map(String::trim)
                .filter(line -> line.contains(" - "))
                .findFirst()
                .orElse("");
    }

    private static String safeText(WebElement element) {
        try {
            String text = element.getText();
            return text == null ? "" : text.trim();
        } catch (RuntimeException ignored) {
            return "";
        }
    }
}
