package com.banistmo.automation.tasks;

import com.banistmo.automation.userinterfaces.BranchFinder;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.actions.SelectFromOptions;
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
                WaitUntil.the(BranchFinder.POINT_TYPE, isVisible()).forNoMoreThan(10).seconds(),
                SelectFromOptions.byVisibleText(pointType).from(BranchFinder.POINT_TYPE),
                WaitUntil.the(BranchFinder.PROVINCE, isVisible()).forNoMoreThan(10).seconds(),
                SelectFromOptions.byVisibleText(province).from(BranchFinder.PROVINCE)
        );
    }
}
