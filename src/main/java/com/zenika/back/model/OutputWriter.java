package com.zenika.back.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OutputWriter {

	static class Settings {
		private String host;
		private Integer port;
		private String username;
		private String password;
		private String templateFile;
		private String outputFile;
		private String binaryPath;
		private boolean debug;
		private boolean generate;

		public String getTemplateFile() {
			return templateFile;
		}

		public void setTemplateFile(String templateFile) {
			this.templateFile = templateFile;
		}

		public String getOutputFile() {
			return outputFile;
		}

		public void setOutputFile(String outputFile) {
			this.outputFile = outputFile;
		}

		public String getBinaryPath() {
			return binaryPath;
		}

		public void setBinaryPath(String binaryPath) {
			this.binaryPath = binaryPath;
		}

		public boolean isDebug() {
			return debug;
		}

		public void setDebug(boolean debug) {
			this.debug = debug;
		}

		public boolean isGenerate() {
			return generate;
		}

		public void setGenerate(boolean generate) {
			this.generate = generate;
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
	}

	@JsonProperty("@class")
	public String writer;

	private Settings settings;

	public Settings getSettings() {
		return settings;
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
	}

}
