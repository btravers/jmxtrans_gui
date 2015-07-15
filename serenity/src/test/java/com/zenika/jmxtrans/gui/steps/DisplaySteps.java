package com.zenika.jmxtrans.gui.steps;

import com.zenika.jmxtrans.gui.pages.MainPage;
import net.serenitybdd.core.pages.WebElementFacade;
import net.thucydides.core.annotations.Step;
import org.assertj.core.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class DisplaySteps {

    MainPage mainPage;

    @Step
    public void opensJmxtransguiHomePage() {
        mainPage.open();
    }

    @Step
    public void opensConfDocument(String host) {
        mainPage.openConfDocument(host);
    }

    @Step
    public void shouldDisplayConfDocument(String host, String port) {
        WebElementFacade form = mainPage.getConfDocument();

        WebElementFacade hostElement = form.find(By.cssSelector("input#host"));
        WebElementFacade portElement = form.find(By.cssSelector("input#port"));

        hostElement.waitUntilVisible();
        portElement.waitUntilVisible();

        Assertions.assertThat(hostElement.getValue()).isEqualTo(host);
        Assertions.assertThat(portElement.getValue()).isEqualTo(port);
    }

}