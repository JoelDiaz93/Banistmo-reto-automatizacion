package com.banistmo.automation.tasks;

import com.banistmo.automation.config.ChallengeConfig;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.actions.Open;

public class OpenBanistmo implements Task {

    public static OpenBanistmo personas() {
        return Tasks.instrumented(OpenBanistmo.class);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(Open.url(ChallengeConfig.personasUrl()));
    }
}
