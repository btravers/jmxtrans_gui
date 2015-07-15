package com.zenika.jmxtrans.gui.features.display;

import com.zenika.jmxtrans.gui.steps.DisplaySteps;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.Steps;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

@RunWith(SerenityRunner.class)
public class WhenClickingServerTest {

    @Managed(driver = "firefox")
    WebDriver driver;

    @Steps
    DisplaySteps display;

    @Test
    public void shouldOpenDocument() {
        String host = "192.168.33.10";
        String port = "9991";
        display.opensJmxtransguiHomePage();
        display.opensConfDocument(host + ":" + port);
        display.shouldDisplayConfDocument(host, port);
    }

}
