package com.banistmo.automation.interactions;

import com.banistmo.automation.userinterfaces.BanistmoHomePage;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.waits.WaitUntil;

import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isClickable;

public class DismissPrivacyNotice implements Interaction {

    public static DismissPrivacyNotice ifPresent() {
        return new DismissPrivacyNotice();
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        if (BanistmoHomePage.PRIVACY_ACKNOWLEDGE.resolveFor(actor).isVisible()) {
            actor.attemptsTo(
                    WaitUntil.the(BanistmoHomePage.PRIVACY_ACKNOWLEDGE, isClickable())
                            .forNoMoreThan(5).seconds(),
                    Click.on(BanistmoHomePage.PRIVACY_ACKNOWLEDGE)
            );
        }
    }
}
