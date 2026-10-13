package com.banistmo.automation.interactions;

import com.banistmo.automation.userinterfaces.BranchFinder;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.Tasks;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.text.Normalizer;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Selects a branch-finder filter without coupling the test to one specific
 * front-end implementation. Banistmo currently renders these controls as
 * dynamic inputs/comboboxes, while older versions used native selects.
 *
 * The interaction supports both representations and always scrolls the actual
 * control into the viewport before interacting with it.
 */
public class SelectBranchFilterOption implements Interaction {

    private static final Duration CONTROL_TIMEOUT = Duration.ofSeconds(15);
    private static final Duration OPTION_TIMEOUT = Duration.ofSeconds(10);

    private final Filter filter;
    private final String option;

    public SelectBranchFilterOption(Filter filter, String option) {
        this.filter = filter;
        this.option = option;
    }

    public static SelectBranchFilterOption pointType(String option) {
        return Tasks.instrumented(SelectBranchFilterOption.class, Filter.POINT_TYPE, option);
    }

    public static SelectBranchFilterOption province(String option) {
        return Tasks.instrumented(SelectBranchFilterOption.class, Filter.PROVINCE, option);
    }

    @Override
    @Step("{0} selecciona '#option' en el filtro #filter")
    public <T extends Actor> void performAs(T actor) {
        WebDriver driver = BrowseTheWeb.as(actor).getDriver();

        WebElement control = new WebDriverWait(driver, CONTROL_TIMEOUT)
                .until(currentDriver -> findControl(currentDriver, filter));

        scrollToCenter(driver, control);

        if ("select".equalsIgnoreCase(control.getTagName())) {
            selectNativeOption(control, option);
        } else {
            selectCustomOption(driver, control, option);
        }

        // Fail immediately if the UI did not retain the selected value.
        if (!selectionIsObservable(control, option)) {
            // Some custom components replace the original input after selection.
            WebElement refreshed = findControl(driver, filter);
            if (refreshed == null || !selectionIsObservable(refreshed, option)) {
                throw new AssertionError(
                        "El filtro '" + filter.displayName + "' no conservó la opción '" + option + "'. " +
                        "Control detectado: " + describe(control)
                );
            }
        }
    }

    private WebElement findControl(WebDriver driver, Filter requestedFilter) {
        driver.switchTo().defaultContent();

        WebElement nativeSelect = findNativeSelect(driver, requestedFilter);
        if (nativeSelect != null) {
            return nativeSelect;
        }

        WebElement semanticControl = firstDisplayed(driver.findElements(By.xpath(requestedFilter.semanticControlXpath)));
        if (semanticControl != null) {
            return semanticControl;
        }

        // Controlled fallback: inspect only the interactive controls that appear
        // after the branch-finder heading. This avoids unrelated header/search
        // inputs while remaining independent from the page's generated CSS.
        WebElement heading = firstDisplayed(driver.findElements(By.xpath(BranchFinder.HEADING_XPATH)));
        if (heading == null) {
            return null;
        }

        List<WebElement> candidates = heading.findElements(By.xpath(
                "following::*[self::input or self::select or @role='combobox' or @aria-haspopup='listbox'][not(ancestor::header)]"
        ));
        List<WebElement> displayed = candidates.stream().filter(this::isDisplayed).toList();

        if (requestedFilter == Filter.PROVINCE) {
            return displayed.stream()
                    .filter(element -> searchableAttributes(element).contains("provincia"))
                    .findFirst()
                    .orElse(displayed.size() >= 2 ? displayed.get(1) : null);
        }

        return displayed.stream()
                .filter(element -> !searchableAttributes(element).contains("provincia"))
                .findFirst()
                .orElse(displayed.isEmpty() ? null : displayed.get(0));
    }

    private WebElement findNativeSelect(WebDriver driver, Filter requestedFilter) {
        String expected = normalize(requestedFilter == Filter.POINT_TYPE ? "sucursal" : option);

        for (WebElement select : driver.findElements(By.tagName("select"))) {
            if (!isDisplayed(select)) {
                continue;
            }
            try {
                boolean matchingOption = new Select(select).getOptions().stream()
                        .map(WebElement::getText)
                        .map(this::normalize)
                        .anyMatch(text -> text.contains(expected));
                if (matchingOption) {
                    return select;
                }
            } catch (RuntimeException ignored) {
                // Continue with the custom-control strategy.
            }
        }
        return null;
    }

    private void selectNativeOption(WebElement control, String visibleText) {
        Select select = new Select(control);
        String expected = normalize(visibleText);

        WebElement matching = select.getOptions().stream()
                .filter(optionElement -> normalize(optionElement.getText()).equals(expected)
                        || normalize(optionElement.getText()).contains(expected))
                .findFirst()
                .orElseThrow(() -> new AssertionError(
                        "No se encontró la opción '" + visibleText + "' en el select " + describe(control)
                ));

        select.selectByVisibleText(matching.getText());
    }

    private void selectCustomOption(WebDriver driver, WebElement control, String visibleText) {
        control.click();

        if ("input".equalsIgnoreCase(control.getTagName())) {
            String readonly = control.getAttribute("readonly");
            if (readonly == null) {
                control.sendKeys(Keys.chord(Keys.CONTROL, "a"));
                control.sendKeys(visibleText);
            }
        }

        WebElement optionElement = new WebDriverWait(driver, OPTION_TIMEOUT)
                .until(currentDriver -> firstDisplayed(optionCandidates(currentDriver, visibleText)));

        scrollToCenter(driver, optionElement);
        optionElement.click();
    }

    private List<WebElement> optionCandidates(WebDriver driver, String visibleText) {
        String literal = xpathLiteral(visibleText);
        Set<WebElement> candidates = new LinkedHashSet<>();

        candidates.addAll(driver.findElements(By.xpath(
                "//*[@role='option'][normalize-space(.)=" + literal + "]"
        )));
        candidates.addAll(driver.findElements(By.xpath(
                "//*[@role='listbox']//*[normalize-space(.)=" + literal + "]"
        )));
        candidates.addAll(driver.findElements(By.xpath(
                "//*[self::li or self::button][normalize-space(.)=" + literal + "]"
        )));

        // Last, controlled fallback for component libraries that render options
        // as div/span nodes inside a visible popup portal.
        candidates.addAll(driver.findElements(By.xpath(
                "//*[self::div or self::span][normalize-space(.)=" + literal + "]"
        )));

        return new ArrayList<>(candidates);
    }

    private boolean selectionIsObservable(WebElement control, String expectedText) {
        String expected = normalize(expectedText);
        try {
            if ("select".equalsIgnoreCase(control.getTagName())) {
                return new Select(control).getAllSelectedOptions().stream()
                        .map(WebElement::getText)
                        .map(this::normalize)
                        .anyMatch(text -> text.contains(expected));
            }

            return normalize(control.getAttribute("value")).contains(expected)
                    || normalize(control.getAttribute("aria-label")).contains(expected)
                    || normalize(control.getAttribute("title")).contains(expected)
                    || normalize(control.getText()).contains(expected);
        } catch (RuntimeException ignored) {
            return false;
        }
    }

    private void scrollToCenter(WebDriver driver, WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({behavior:'auto', block:'center', inline:'nearest'});" +
                "window.scrollBy(0, -80);",
                element
        );
    }

    private WebElement firstDisplayed(List<WebElement> elements) {
        return elements.stream().filter(this::isDisplayed).findFirst().orElse(null);
    }

    private boolean isDisplayed(WebElement element) {
        try {
            return element != null && element.isDisplayed() && element.isEnabled();
        } catch (RuntimeException ignored) {
            return false;
        }
    }

    private String searchableAttributes(WebElement element) {
        return normalize(String.join(" ",
                safe(element.getAttribute("aria-label")),
                safe(element.getAttribute("placeholder")),
                safe(element.getAttribute("name")),
                safe(element.getAttribute("id")),
                safe(element.getAttribute("title")),
                safe(element.getText())
        ));
    }

    private String describe(WebElement element) {
        try {
            return String.valueOf(((JavascriptExecutor) ((org.openqa.selenium.WrapsDriver) element).getWrappedDriver())
                    .executeScript("return arguments[0].outerHTML;", element));
        } catch (RuntimeException ignored) {
            return "<sin diagnóstico DOM>";
        }
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        return Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .trim();
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String xpathLiteral(String value) {
        if (!value.contains("'")) {
            return "'" + value + "'";
        }
        if (!value.contains("\"")) {
            return "\"" + value + "\"";
        }
        String[] parts = value.split("'");
        return "concat('" + String.join("',\"'\",'", parts) + "')";
    }

    public enum Filter {
        POINT_TYPE(
                "tipo de punto",
                "(//*[@role='combobox' or self::input or self::button or @aria-haspopup='listbox']" +
                        "[contains(translate(concat(@aria-label,' ',@placeholder,' ',@name,' ',@id,' ',@title)," +
                        "'ABCDEFGHIJKLMNOPQRSTUVWXYZÁÉÍÓÚÜÑ','abcdefghijklmnopqrstuvwxyzáéíóúüñ'),'sucursal') or " +
                        "contains(translate(concat(@aria-label,' ',@placeholder,' ',@name,' ',@id,' ',@title)," +
                        "'ABCDEFGHIJKLMNOPQRSTUVWXYZÁÉÍÓÚÜÑ','abcdefghijklmnopqrstuvwxyzáéíóúüñ'),'punto')])[1]"
        ),
        PROVINCE(
                "provincia",
                "(//*[@role='combobox' or self::input or self::button or @aria-haspopup='listbox']" +
                        "[contains(translate(concat(@aria-label,' ',@placeholder,' ',@name,' ',@id,' ',@title)," +
                        "'ABCDEFGHIJKLMNOPQRSTUVWXYZÁÉÍÓÚÜÑ','abcdefghijklmnopqrstuvwxyzáéíóúüñ'),'provincia')])[1]"
        );

        private final String displayName;
        private final String semanticControlXpath;

        Filter(String displayName, String semanticControlXpath) {
            this.displayName = displayName;
            this.semanticControlXpath = semanticControlXpath;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }
}
