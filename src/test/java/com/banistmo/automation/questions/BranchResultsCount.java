package com.banistmo.automation.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import org.openqa.selenium.WebDriver;

import java.util.List;

public class BranchResultsCount implements Question<Integer> {

    private final String province;

    private BranchResultsCount(String province) {
        this.province = province;
    }

    public static BranchResultsCount forProvince(String province) {
        return new BranchResultsCount(province);
    }

    @Override
    public Integer answeredBy(Actor actor) {
        WebDriver driver = BrowseTheWeb.as(actor).getDriver();
        List<BranchResult> visibleResults = BranchResultDom.visibleResults(driver);
        return BranchResultMetrics.countForProvince(visibleResults, province);
    }
}
