package com.zenika.jmxtrans.gui.utils;

import com.zenika.jmxtrans.gui.exception.JmxtransException;
import org.apache.commons.pool.BaseKeyedPoolableObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.util.HashMap;
import java.util.Map;

public class SocketFactory extends BaseKeyedPoolableObjectFactory<JMXAgent, JMXConnector> {
    private static final Logger logger = LoggerFactory.getLogger(SocketFactory.class);

    public JMXConnector makeObject(JMXAgent key) throws Exception {

        if (key.getHost() == null || key.getPort() == null) {
            throw new JmxtransException("Host and key cannot be null.");
        }

        String url = "service:jmx:rmi:///jndi/rmi://" + key.getHost() + ":" + key.getPort() + "/jmxrmi";
        JMXServiceURL serviceURL = new JMXServiceURL(url);

        if (key.getUsername() != null && key.getPassword() != null) {
            Map<String, Object> env = new HashMap<>();
            env.put(JMXConnector.CREDENTIALS, new String[]{key.getUsername(), key.getPassword()});
            return JMXConnectorFactory.connect(serviceURL, env);
        }

        return JMXConnectorFactory.connect(serviceURL);
    }

    @Override
    public void destroyObject(JMXAgent key, JMXConnector jmxConnector) throws Exception {
        jmxConnector.close();
    }
}
