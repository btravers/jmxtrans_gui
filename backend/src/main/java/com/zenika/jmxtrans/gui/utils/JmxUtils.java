package com.zenika.jmxtrans.gui.utils;

import com.zenika.jmxtrans.gui.model.ObjectNameRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.*;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;

public class JmxUtils {

    private static final Logger logger = LoggerFactory.getLogger(JmxUtils.class);

    public static List<ObjectNameRepresentation> objectNames(String host, int port, String username, String password) {
        String url = "service:jmx:rmi:///jndi/rmi://" + host + ":" + port + "/jmxrmi";
        JMXServiceURL serviceURL = null;
        JMXConnector jmxConnector = null;

        try {
            serviceURL = new JMXServiceURL(url);
            if (username != null && password != null) {
                Map<String, Object> env = new HashMap<>();
                env.put(JMXConnector.CREDENTIALS, new String[]{username, password});
                jmxConnector = JMXConnectorFactory.connect(serviceURL, env);
            } else {
                jmxConnector = JMXConnectorFactory.connect(serviceURL);
            }
            MBeanServerConnection mbeanConn = jmxConnector.getMBeanServerConnection();

            List<ObjectNameRepresentation> result = new ArrayList();

            logger.info("Retrieving MBeans using  " + url);
            Set<ObjectName> beanSet = mbeanConn.queryNames(null, null);
            for (ObjectName name : beanSet) {
                ObjectNameRepresentation tmp = new ObjectNameRepresentation();
                tmp.setHost(host);
                tmp.setPort(port);
                tmp.setName(name.toString());

                List<String> attributes = new ArrayList();
                logger.info("Retrieving attributes for MBeans " + name.toString());
                try {
                    for (MBeanAttributeInfo attr : mbeanConn.getMBeanInfo(name).getAttributes()) {
                        attributes.add(attr.getName());
                    }
                } catch (InstanceNotFoundException e) {
                    logger.error(e.getMessage());
                } catch (IntrospectionException e) {
                    logger.error(e.getMessage());
                } catch (ReflectionException e) {
                    logger.error(e.getMessage());
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
                tmp.setAttributes(attributes);
                result.add(tmp);
            }

            return result;
        } catch (MalformedURLException e) {
            logger.error(e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage());
        } finally {
            if (jmxConnector != null) {
                try {
                    jmxConnector.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
        }

        return new ArrayList<>();
    }
}
