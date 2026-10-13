package com.banistmo.automation.tasks;

import com.banistmo.automation.interactions.FocusOnBranchFinder;
import com.banistmo.automation.userinterfaces.PymesPage;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Scroll;
import net.serenitybdd.screenplay.waits.WaitUntil;

import java.util.HashSet;
import java.util.Set;

import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isClickable;

public class OpenBranchFinder implements Task {

    public static OpenBranchFinder fromExecutiveButton() {
        return Tasks.instrumented(OpenBranchFinder.class);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        Set<String> windowsBefore = new HashSet<>(BrowseTheWeb.as(actor).getDriver().getWindowHandles());

        actor.attemptsTo(
                Scroll.to(PymesPage.SOLICITALO_CON_EJECUTIVO),
                WaitUntil.the(PymesPage.SOLICITALO_CON_EJECUTIVO, isClickable()).forNoMoreThan(10).seconds(),
                Click.on(PymesPage.SOLICITALO_CON_EJECUTIVO),
                FocusOnBranchFinder.openedAfter(windowsBefore)
        );
    }
}
