package com.banistmo.automation.interactions;

import com.banistmo.automation.userinterfaces.BranchFinder;
import com.banistmo.automation.userinterfaces.PymesPage;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

/**
 * Activates the "Solicítalo con tu Ejecutivo Pyme" CTA and verifies that the
 * click actually produces a transition towards the branch finder.
 *
 * <p>The Banistmo page renders this CTA inside a dynamic carousel. A normal
 * Selenium click is attempted first. If the DOM receives the click but the
 * expected transition is not observable, the same visible CTA is activated
 * once more using the DOM click API. This is a click-recovery strategy, not a
 * navigation fallback: the test never opens the branch-finder URL directly.</p>
 */
public class ActivateExecutivePymeCta implements Interaction {

    private static final Duration NATIVE_CLICK_RESULT_WAIT = Duration.ofSeconds(5);
    private static final Duration DOM_CLICK_RESULT_WAIT = Duration.ofSeconds(8);

    public static ActivateExecutivePymeCta andWaitForBranchFinder() {
        return new ActivateExecutivePymeCta();
    }

    @Override
    @Step("{0} selecciona Solicítalo con tu Ejecutivo Pyme y espera la apertura del buscador")
    public <T extends Actor> void performAs(T actor) {
        WebDriver driver = BrowseTheWeb.as(actor).getDriver();
        Set<String> windowsBefore = new HashSet<>(driver.getWindowHandles());
        String urlBefore = driver.getCurrentUrl();

        WebElement cta = PymesPage.SOLICITALO_CON_EJECUTIVO.resolveFor(actor);
        scrollToCenter(driver, cta);

        String diagnostic = describe(cta);

        // First choice: a genuine WebDriver user click.
        cta.click();
        if (waitForBranchFinder(driver, windowsBefore, NATIVE_CLICK_RESULT_WAIT)) {
            return;
        }

        // The carousel can re-render the CTA after the first click, so resolve
        // the visible element again before using the DOM click recovery.
        WebElement refreshedCta = PymesPage.SOLICITALO_CON_EJECUTIVO.resolveFor(actor);
        scrollToCenter(driver, refreshedCta);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", refreshedCta);

        if (waitForBranchFinder(driver, windowsBefore, DOM_CLICK_RESULT_WAIT)) {
            return;
        }

        throw new AssertionError(
                "El CTA 'Solicítalo con tu Ejecutivo Pyme' fue activado, pero Banistmo no abrió el buscador de sucursales. " +
                "URL antes='" + urlBefore + "', URL después='" + driver.getCurrentUrl() + "', " +
                "ventanas antes=" + windowsBefore.size() + ", ventanas después=" + driver.getWindowHandles().size() + ". " +
                "Elemento CTA: " + diagnostic
        );
    }

    private boolean waitForBranchFinder(WebDriver driver, Set<String> windowsBefore, Duration timeout) {
        try {
            return new WebDriverWait(driver, timeout).until(currentDriver -> {
                switchToNewWindowIfPresent(currentDriver, windowsBefore);
                return branchFinderIsObservable(currentDriver);
            });
        } catch (RuntimeException ignored) {
            return false;
        }
    }

    private void switchToNewWindowIfPresent(WebDriver driver, Set<String> windowsBefore) {
        driver.getWindowHandles().stream()
                .filter(handle -> !windowsBefore.contains(handle))
                .findFirst()
                .ifPresent(handle -> driver.switchTo().window(handle));
    }

    private boolean branchFinderIsObservable(WebDriver driver) {
        driver.switchTo().defaultContent();

        if (isBranchFinderUrl(driver.getCurrentUrl()) || headingIsVisible(driver)) {
            return true;
        }

        for (WebElement frame : driver.findElements(org.openqa.selenium.By.tagName("iframe"))) {
            try {
                driver.switchTo().defaultContent();
                driver.switchTo().frame(frame);
                if (headingIsVisible(driver)) {
                    return true;
                }
            } catch (RuntimeException ignored) {
                // A dynamic frame may disappear during inspection. Continue.
            }
        }

        driver.switchTo().defaultContent();
        return false;
    }

    static boolean isBranchFinderUrl(String url) {
        return url != null && url.toLowerCase().contains("buscador-sucursales");
    }

    private boolean headingIsVisible(WebDriver driver) {
        return driver.findElements(org.openqa.selenium.By.xpath(BranchFinder.HEADING_XPATH)).stream()
                .anyMatch(WebElement::isDisplayed);
    }

    private void scrollToCenter(WebDriver driver, WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center', inline:'center'});", element
        );
    }

    private String describe(WebElement element) {
        String outerHtml = element.getAttribute("outerHTML");
        if (outerHtml != null && outerHtml.length() > 600) {
            outerHtml = outerHtml.substring(0, 600) + "...";
        }
        return outerHtml;
    }
}
