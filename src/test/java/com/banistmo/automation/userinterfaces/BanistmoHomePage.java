package com.banistmo.automation.userinterfaces;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

public final class BanistmoHomePage {

    public static final Target PYMES = Target.the("sección Pymes")
            .located(By.xpath("(//*[self::a or self::button][normalize-space()='Pymes' or contains(@href,'/pymes')])[1]"));

    public static final Target PRIVACY_ACKNOWLEDGE = Target.the("botón Entendido del aviso de privacidad")
            .located(By.xpath("(//*[self::button or self::a][normalize-space()='Entendido' or normalize-space()='Aceptar'])[1]"));

    private BanistmoHomePage() {
    }
}
