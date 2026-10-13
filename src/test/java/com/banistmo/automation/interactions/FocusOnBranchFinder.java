package com.banistmo.automation.interactions;

import com.banistmo.automation.userinterfaces.BranchFinder;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Set;

public class FocusOnBranchFinder implements Interaction {

    private final Set<String> windowsBeforeClick;

    public FocusOnBranchFinder(Set<String> windowsBeforeClick) {
        this.windowsBeforeClick = Set.copyOf(windowsBeforeClick);
    }

    public static FocusOnBranchFinder openedAfter(Set<String> windowsBeforeClick) {
        return new FocusOnBranchFinder(windowsBeforeClick);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        WebDriver driver = BrowseTheWeb.as(actor).getDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(12));

        boolean found = wait.until(d -> locateFinder(d));
        if (!found) {
            throw new AssertionError("No se pudo ubicar el buscador de sucursales después de seleccionar la opción solicitada.");
        }
    }

    private boolean locateFinder(WebDriver driver) {
        driver.switchTo().defaultContent();

        if (headingIsVisible(driver)) {
            return true;
        }

        String newWindow = driver.getWindowHandles().stream()
                .filter(handle -> !windowsBeforeClick.contains(handle))
                .findFirst()
                .orElse(null);

        if (newWindow != null) {
            driver.switchTo().window(newWindow);
            if (headingIsVisible(driver)) {
                return true;
            }
        }

        driver.switchTo().defaultContent();
        for (WebElement frame : new ArrayList<>(driver.findElements(By.tagName("iframe")))) {
            try {
                driver.switchTo().defaultContent();
                driver.switchTo().frame(frame);
                if (headingIsVisible(driver)) {
                    return true;
                }
            } catch (NoSuchFrameException ignored) {
                // The frame was re-rendered while evaluating it; continue with the remaining frames.
            }
        }

        driver.switchTo().defaultContent();
        return false;
    }

    private boolean headingIsVisible(WebDriver driver) {
        return driver.findElements(By.xpath(BranchFinder.HEADING_XPATH)).stream()
                .anyMatch(WebElement::isDisplayed);
    }
}
