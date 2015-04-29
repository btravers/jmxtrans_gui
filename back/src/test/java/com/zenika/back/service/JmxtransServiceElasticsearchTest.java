package com.zenika.back.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zenika.back.AppConfig;
import com.zenika.back.model.ObjectNameRepresentation;
import org.apache.commons.io.IOUtils;
import org.elasticsearch.action.admin.indices.flush.FlushRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.io.InputStream;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
@ActiveProfiles("test")
public class JmxtransServiceElasticsearchTest extends AbsractJmxtransServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(JmxtransServiceElasticsearchTest.class);

    private Client client;

    @Autowired
    public void setClient(Client client) {
        this.client = client;
    }

    protected void flushChanges() {
        this.client.admin().indices().flush(new FlushRequest(AppConfig.INDEX)).actionGet();
    }

    @Override
    public void setUp() {
        InputStream writer = getClass().getResourceAsStream("/test/bluefloodWriter.json");
        try {
            this.client.prepareIndex(AppConfig.INDEX, AppConfig.SETTINGS_TYPE, AppConfig.SETTINGS_ID)
                    .setSource(IOUtils.toString(writer))
                    .execute().actionGet();
        } catch (IOException e) {
            e.printStackTrace();
        }

        InputStream server1 = getClass().getResourceAsStream("/test/document1.json");
        try {
            this.client.prepareIndex(AppConfig.INDEX, AppConfig.CONF_TYPE)
                    .setSource(IOUtils.toString(server1))
                    .execute().actionGet();
        } catch (IOException e) {
            e.printStackTrace();
        }

        InputStream server2 = getClass().getResourceAsStream("/test/document2.json");
        try {
            this.client.prepareIndex(AppConfig.INDEX, AppConfig.CONF_TYPE)
                    .setSource(IOUtils.toString(server2))
                    .execute().actionGet();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ObjectNameRepresentation[] objectnames = new ObjectNameRepresentation[0];
        try {
            objectnames = this.mapper.readValue(getClass().getResourceAsStream("/test/objectnames.json"), ObjectNameRepresentation[].class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (ObjectNameRepresentation obj : objectnames) {
            try {
                this.client.prepareIndex(AppConfig.INDEX, AppConfig.OBJECTNAME_TYPE)
                        .setSource(mapper.writeValueAsString(obj))
                        .execute().actionGet();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        this.flushChanges();
    }

    @Override
    public void tearDown() {
        this.client.prepareDeleteByQuery(AppConfig.INDEX)
                .setTypes(AppConfig.CONF_TYPE)
                .setQuery(QueryBuilders.matchAllQuery())
                .execute().actionGet();

        this.client.prepareDeleteByQuery(AppConfig.INDEX)
                .setTypes(AppConfig.SETTINGS_TYPE)
                .setQuery(QueryBuilders.matchAllQuery())
                .execute().actionGet();

        this.client.prepareDeleteByQuery(AppConfig.INDEX)
                .setTypes(AppConfig.OBJECTNAME_TYPE)
                .setQuery(QueryBuilders.matchAllQuery())
                .execute().actionGet();
    }

}
