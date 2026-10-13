package com.banistmo.automation.stepdefinitions;

import com.banistmo.automation.questions.BranchResultsCount;
import com.banistmo.automation.questions.CurrentUrl;
import com.banistmo.automation.questions.IsVisible;
import com.banistmo.automation.tasks.GoToPymes;
import com.banistmo.automation.tasks.OpenAssistancePyme;
import com.banistmo.automation.tasks.OpenBanistmo;
import com.banistmo.automation.tasks.OpenBranchFinder;
import com.banistmo.automation.tasks.OpenProductsAndServices;
import com.banistmo.automation.tasks.SearchBranch;
import com.banistmo.automation.userinterfaces.BranchFinder;
import com.banistmo.automation.userinterfaces.PymesPage;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import net.serenitybdd.annotations.Managed;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;
import net.serenitybdd.screenplay.actions.Scroll;
import net.serenitybdd.screenplay.waits.WaitUntil;
import org.openqa.selenium.WebDriver;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

public class AssistancePymeStepDefinitions {

    @Managed
    WebDriver browser;

    @Before
    public void setUpActor() {
        OnStage.setTheStage(new OnlineCast());
        OnStage.theActorCalled("QA Automation Senior")
                .whoCan(BrowseTheWeb.with(browser));
    }

    @After
    public void tearDownActor() {
        OnStage.drawTheCurtain();
    }

    @Dado("que el usuario ingresa al portal de Personas de Banistmo")
    public void openPersonasPortal() {
        OnStage.theActorInTheSpotlight().attemptsTo(OpenBanistmo.personas());
        OnStage.theActorInTheSpotlight().should(
                seeThat("la URL pertenece a Banistmo",
                        CurrentUrl.value(), containsString("banistmo.com"))
        );
    }

    @Cuando("navega a la sección Pymes")
    public void goToPymes() {
        OnStage.theActorInTheSpotlight().attemptsTo(GoToPymes.section());
        OnStage.theActorInTheSpotlight().should(
                seeThat("se encuentra dentro de la experiencia Pymes",
                        CurrentUrl.value(), containsString("/pymes"))
        );
    }

    @Cuando("selecciona Productos y servicios")
    public void openProductsAndServices() {
        OnStage.theActorInTheSpotlight().attemptsTo(OpenProductsAndServices.menu());
        OnStage.theActorInTheSpotlight().should(
                seeThat("la opción Asistencia pyme está disponible",
                        IsVisible.the(PymesPage.ASISTENCIA_PYME), is(true))
        );
    }

    @Cuando("ingresa a Asistencia pyme")
    public void openAssistancePyme() {
        OnStage.theActorInTheSpotlight().attemptsTo(OpenAssistancePyme.plan());
        OnStage.theActorInTheSpotlight().should(
                seeThat("el Plan de Asistencia Pyme está visible",
                        IsVisible.the(PymesPage.ASISTENCIA_HEADING), is(true))
        );
    }

    @Cuando("selecciona Solicítalo con tu Ejecutivo Pyme")
    public void openBranchFinder() {
        OnStage.theActorInTheSpotlight().attemptsTo(OpenBranchFinder.fromExecutiveButton());
    }

    @Entonces("debe visualizar el buscador emergente de sucursales correcto")
    public void validateBranchFinder() {
        OnStage.theActorInTheSpotlight().attemptsTo(
                WaitUntil.the(BranchFinder.HEADING, isVisible()).forNoMoreThan(15).seconds()
        );

        // At this point we validate page identity, not viewport position. The
        // filter controls are lower in the page and SearchBranch is responsible
        // for scrolling to them and selecting their values.
        OnStage.theActorInTheSpotlight().should(
                seeThat("el título del buscador corresponde al esperado",
                        IsVisible.the(BranchFinder.HEADING), is(true)),
                seeThat("la URL corresponde al buscador de sucursales",
                        CurrentUrl.value(), containsString("buscador-sucursales"))
        );
    }

    @Cuando("busca el tipo de punto {string} en la provincia {string}")
    public void searchBranch(String pointType, String province) {
        OnStage.theActorInTheSpotlight().attemptsTo(
                SearchBranch.using(pointType, province)
        );
    }

    @Entonces("debe visualizar {int} sucursales en la provincia {string}")
    public void validateBranchCount(int expectedCount, String province) {
        OnStage.theActorInTheSpotlight().attemptsTo(
                WaitUntil.the(BranchFinder.branchAddressForProvince(province), isVisible())
                        .forNoMoreThan(15).seconds(),
                Scroll.to(BranchFinder.branchAddressForProvince(province))
        );

        OnStage.theActorInTheSpotlight().should(
                seeThat("la cantidad de sucursales visibles en " + province,
                        BranchResultsCount.forProvince(province), is(expectedCount))
        );
    }

    @Entonces("debe visualizar la sucursal {string} asociada a {string}")
    public void validateBranch(String branchName, String province) {
        OnStage.theActorInTheSpotlight().should(
                seeThat("la sucursal y su provincia aparecen en la misma tarjeta de resultado",
                        IsVisible.the(BranchFinder.branchCard(branchName, province)), is(true))
        );
    }
}
