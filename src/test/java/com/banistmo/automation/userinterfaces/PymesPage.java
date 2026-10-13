package com.banistmo.automation.userinterfaces;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

public final class PymesPage {

    public static final Target PRODUCTOS_Y_SERVICIOS = Target.the("opción Productos y servicios")
            .located(By.xpath(
                    "(//*[self::a or self::button][contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZÁÉÍÓÚÜÑ','abcdefghijklmnopqrstuvwxyzáéíóúüñ'),'productos') " +
                    "and contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZÁÉÍÓÚÜÑ','abcdefghijklmnopqrstuvwxyzáéíóúüñ'),'servicios')])[1]"
            ));

    public static final Target ASISTENCIA_PYME = Target.the("opción Asistencia pyme")
            .located(By.xpath(
                    "(//*[self::a or self::button][contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZÁÉÍÓÚÜÑ','abcdefghijklmnopqrstuvwxyzáéíóúüñ'),'asistencia pyme') " +
                    "or contains(@href,'plan-asistencia')])[1]"
            ));

    public static final Target ASISTENCIA_HEADING = Target.the("título del Plan de Asistencia Pyme")
            .located(By.xpath(
                    "(//*[self::h1 or self::h2][contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZÁÉÍÓÚÜÑ','abcdefghijklmnopqrstuvwxyzáéíóúüñ'),'asistencia') " +
                    "and contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZÁÉÍÓÚÜÑ','abcdefghijklmnopqrstuvwxyzáéíóúüñ'),'pyme')])[1]"
            ));

    public static final Target SOLICITALO_CON_EJECUTIVO = Target.the("botón Solicítalo con tu Ejecutivo Pyme")
            .located(By.xpath(
                    "(//*[self::a or self::button][contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZÁÉÍÓÚÜÑ','abcdefghijklmnopqrstuvwxyzáéíóúüñ'),'solicítalo con tu ejecutivo pyme') " +
                    "or contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZÁÉÍÓÚÜÑ','abcdefghijklmnopqrstuvwxyzáéíóúüñ'),'solicitalo con tu ejecutivo pyme')])[1]"
            ));

    private PymesPage() {
    }
}
