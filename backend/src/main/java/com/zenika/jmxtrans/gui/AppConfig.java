package com.zenika.jmxtrans.gui;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.close.CloseIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.open.OpenIndexRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@ComponentScan({"com.zenika.jmxtrans.gui.service", "com.zenika.jmxtrans.gui.repository"})
public class AppConfig {
    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);

    public static final String INDEX = ".jmxtrans";
    public static final String CONF_TYPE = "conf";
    public static final String OBJECTNAME_TYPE = "objectname";
    public static final String SETTINGS_TYPE = "writer";
    public static final String SETTINGS_ID = "writer";

    @Value("${elasticsearch.host:}")
    private String host;

    @Value("${elasticsearch.path:}")
    private String path;

    @Value("${java.io.tmpdir}")
    private String tmpdir;

    @Value("${elasticsearch.cluster.name:elasticsearch}")
    private String clusterName;

    @Bean
    public static PropertyPlaceholderConfigurer configurer() {
        PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
        ppc.setIgnoreResourceNotFound(true);
        ppc.setSearchSystemEnvironment(true);
        ppc.setSystemPropertiesMode(PropertyPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_OVERRIDE);
        return ppc;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }

    @Bean(name = "client")
    @Profile("prod")
    public Client clientForProd() {
        logger.info("Elasticsearch client instantiation for prod");
        Client client;
        if (this.host.isEmpty()) {
            logger.info("Starting a new ElasticSearch instance");
            if (this.path.isEmpty()) {
                logger.info("ElasticSearch path: " + this.tmpdir);
                this.path = this.tmpdir;
            }
            Node node = NodeBuilder
                    .nodeBuilder()
                    .clusterName(this.clusterName)
                    .settings(
                            ImmutableSettings
                                    .settingsBuilder()
                                    .put("path.home",
                                            this.path + "/elasticsearch")
                                    .put("path.data",
                                            this.path + "/elasticsearch/data")
                                    .put("path.work",
                                            this.path + "/elasticsearch/work")
                                    .put("path.logs",
                                            this.path + "/elasticsearch/logs")
                                    .build()).node();
            client = node.client();
        } else {
            logger.info("Connecting to ElasticSearch cluster: " + this.host);
            client = new TransportClient();
            String[] host = this.host.split(":");
            TransportAddress address = new InetSocketTransportAddress(host[0],
                    Integer.parseInt(host[1]));
            ((TransportClient) client).addTransportAddress(address);
        }

        try {
            InputStream settings = getClass().getResourceAsStream("/settings.json");

            InputStream confMapping = getClass().getResourceAsStream("/conf_mapping.json");
            InputStream objectnameMapping = getClass().getResourceAsStream("/objectname_mapping.json");
            InputStream settingsMapping = getClass().getResourceAsStream("/settings_mapping.json");

            ObjectMapper mapper = this.objectMapper();

            client.admin().indices().prepareCreate(INDEX)
                    .setSettings(mapper.readValue(settings, Map.class))
                    .addMapping(CONF_TYPE, mapper.readValue(confMapping, Map.class))
                    .addMapping(OBJECTNAME_TYPE, mapper.readValue(objectnameMapping, Map.class))
                    .addMapping(SETTINGS_TYPE, mapper.readValue(settingsMapping, Map.class))
                    .execute().actionGet();

        } catch (ElasticsearchException e) {
            logger.error(e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return client;
    }

    @Bean(name = "client")
    @Profile("test")
    public Client clientForTest() {
        logger.info("Elasticsearch client instantiation for testing");

        ImmutableSettings.Builder elasticsearchSettings = ImmutableSettings.settingsBuilder()
                .put("http.enabled", "false")
                .put("path.data", "target/elasticsearch-data");

        Node node = NodeBuilder.nodeBuilder().local(true).settings(elasticsearchSettings).node();
        Client client = node.client();

        try {
            InputStream settings = getClass().getResourceAsStream("/settings.json");

            InputStream confMapping = getClass().getResourceAsStream("/conf_mapping.json");
            InputStream objectnameMapping = getClass().getResourceAsStream("/objectname_mapping.json");
            InputStream settingsMapping = getClass().getResourceAsStream("/settings_mapping.json");
            ObjectMapper mapper = this.objectMapper();

            client.admin().indices().prepareCreate(INDEX)
                    .setSettings(mapper.readValue(settings, Map.class))
                    .addMapping(CONF_TYPE, mapper.readValue(confMapping, Map.class))
                    .addMapping(OBJECTNAME_TYPE, mapper.readValue(objectnameMapping, Map.class))
                    .addMapping(SETTINGS_TYPE, mapper.readValue(settingsMapping, Map.class))
                    .execute().actionGet();
        } catch (ElasticsearchException e) {
            logger.info(e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        return client;
    }
}
