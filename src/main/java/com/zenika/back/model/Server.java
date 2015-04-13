package com.zenika.back.model;

import java.util.Collection;

public class Server {

	private String alias;
	private String host;
	private String port;
	private String username;
	private String password;
	private String protocolProviderPackages;
	private String url;
	private String cronExpression;
	private Integer numQueryThreads;

	// if using local JMX to embed JmxTrans to query the local MBeanServer
	private boolean local;

	private Collection<Query> queries;

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getProtocolProviderPackages() {
		return protocolProviderPackages;
	}

	public void setProtocolProviderPackages(String protocolProviderPackages) {
		this.protocolProviderPackages = protocolProviderPackages;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCronExpression() {
		return cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	public Integer getNumQueryThreads() {
		return numQueryThreads;
	}

	public void setNumQueryThreads(Integer numQueryThreads) {
		this.numQueryThreads = numQueryThreads;
	}

	public boolean isLocal() {
		return local;
	}

	public void setLocal(boolean local) {
		this.local = local;
	}

	public Collection<Query> getQueries() {
		return queries;
	}

	public void setQueries(Collection<Query> queries) {
		this.queries = queries;
	}

}
