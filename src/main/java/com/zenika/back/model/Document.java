package com.zenika.back.model;

import java.util.Collection;

public class Document {

	private String name;
	private Collection<Server> servers;
	private Integer numMultiThreadedServers;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Collection<Server> getServers() {
		return servers;
	}

	public void setServers(Collection<Server> servers) {
		this.servers = servers;
	}

	public Integer getNumMultiThreadedServers() {
		return numMultiThreadedServers;
	}

	public void setNumMultiThreadedServers(Integer numMultiThreadedServers) {
		this.numMultiThreadedServers = numMultiThreadedServers;
	}

}
