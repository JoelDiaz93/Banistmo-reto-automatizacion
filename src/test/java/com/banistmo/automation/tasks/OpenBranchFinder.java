package com.banistmo.automation.tasks;

import com.banistmo.automation.interactions.ActivateExecutivePymeCta;
import com.banistmo.automation.interactions.DismissBlockingOverlays;
import com.banistmo.automation.userinterfaces.PymesPage;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.actions.Scroll;
import net.serenitybdd.screenplay.waits.WaitUntil;

import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isClickable;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;

public class OpenBranchFinder implements Task {

    public static OpenBranchFinder fromExecutiveButton() {
        return Tasks.instrumented(OpenBranchFinder.class);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                DismissBlockingOverlays.ifPresent(),
                WaitUntil.the(PymesPage.SOLICITALO_CON_EJECUTIVO, isVisible()).forNoMoreThan(8).seconds(),
                Scroll.to(PymesPage.SOLICITALO_CON_EJECUTIVO),
                WaitUntil.the(PymesPage.SOLICITALO_CON_EJECUTIVO, isClickable()).forNoMoreThan(8).seconds(),
                ActivateExecutivePymeCta.andWaitForBranchFinder()
        );
    }
}
