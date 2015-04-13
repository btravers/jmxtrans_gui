package com.zenika.back.model;

import java.util.Collection;

public class Query {

	private String obj;
	private Collection<String> keys;
	private Collection<String> attr;
	private Collection<String> typeNames;
	private String resultAlias;
	private boolean allowDottedKeys;
	private Collection<OutputWriter> outputWriters;

	public String getObj() {
		return obj;
	}

	public void setObj(String obj) {
		this.obj = obj;
	}

	public Collection<String> getKeys() {
		return keys;
	}

	public void setKeys(Collection<String> keys) {
		this.keys = keys;
	}

	public Collection<String> getAttr() {
		return attr;
	}

	public void setAttr(Collection<String> attr) {
		this.attr = attr;
	}

	public Collection<String> getTypeNames() {
		return typeNames;
	}

	public void setTypeNames(Collection<String> typeNames) {
		this.typeNames = typeNames;
	}

	public String getResultAlias() {
		return resultAlias;
	}

	public void setResultAlias(String resultAlias) {
		this.resultAlias = resultAlias;
	}

	public boolean isAllowDottedKeys() {
		return allowDottedKeys;
	}

	public void setAllowDottedKeys(boolean allowDottedKeys) {
		this.allowDottedKeys = allowDottedKeys;
	}

	public Collection<OutputWriter> getOutputWriters() {
		return outputWriters;
	}

	public void setOutputWriters(Collection<OutputWriter> outputWriters) {
		this.outputWriters = outputWriters;
	}

}
