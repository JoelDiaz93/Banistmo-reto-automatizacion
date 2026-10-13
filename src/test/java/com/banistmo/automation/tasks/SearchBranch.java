package com.banistmo.automation.tasks;

import com.banistmo.automation.interactions.DismissBlockingOverlays;
import com.banistmo.automation.interactions.SelectBranchFilterOption;
import com.banistmo.automation.userinterfaces.BranchFinder;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.actions.Scroll;
import net.serenitybdd.screenplay.waits.WaitUntil;

import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;

public class SearchBranch implements Task {

    private final String pointType;
    private final String province;

    public SearchBranch(String pointType, String province) {
        this.pointType = pointType;
        this.province = province;
    }

    public static SearchBranch using(String pointType, String province) {
        return Tasks.instrumented(SearchBranch.class, pointType, province);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                DismissBlockingOverlays.ifPresent(),
                WaitUntil.the(BranchFinder.HEADING, isVisible()).forNoMoreThan(15).seconds(),
                Scroll.to(BranchFinder.HEADING),
                SelectBranchFilterOption.pointType(pointType),
                SelectBranchFilterOption.province(province)
        );
    }
}
