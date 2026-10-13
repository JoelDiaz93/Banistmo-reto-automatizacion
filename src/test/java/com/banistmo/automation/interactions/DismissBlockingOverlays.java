package com.banistmo.automation.interactions;

import com.banistmo.automation.userinterfaces.BanistmoHomePage;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Closes only known, non-functional overlays that can interrupt the challenge.
 * Optional overlays are detected with immediate WebDriver lookups so their
 * absence never consumes Serenity's global fluent wait.
 */
public class DismissBlockingOverlays implements Interaction {

    private static final Duration LATE_PROMO_WAIT = Duration.ofMillis(1800);
    private static final Duration DISMISS_WAIT = Duration.ofMillis(1500);

    private final boolean waitForLatePromotionalModal;

    private DismissBlockingOverlays(boolean waitForLatePromotionalModal) {
        this.waitForLatePromotionalModal = waitForLatePromotionalModal;
    }

    public static DismissBlockingOverlays afterPageLoad() {
        return new DismissBlockingOverlays(true);
    }

    public static DismissBlockingOverlays ifPresent() {
        return new DismissBlockingOverlays(false);
    }

    @Override
    @Step("{0} descarta avisos no funcionales si bloquean la navegación")
    public <T extends Actor> void performAs(T actor) {
        WebDriver driver = BrowseTheWeb.as(actor).getDriver();

        // The privacy banner is commonly present immediately and can intercept
        // the first navigation click, so it has priority.
        dismissPrivacyNotice(driver);

        if (waitForLatePromotionalModal) {
            waitBrieflyForPromotionalModal(driver);
        }

        dismissPromotionalModal(driver);

        // The privacy banner may be injected after the campaign/modal cycle.
        dismissPrivacyNotice(driver);
    }

    private void waitBrieflyForPromotionalModal(WebDriver driver) {
        try {
            new WebDriverWait(driver, LATE_PROMO_WAIT).until(ignored ->
                    visibleNow(driver, BanistmoHomePage.PROMOTIONAL_MODAL_HEADING_BY) != null
            );
        } catch (TimeoutException ignored) {
            // Optional campaign: absence is a normal state.
        }
    }

    private void dismissPromotionalModal(WebDriver driver) {
        WebElement modalHeading = visibleNow(driver, BanistmoHomePage.PROMOTIONAL_MODAL_HEADING_BY);
        if (modalHeading == null) {
            return;
        }

        WebElement close = visibleNow(driver, BanistmoHomePage.PROMOTIONAL_MODAL_CLOSE_BY);
        if (close != null) {
            close.click();
        } else {
            new Actions(driver).sendKeys(Keys.ESCAPE).perform();
        }

        waitUntilGone(driver, BanistmoHomePage.PROMOTIONAL_MODAL_HEADING_BY, "modal promocional de MultiPuntos");
    }

    private void dismissPrivacyNotice(WebDriver driver) {
        WebElement acknowledge = visibleNow(driver, BanistmoHomePage.PRIVACY_ACKNOWLEDGE_BY);
        if (acknowledge == null) {
            return;
        }

        acknowledge.click();
        waitUntilGone(driver, BanistmoHomePage.PRIVACY_ACKNOWLEDGE_BY, "aviso de privacidad");
    }

    private void waitUntilGone(WebDriver driver, By locator, String overlayName) {
        try {
            new WebDriverWait(driver, DISMISS_WAIT).until(ignored -> visibleNow(driver, locator) == null);
        } catch (TimeoutException exception) {
            throw new IllegalStateException(
                    "El " + overlayName + " continúa visible y bloquea la navegación; se detiene para evitar un falso positivo.",
                    exception
            );
        }
    }

    private WebElement visibleNow(WebDriver driver, By locator) {
        List<WebElement> matches = driver.findElements(locator);
        for (WebElement element : matches) {
            try {
                if (element.isDisplayed() && element.isEnabled()) {
                    return element;
                }
            } catch (RuntimeException ignored) {
                // Dynamic element disappeared between lookup and inspection.
            }
        }
        return null;
    }
}
