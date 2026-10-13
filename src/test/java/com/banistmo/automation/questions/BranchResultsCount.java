package com.banistmo.automation.questions;

import com.banistmo.automation.userinterfaces.BranchFinder;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

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
        List<String> visibleAddressTexts = BranchFinder.RESULT_ADDRESS_LINES
                .resolveAllFor(actor)
                .stream()
                .filter(element -> element.isVisible())
                .map(element -> element.getText().trim())
                .filter(text -> !text.isBlank())
                .toList();

        return BranchResultMetrics.countForProvince(visibleAddressTexts, province);
    }
}
