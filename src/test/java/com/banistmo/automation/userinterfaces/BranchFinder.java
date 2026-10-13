package com.banistmo.automation.userinterfaces;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

public final class BranchFinder {

    public static final String HEADING_XPATH =
            "(//*[self::h1 or self::h2 or self::h3][contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZÁÉÍÓÚÜÑ','abcdefghijklmnopqrstuvwxyzáéíóúüñ'),'buscador de sucursales')])[1]";

    public static final Target HEADING = Target.the("título del Buscador de Sucursales, Cajeros ATM y Comercios Autorizados")
            .located(By.xpath(HEADING_XPATH));

    public static final Target POINT_TYPE = Target.the("selector de tipo de punto")
            .located(By.xpath(
                    "(//select[.//option[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZÁÉÍÓÚÜÑ','abcdefghijklmnopqrstuvwxyzáéíóúüñ'),'sucursal')]])[1]"
            ));

    public static final Target PROVINCE = Target.the("selector de provincia")
            .located(By.xpath(
                    "(//select[.//option[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZÁÉÍÓÚÜÑ','abcdefghijklmnopqrstuvwxyzáéíóúüñ'),'colón') " +
                    "or contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZÁÉÍÓÚÜÑ','abcdefghijklmnopqrstuvwxyzáéíóúüñ'),'colon')]])[1]"
            ));

    private BranchFinder() {
    }
}
