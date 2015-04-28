package com.zenika.back.service;

import com.zenika.back.AppConfig;
import org.apache.commons.io.IOUtils;
import org.elasticsearch.action.admin.indices.flush.FlushRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.After;
import org.junit.Before;
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

    protected void forceUpdate() {
        this.client.admin().indices().flush(new FlushRequest(AppConfig.INDEX)).actionGet();
    }

    @Before
    public void setUp() throws IOException {
        InputStream writer = getClass().getResourceAsStream("/test/bluefloodWriter.json");
        IndexResponse indexResponse = this.client.prepareIndex(AppConfig.INDEX, AppConfig.SETTINGS_TYPE, AppConfig.SETTINGS_ID)
                .setSource(IOUtils.toString(writer))
                .execute().actionGet();
        logger.info("Index writer: " + indexResponse.isCreated());

        InputStream server1 = getClass().getResourceAsStream("/test/document1.json");
        indexResponse = this.client.prepareIndex(AppConfig.INDEX, AppConfig.CONF_TYPE)
                .setSource(IOUtils.toString(server1))
                .execute().actionGet();
        logger.info("Index server 1: " + indexResponse.isCreated());

        InputStream server2 = getClass().getResourceAsStream("/test/document2.json");
        indexResponse = this.client.prepareIndex(AppConfig.INDEX, AppConfig.CONF_TYPE)
                .setSource(IOUtils.toString(server2))
                .execute().actionGet();
        logger.info("Index server 2: " + indexResponse.isCreated());

        this.forceUpdate();
    }

    @After
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
