package com.banistmo.automation.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import org.openqa.selenium.WebDriver;

import java.util.List;

public class BranchIsListed implements Question<Boolean> {

    private final String branchName;
    private final String province;

    private BranchIsListed(String branchName, String province) {
        this.branchName = branchName;
        this.province = province;
    }

    public static BranchIsListed named(String branchName, String province) {
        return new BranchIsListed(branchName, province);
    }

    @Override
    public Boolean answeredBy(Actor actor) {
        WebDriver driver = BrowseTheWeb.as(actor).getDriver();
        List<BranchResult> visibleResults = BranchResultDom.visibleResults(driver);
        return BranchResultMetrics.containsBranch(visibleResults, branchName, province);
    }
}
