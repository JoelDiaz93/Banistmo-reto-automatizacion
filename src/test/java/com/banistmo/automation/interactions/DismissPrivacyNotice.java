package com.banistmo.automation.interactions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;

/**
 * @deprecated Use {@link DismissBlockingOverlays}. Retained temporarily so
 * existing branches do not break while the challenge is being iterated.
 */
@Deprecated
public class DismissPrivacyNotice implements Interaction {

    public static DismissPrivacyNotice ifPresent() {
        return new DismissPrivacyNotice();
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(DismissBlockingOverlays.ifPresent());
    }
}
