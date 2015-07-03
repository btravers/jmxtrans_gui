package com.zenika.jmxtrans.gui.model;

import java.util.Map;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OutputWriter {

    @JsonProperty("@class")
    @NotNull @NotEmpty
    public String writer;
    private Map<String, Object> settings;

    public Map<String, Object> getSettings() {
	return settings;
    }

    public void setSettings(Map<String, Object> settings) {
	this.settings = settings;
    }

}
