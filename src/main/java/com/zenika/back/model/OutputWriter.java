package com.zenika.back.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OutputWriter {

    static class Settings {
	private List<String> typeNames;
	private boolean booleanAsNumber;
	private boolean debug;
	private String host;
	private Integer port;
	private Integer ttl;
	private String outputFile;
	private Long maxLogFileSize;
	private Long maxLogBackupFiles;
	private String delimiter;
	private String datePattern;
	private String addressingMode;
	private boolean v31;
	private String units;
	private boolean slope;
	private Long tmax;
	private Long dmax;
	private String groupeName;
	private Long libratoApiTimeoutInMillis;	
	private String username;
	private String token;
	private String proxyHost;
	private Integer proxPort;
	private List<String> filters;
	private List<String> tresholds;
	private String nagiosHost;
	private String prefix;
	private String suffix;
	private Map<String, String> tags;
	private String tagName;
	private boolean mergeTypeNamesTags;
	private String metricNamingExpression;
	private boolean addHostnameTag;
	private String binaryPath;
	private boolean generate;
	private String templateFile;
	private Long timeoutInMillis;
	private String gatewayUrl;
	private String apiKey;
	private String source;
	private String detectInstance;
	private String handler;
	private String bucketType;
	private String rootPrefix;
	
	public List<String> getTypeNames() {
	    return typeNames;
	}

	public void setTypeNames(List<String> typeNames) {
	    this.typeNames = typeNames;
	}

	public boolean isBooleanAsNumber() {
	    return booleanAsNumber;
	}

	public void setBooleanAsNumber(boolean booleanAsNumber) {
	    this.booleanAsNumber = booleanAsNumber;
	}

	public Integer getTtl() {
	    return ttl;
	}

	public void setTtl(Integer ttl) {
	    this.ttl = ttl;
	}

	public Long getMaxLogFileSize() {
	    return maxLogFileSize;
	}

	public void setMaxLogFileSize(Long maxLogFileSize) {
	    this.maxLogFileSize = maxLogFileSize;
	}

	public Long getMaxLogBackupFiles() {
	    return maxLogBackupFiles;
	}

	public void setMaxLogBackupFiles(Long maxLogBackupFiles) {
	    this.maxLogBackupFiles = maxLogBackupFiles;
	}

	public String getDelimiter() {
	    return delimiter;
	}

	public void setDelimiter(String delimiter) {
	    this.delimiter = delimiter;
	}

	public String getDatePattern() {
	    return datePattern;
	}

	public void setDatePattern(String datePattern) {
	    this.datePattern = datePattern;
	}

	public String getAddressingMode() {
	    return addressingMode;
	}

	public void setAddressingMode(String addressingMode) {
	    this.addressingMode = addressingMode;
	}

	public boolean isV31() {
	    return v31;
	}

	public void setV31(boolean v31) {
	    this.v31 = v31;
	}

	public String getUnits() {
	    return units;
	}

	public void setUnits(String units) {
	    this.units = units;
	}

	public boolean isSlope() {
	    return slope;
	}

	public void setSlope(boolean slope) {
	    this.slope = slope;
	}

	public Long getTmax() {
	    return tmax;
	}

	public void setTmax(Long tmax) {
	    this.tmax = tmax;
	}

	public Long getDmax() {
	    return dmax;
	}

	public void setDmax(Long dmax) {
	    this.dmax = dmax;
	}

	public String getGroupeName() {
	    return groupeName;
	}

	public void setGroupeName(String groupeName) {
	    this.groupeName = groupeName;
	}

	public Long getLibratoApiTimeoutInMillis() {
	    return libratoApiTimeoutInMillis;
	}

	public void setLibratoApiTimeoutInMillis(Long libratoApiTimeoutInMillis) {
	    this.libratoApiTimeoutInMillis = libratoApiTimeoutInMillis;
	}

	public String getToken() {
	    return token;
	}

	public void setToken(String token) {
	    this.token = token;
	}

	public String getProxyHost() {
	    return proxyHost;
	}

	public void setProxyHost(String proxyHost) {
	    this.proxyHost = proxyHost;
	}

	public Integer getProxPort() {
	    return proxPort;
	}

	public void setProxPort(Integer proxPort) {
	    this.proxPort = proxPort;
	}

	public List<String> getFilters() {
	    return filters;
	}

	public void setFilters(List<String> filters) {
	    this.filters = filters;
	}

	public List<String> getTresholds() {
	    return tresholds;
	}

	public void setTresholds(List<String> tresholds) {
	    this.tresholds = tresholds;
	}

	public String getNagiosHost() {
	    return nagiosHost;
	}

	public void setNagiosHost(String nagiosHost) {
	    this.nagiosHost = nagiosHost;
	}

	public String getPrefix() {
	    return prefix;
	}

	public void setPrefix(String prefix) {
	    this.prefix = prefix;
	}

	public String getSuffix() {
	    return suffix;
	}

	public void setSuffix(String suffix) {
	    this.suffix = suffix;
	}

	public Map<String, String> getTags() {
	    return tags;
	}

	public void setTags(Map<String, String> tags) {
	    this.tags = tags;
	}

	public String getTagName() {
	    return tagName;
	}

	public void setTagName(String tagName) {
	    this.tagName = tagName;
	}

	public boolean isMergeTypeNamesTags() {
	    return mergeTypeNamesTags;
	}

	public void setMergeTypeNamesTags(boolean mergeTypeNamesTags) {
	    this.mergeTypeNamesTags = mergeTypeNamesTags;
	}

	public String getMetricNamingExpression() {
	    return metricNamingExpression;
	}

	public void setMetricNamingExpression(String metricNamingExpression) {
	    this.metricNamingExpression = metricNamingExpression;
	}

	public boolean isAddHostnameTag() {
	    return addHostnameTag;
	}

	public void setAddHostnameTag(boolean addHostnameTag) {
	    this.addHostnameTag = addHostnameTag;
	}

	public Long getTimeoutInMillis() {
	    return timeoutInMillis;
	}

	public void setTimeoutInMillis(Long timeoutInMillis) {
	    this.timeoutInMillis = timeoutInMillis;
	}

	public String getGatewayUrl() {
	    return gatewayUrl;
	}

	public void setGatewayUrl(String gatewayUrl) {
	    this.gatewayUrl = gatewayUrl;
	}

	public String getApiKey() {
	    return apiKey;
	}

	public void setApiKey(String apiKey) {
	    this.apiKey = apiKey;
	}

	public String getSource() {
	    return source;
	}

	public void setSource(String source) {
	    this.source = source;
	}

	public String getDetectInstance() {
	    return detectInstance;
	}

	public void setDetectInstance(String detectInstance) {
	    this.detectInstance = detectInstance;
	}

	public String getHandler() {
	    return handler;
	}

	public void setHandler(String handler) {
	    this.handler = handler;
	}

	public String getBucketType() {
	    return bucketType;
	}

	public void setBucketType(String bucketType) {
	    this.bucketType = bucketType;
	}

	public String getRootPrefix() {
	    return rootPrefix;
	}

	public void setRootPrefix(String rootPrefix) {
	    this.rootPrefix = rootPrefix;
	}

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
