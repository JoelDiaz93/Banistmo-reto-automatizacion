package com.banistmo.automation.userinterfaces;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

public final class BanistmoHomePage {

    public static final By PYMES_BY = By.xpath(
            "(//*[self::a or self::button][normalize-space()='Pymes' or contains(@href,'/pymes')])[1]"
    );
    public static final Target PYMES = Target.the("sección Pymes").located(PYMES_BY);

    /**
     * Promotional campaign currently injected on the Personas home page.
     * Matching by business copy is intentionally more stable than generated
     * CSS class names from the front-end framework.
     */
    public static final By PROMOTIONAL_MODAL_HEADING_BY = By.xpath(
            "(//*[self::h1 or self::h2 or self::h3 or @role='heading']" +
                    "[contains(normalize-space(.),'compras acumulan MultiPuntos')])[1]"
    );
    public static final Target PROMOTIONAL_MODAL_HEADING = Target.the("modal promocional de MultiPuntos")
            .located(PROMOTIONAL_MODAL_HEADING_BY);

    /**
     * Close control scoped to the ancestor that contains the known campaign.
     */
    public static final By PROMOTIONAL_MODAL_CLOSE_BY = By.xpath(
            "(//*[contains(normalize-space(.),'compras acumulan MultiPuntos')]" +
                    "/ancestor::*[self::div or self::section][.//*[self::button or @role='button']][1]" +
                    "//*[self::button or @role='button'][" +
                    "contains(translate(@aria-label,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'cerr') or " +
                    "contains(translate(@aria-label,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'close') or " +
                    "contains(translate(@title,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'cerr') or " +
                    "contains(translate(@title,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'close') or " +
                    "contains(translate(@class,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'close') or " +
                    "contains(translate(@data-testid,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'close') or " +
                    "normalize-space()='×' or normalize-space()='✕' or normalize-space()='X'" +
                    "])[1]"
    );
    public static final Target PROMOTIONAL_MODAL_CLOSE = Target.the("botón para cerrar el modal promocional")
            .located(PROMOTIONAL_MODAL_CLOSE_BY);

    public static final By PRIVACY_ACKNOWLEDGE_BY = By.xpath(
            "(//*[self::button or self::a][normalize-space()='Entendido' or normalize-space()='Aceptar'])[1]"
    );
    public static final Target PRIVACY_ACKNOWLEDGE = Target.the("botón Entendido del aviso de privacidad")
            .located(PRIVACY_ACKNOWLEDGE_BY);

    private BanistmoHomePage() {
    }
}
