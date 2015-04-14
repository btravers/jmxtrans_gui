package com.zenika.back.model;

import java.util.Collection;

public class ObjectNameRepresentation {

    private String jmxhost;
    private String name;
    private Collection<String> attributes;

    public String getJMXHost() {
	return jmxhost;
    }

    public void setJMXhost(String jmxhost) {
	this.jmxhost = jmxhost;
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
