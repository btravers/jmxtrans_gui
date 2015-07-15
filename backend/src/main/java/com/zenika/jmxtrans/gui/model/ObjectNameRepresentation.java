package com.zenika.jmxtrans.gui.model;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

public class ObjectNameRepresentation {

    @NotNull
    @NotEmpty
    private String host;
    @NotNull
    @NotEmpty
    private Integer port;
    @NotNull
    @NotEmpty
    private String name;
    @NotNull
    private String suggest;

    public String getSuggest() {
        return suggest;
    }

    public void setSuggest(String suggest) {
        this.suggest = suggest;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
