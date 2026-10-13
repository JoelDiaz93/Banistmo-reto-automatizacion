package com.banistmo.automation.interactions;

import com.banistmo.automation.userinterfaces.BanistmoHomePage;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Closes only known, non-functional overlays that can interrupt the navigation
 * required by the challenge. Functional dialogs (for example the branch
 * finder opened by the challenge) are deliberately not handled here.
 */
public class DismissBlockingOverlays implements Interaction {

    private static final Duration LATE_PROMO_WAIT = Duration.ofSeconds(4);
    private static final Duration DISMISS_WAIT = Duration.ofSeconds(3);

    private final boolean waitForLatePromotionalModal;

    private DismissBlockingOverlays(boolean waitForLatePromotionalModal) {
        this.waitForLatePromotionalModal = waitForLatePromotionalModal;
    }

    /**
     * Use immediately after loading the Personas home page. The promotional
     * campaign modal is injected asynchronously, so we give it a short,
     * bounded opportunity to appear before continuing.
     */
    public static DismissBlockingOverlays afterPageLoad() {
        return new DismissBlockingOverlays(true);
    }

    /**
     * Use before subsequent navigation actions. It does not add an artificial
     * wait when no known overlay is currently visible.
     */
    public static DismissBlockingOverlays ifPresent() {
        return new DismissBlockingOverlays(false);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        WebDriver driver = BrowseTheWeb.as(actor).getDriver();

        if (waitForLatePromotionalModal) {
            waitForPromotionalModalToAppear(actor, driver);
        }

        dismissPromotionalModal(actor, driver);
        dismissPrivacyNotice(actor, driver);
    }

    private <T extends Actor> void waitForPromotionalModalToAppear(T actor, WebDriver driver) {
        try {
            new WebDriverWait(driver, LATE_PROMO_WAIT).until(ignored ->
                    BanistmoHomePage.PROMOTIONAL_MODAL_HEADING.resolveFor(actor).isVisible()
            );
        } catch (TimeoutException ignored) {
            // The campaign is optional and may be disabled by Banistmo at any time.
        }
    }

    private <T extends Actor> void dismissPromotionalModal(T actor, WebDriver driver) {
        if (!BanistmoHomePage.PROMOTIONAL_MODAL_HEADING.resolveFor(actor).isVisible()) {
            return;
        }

        if (BanistmoHomePage.PROMOTIONAL_MODAL_CLOSE.resolveFor(actor).isVisible()) {
            actor.attemptsTo(Click.on(BanistmoHomePage.PROMOTIONAL_MODAL_CLOSE));
        } else {
            // Accessible modal implementations normally support Escape. This is
            // a fallback only for the known MultiPuntos campaign identified by
            // its heading; we never press Escape for an unknown dialog.
            new Actions(driver).sendKeys(Keys.ESCAPE).perform();
        }

        waitUntilHidden(actor, driver, true);
    }

    private <T extends Actor> void dismissPrivacyNotice(T actor, WebDriver driver) {
        if (!BanistmoHomePage.PRIVACY_ACKNOWLEDGE.resolveFor(actor).isVisible()) {
            return;
        }

        actor.attemptsTo(Click.on(BanistmoHomePage.PRIVACY_ACKNOWLEDGE));
        waitUntilHidden(actor, driver, false);
    }

    private <T extends Actor> void waitUntilHidden(T actor, WebDriver driver, boolean promotionalModal) {
        try {
            new WebDriverWait(driver, DISMISS_WAIT).until(ignored -> promotionalModal
                    ? !BanistmoHomePage.PROMOTIONAL_MODAL_HEADING.resolveFor(actor).isVisible()
                    : !BanistmoHomePage.PRIVACY_ACKNOWLEDGE.resolveFor(actor).isVisible()
            );
        } catch (TimeoutException exception) {
            String overlay = promotionalModal ? "modal promocional de MultiPuntos" : "aviso de privacidad";
            throw new IllegalStateException(
                    "El " + overlay + " continúa visible y bloquea la navegación; no se continúa para evitar un falso positivo.",
                    exception
            );
        }
    }
}
