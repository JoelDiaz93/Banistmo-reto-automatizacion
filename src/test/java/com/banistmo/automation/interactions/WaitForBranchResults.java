package com.banistmo.automation.interactions;

import com.banistmo.automation.questions.BranchResultDom;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Waits for the expected number of business results and then positions the
 * first matching branch card in a stable viewport area for Serenity evidence.
 *
 * <p>The result count is the functional synchronization condition. The later
 * viewport positioning is best-effort evidence handling: it repeatedly
 * re-anchors the first result while the asynchronous map/layout settles, but
 * it never changes a correct business result into a failed test merely because
 * the browser could not hold a specific screenshot position.</p>
 */
public class WaitForBranchResults implements Interaction {

    private static final Duration RESULTS_TIMEOUT = Duration.ofSeconds(8);
    private static final Duration EVIDENCE_TIMEOUT = Duration.ofSeconds(3);
    private static final Duration EVIDENCE_POLLING = Duration.ofMillis(200);
    private static final int REQUIRED_STABLE_POLLS = 3;

    private final int expectedCount;
    private final String province;

    public WaitForBranchResults(int expectedCount, String province) {
        this.expectedCount = expectedCount;
        this.province = province;
    }

    public static WaitForBranchResults count(int expectedCount, String province) {
        return Tasks.instrumented(WaitForBranchResults.class, expectedCount, province);
    }

    @Override
    @Step("{0} espera #expectedCount sucursales para '#province' y prepara la evidencia visual")
    public <T extends Actor> void performAs(T actor) {
        WebDriver driver = BrowseTheWeb.as(actor).getDriver();

        waitForExpectedBusinessResults(driver);
        positionResultsForEvidence(driver);
    }

    private void waitForExpectedBusinessResults(WebDriver driver) {
        try {
            new WebDriverWait(driver, RESULTS_TIMEOUT)
                    .pollingEvery(Duration.ofMillis(200))
                    .ignoring(StaleElementReferenceException.class)
                    .until(currentDriver ->
                            BranchResultDom.visibleResultsForProvince(currentDriver, province).size() == expectedCount
                    );
        } catch (TimeoutException exception) {
            int actualCount = BranchResultDom.visibleResultsForProvince(driver, province).size();
            throw new AssertionError(
                    "La lista de sucursales de '" + province + "' no alcanzó la cantidad esperada. " +
                    "Esperadas=" + expectedCount + ", visibles=" + actualCount,
                    exception
            );
        }
    }

    /**
     * Keeps the first result inside a safe screenshot area for several
     * consecutive polls. If the map finishes loading and pushes the cards down,
     * the next poll repositions the result automatically.
     */
    private void positionResultsForEvidence(WebDriver driver) {
        AtomicInteger stablePolls = new AtomicInteger(0);

        try {
            new WebDriverWait(driver, EVIDENCE_TIMEOUT)
                    .pollingEvery(EVIDENCE_POLLING)
                    .ignoring(StaleElementReferenceException.class)
                    .until(currentDriver -> {
                        WebElement firstResult = BranchResultDom.firstVisibleHeadingForProvince(currentDriver, province);
                        if (firstResult == null) {
                            stablePolls.set(0);
                            return false;
                        }

                        ViewportMetrics metrics = viewportMetrics(currentDriver, firstResult);
                        if (!BranchResultViewportPolicy.isEvidenceVisible(
                                metrics.top(), metrics.bottom(), metrics.viewportHeight())) {
                            scrollResultIntoEvidenceArea(currentDriver, firstResult);
                            stablePolls.set(0);
                            return false;
                        }

                        return stablePolls.incrementAndGet() >= REQUIRED_STABLE_POLLS;
                    });
        } catch (TimeoutException ignored) {
            // Evidence positioning must not invalidate a business assertion that
            // already proved the expected result count. Serenity will still keep
            // the normal end-of-step screenshot.
        }
    }

    private void scrollResultIntoEvidenceArea(WebDriver driver, WebElement result) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({behavior:'auto', block:'center', inline:'nearest'});" +
                "const rect = arguments[0].getBoundingClientRect();" +
                "const desiredTop = Math.min(260, Math.max(160, window.innerHeight * 0.28));" +
                "window.scrollBy(0, rect.top - desiredTop);",
                result
        );
    }

    @SuppressWarnings("unchecked")
    private ViewportMetrics viewportMetrics(WebDriver driver, WebElement result) {
        Object raw = ((JavascriptExecutor) driver).executeScript(
                "const rect = arguments[0].getBoundingClientRect();" +
                "return [rect.top, rect.bottom, window.innerHeight];",
                result
        );

        if (!(raw instanceof List<?> values) || values.size() < 3) {
            return new ViewportMetrics(-1, -1, -1);
        }

        return new ViewportMetrics(
                asDouble(values.get(0)),
                asDouble(values.get(1)),
                asDouble(values.get(2))
        );
    }

    private double asDouble(Object value) {
        return value instanceof Number number ? number.doubleValue() : -1;
    }

    private record ViewportMetrics(double top, double bottom, double viewportHeight) {
    }
}
