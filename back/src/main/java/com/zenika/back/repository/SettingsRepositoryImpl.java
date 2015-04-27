package com.zenika.back.repository;

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

    private static final Logger logger = LoggerFactory
            .getLogger(SettingsRepository.class);

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
    public OutputWriter settings() throws IOException {
        GetResponse getResponse = this.client
                .prepareGet(AppConfig.INDEX, AppConfig.SETTINGS_TYPE,
                        AppConfig.SETTINGS_ID).execute().actionGet();

        if (getResponse.isExists()) {
            return mapper.readValue(getResponse.getSourceAsString(),
                    OutputWriter.class);
        } else {
            OutputWriter settings = new OutputWriter();

            this.client
                    .prepareIndex(AppConfig.INDEX, AppConfig.SETTINGS_TYPE,
                            AppConfig.SETTINGS_ID).setSource(mapper.writeValueAsString(settings))
                    .execute().actionGet();

            return settings;
        }
    }

    @Override
    public void save(OutputWriter settings) throws IOException {
        String json = mapper.writeValueAsString(settings);

        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.index(AppConfig.INDEX);
        updateRequest.type(AppConfig.SETTINGS_TYPE);
        updateRequest.id(AppConfig.SETTINGS_ID);
        updateRequest.doc(json);

        this.client.update(updateRequest);
    }
}
