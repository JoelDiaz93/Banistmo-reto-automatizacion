package com.banistmo.automation.tasks;

import com.banistmo.automation.interactions.DismissPrivacyNotice;
import com.banistmo.automation.userinterfaces.PymesPage;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.waits.WaitUntil;

import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isClickable;

public class OpenProductsAndServices implements Task {

    public static OpenProductsAndServices menu() {
        return Tasks.instrumented(OpenProductsAndServices.class);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                DismissPrivacyNotice.ifPresent(),
                WaitUntil.the(PymesPage.PRODUCTOS_Y_SERVICIOS, isClickable()).forNoMoreThan(10).seconds(),
                Click.on(PymesPage.PRODUCTOS_Y_SERVICIOS)
        );
    }
}
