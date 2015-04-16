package com.zenika.back.model;

import java.util.Map;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OutputWriter {

    @JsonProperty("@class")
    @NotNull
    public String writer;

    private Map<String, Object> settings;

    public Map<String, Object> getSettings() {
	return settings;
    }

    public void setSettings(Map<String, Object> settings) {
	this.settings = settings;
    }

}
