package com.zenika.jmxtrans.gui.utils;

import com.zenika.jmxtrans.gui.model.ObjectNameRepresentation;
import com.zenika.jmxtrans.gui.model.Suggest;
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

    public static List<ObjectNameRepresentation> objectNames(JMXConnector jmxConnector, String host, int port) {

        MBeanServerConnection mbeanConn = null;
        try {
            mbeanConn = jmxConnector.getMBeanServerConnection();

            List<ObjectNameRepresentation> result = new ArrayList<>();

            logger.info("Retrieving MBeans using  " + host + ":" + port);
            Set<ObjectName> beanSet = mbeanConn.queryNames(null, null);
            for (ObjectName name : beanSet) {
                ObjectNameRepresentation tmp = new ObjectNameRepresentation();
                tmp.setHost(host);
                tmp.setPort(port);
                tmp.setName(name.toString());
                Suggest suggest = new Suggest();
                suggest.setInput(Arrays.asList(name.toString()));
                suggest.setOutput(name.toString());
                tmp.setSuggest(name.toString());

                result.add(tmp);
            }

            return result;
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        return null;
    }

    public static List<String> attributes(JMXConnector jmxConnector, String host, int port, String objectname) throws MalformedObjectNameException {

        MBeanServerConnection mbeanConn = null;
        try {
            mbeanConn = jmxConnector.getMBeanServerConnection();

            ObjectName name = new ObjectName(objectname);

            List<String> attributes = new ArrayList<>();
            logger.info("Retrieving attributes for MBeans " + name.toString());
            if (mbeanConn.isRegistered(name)) {
                for (MBeanAttributeInfo attr : mbeanConn.getMBeanInfo(name).getAttributes()) {
                    attributes.add(attr.getName());
                }
            }
            return attributes;
        } catch (IOException e) {
            logger.error(e.getMessage());
        } catch (IntrospectionException e) {
            logger.error(e.getMessage());
        } catch (ReflectionException e) {
            logger.error(e.getMessage());
        } catch (InstanceNotFoundException e) {
            logger.error(e.getMessage());
        }

        return Collections.emptyList();
    }
}
