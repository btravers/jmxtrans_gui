package com.zenika.back;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.PropertyResolver;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@ComponentScan({ "com.zenika.back.web", "com.zenika.back.service", "com.zenika.back.repository" })
@PropertySource("classpath:/spring/data-access.properties")
@EnableWebMvc
//@ImportResource("classpath:/spring/elasticsearch-config.xml")
public class AppConfig {

    @Autowired
    private PropertyResolver propertyResolver;

    @Bean
    public MultipartResolver multipartResolver() {
    	return new StandardServletMultipartResolver();
    }

    @Bean
    public Client client() {
        TransportClient client = new TransportClient();
        String[] host = propertyResolver.getProperty("elasticsearch", "localhost:9300").split(":");
        TransportAddress address = new InetSocketTransportAddress(host[0], Integer.parseInt(host[1]));
        client.addTransportAddress(address);
        return client;
    }
}
