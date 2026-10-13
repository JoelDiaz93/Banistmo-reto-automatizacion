package com.banistmo.automation.tasks;

import com.banistmo.automation.interactions.DismissBlockingOverlays;
import com.banistmo.automation.userinterfaces.BanistmoHomePage;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.waits.WaitUntil;

import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isClickable;

public class GoToPymes implements Task {

    public static GoToPymes section() {
        return Tasks.instrumented(GoToPymes.class);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                DismissBlockingOverlays.ifPresent(),
                WaitUntil.the(BanistmoHomePage.PYMES, isClickable()).forNoMoreThan(8).seconds(),
                Click.on(BanistmoHomePage.PYMES)
        );
    }
}
