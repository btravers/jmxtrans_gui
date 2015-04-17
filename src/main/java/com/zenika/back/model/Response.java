package com.zenika.back.model;

import org.springframework.stereotype.Component;

@Component
public class Response {

    private Document source;
    private String id;

    public Document getSource() {
	return source;
    }

    public void setSource(Document source) {
	this.source = source;
    }

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

}
