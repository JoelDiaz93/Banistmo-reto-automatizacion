package com.banistmo.automation.tasks;

import com.banistmo.automation.interactions.DismissBlockingOverlays;
import com.banistmo.automation.userinterfaces.PymesPage;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Scroll;
import net.serenitybdd.screenplay.waits.WaitUntil;

import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isClickable;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;

public class OpenAssistancePyme implements Task {

    public static OpenAssistancePyme plan() {
        return Tasks.instrumented(OpenAssistancePyme.class);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                DismissBlockingOverlays.ifPresent(),
                WaitUntil.the(PymesPage.ASISTENCIA_PYME, isClickable()).forNoMoreThan(10).seconds(),
                Scroll.to(PymesPage.ASISTENCIA_PYME),
                Click.on(PymesPage.ASISTENCIA_PYME),
                WaitUntil.the(PymesPage.ASISTENCIA_HEADING, isVisible()).forNoMoreThan(12).seconds()
        );
    }
}
