package com.zenika.back.model;

import java.util.Collection;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

public class ObjectNameRepresentation {

    @NotNull @NotEmpty
    private String host;
    @NotNull @NotEmpty
    private String name;
    @NotEmpty
    @Valid
    private Collection<String> attributes;

    public String getHost() {
	return host;
    }

    public void setHost(String host) {
	this.host = host;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public Collection<String> getAttributes() {
	return attributes;
    }

    public void setAttributes(Collection<String> attributes) {
	this.attributes = attributes;
    }

}
