package com.zenika.jmxtrans.gui.pages;

import net.serenitybdd.core.annotations.findby.By;
import net.serenitybdd.core.pages.WebElementFacade;
import net.thucydides.core.pages.PageObject;
import net.thucydides.core.annotations.DefaultUrl;

@DefaultUrl("http://localhost:8080")
public class MainPage extends PageObject {

    public void openConfDocument(String host) {
        find(By.cssSelector("button.open-document[name='" + host + "']")).click();
    }

    public void deleteConfDocument(String host) {
        find(By.cssSelector("button.delete-document[name='" + host + "']")).click();
    }

    public WebElementFacade getConfDocument() {
        return find(By.cssSelector("form[name='jmxForm']"));
    }

}
