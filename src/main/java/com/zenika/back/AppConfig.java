package com.zenika.back;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@ComponentScan({ "com.zenika.back.web", "com.zenika.back.service", "com.zenika.back.repository" })

@EnableWebMvc
public class AppConfig {

    @Value("${elasticsearch}")
    private String host;
    
    @Bean
    public static PropertyPlaceholderConfigurer configurer() {
    	PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
    	ppc.setLocations(new ClassPathResource("/spring/data-access.properties"));
    	ppc.setLocations(new FileSystemResource("${back.home}/conf/data-access.properties"));
    	ppc.setIgnoreResourceNotFound(true);
    	ppc.setSearchSystemEnvironment(true);
    	ppc.setSystemPropertiesMode(PropertyPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_OVERRIDE);
    	return ppc;
    }
    
    @Bean
    public MultipartResolver multipartResolver() {
    	return new StandardServletMultipartResolver();
    }

    @Bean
    public Client client() {
        TransportClient client = new TransportClient();
        String[] host = this.host.split(":");
        TransportAddress address = new InetSocketTransportAddress(host[0], Integer.parseInt(host[1]));
        client.addTransportAddress(address);
        return client;
    }
}
