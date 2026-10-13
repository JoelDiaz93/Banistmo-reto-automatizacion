package com.banistmo.automation.interactions;

import com.banistmo.automation.questions.BranchResultDom;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Waits only for the business result we need, using a bounded h5 scan instead
 * of a page-wide XPath. Once stable, it scrolls the first branch result into
 * view so the Serenity step evidence captures the relevant cards.
 */
public class WaitForBranchResults implements Interaction {

    private static final Duration RESULTS_TIMEOUT = Duration.ofSeconds(8);

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
    @Step("{0} espera #expectedCount sucursales para '#province'")
    public <T extends Actor> void performAs(T actor) {
        WebDriver driver = BrowseTheWeb.as(actor).getDriver();

        try {
            new WebDriverWait(driver, RESULTS_TIMEOUT).until(currentDriver ->
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

        WebElement firstResult = BranchResultDom.firstVisibleHeadingForProvince(driver, province);
        if (firstResult != null) {
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({behavior:'auto', block:'center', inline:'nearest'});" +
                    "window.scrollBy(0, -80);",
                    firstResult
            );
        }
    }
}
