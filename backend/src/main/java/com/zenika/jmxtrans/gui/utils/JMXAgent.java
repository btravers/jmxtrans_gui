package com.zenika.jmxtrans.gui.utils;

public class JMXAgent {

    private String host;
    private Integer port;
    private String username;
    private String password;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JMXAgent)) return false;

        JMXAgent jmxAgent = (JMXAgent) o;

        if (host != null ? !host.equals(jmxAgent.host) : jmxAgent.host != null) return false;
        if (port != null ? !port.equals(jmxAgent.port) : jmxAgent.port != null) return false;
        if (username != null ? !username.equals(jmxAgent.username) : jmxAgent.username != null) return false;
        return !(password != null ? !password.equals(jmxAgent.password) : jmxAgent.password != null);

    }

    @Override
    public int hashCode() {
        int result = host != null ? host.hashCode() : 0;
        result = 31 * result + (port != null ? port.hashCode() : 0);
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }
}
