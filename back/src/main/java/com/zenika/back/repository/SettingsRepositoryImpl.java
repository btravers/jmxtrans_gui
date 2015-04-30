package com.zenika.back.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zenika.back.AppConfig;
import com.zenika.back.model.OutputWriter;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;

@Repository
public class SettingsRepositoryImpl implements SettingsRepository {

    private Client client;
    private ObjectMapper mapper;

    @Autowired
    public void setClient(Client client) {
        this.client = client;
    }

    @Autowired
    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public OutputWriter get() throws IOException {
        GetResponse getResponse = this.client.prepareGet(AppConfig.INDEX, AppConfig.SETTINGS_TYPE, AppConfig.SETTINGS_ID)
                .execute().actionGet();

        if (getResponse.isExists()) {
            return mapper.readValue(getResponse.getSourceAsString(),
                    OutputWriter.class);
        } else {
            return null;
        }
    }

    @Override
    public void save(OutputWriter settings) throws IOException {
        String json = mapper.writeValueAsString(settings);

        this.client.prepareIndex(AppConfig.INDEX, AppConfig.SETTINGS_TYPE, AppConfig.SETTINGS_ID)
                .setRefresh(true)
                .setSource(json)
                .execute().actionGet();
    }

    @Override
    public void update(OutputWriter settings) throws JsonProcessingException {
        String json = mapper.writeValueAsString(settings);

        this.client.prepareUpdate(AppConfig.INDEX, AppConfig.SETTINGS_TYPE, AppConfig.SETTINGS_ID)
                .setRefresh(true)
                .setDoc(json)
                .execute().actionGet();
    }

    @Override
    public void delete() {
        this.client.prepareDelete(AppConfig.INDEX, AppConfig.SETTINGS_TYPE, AppConfig.SETTINGS_ID)
                .execute().actionGet();
    }
}
