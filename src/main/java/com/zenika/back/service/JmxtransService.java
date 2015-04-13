package com.zenika.back.service;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.zenika.back.model.Document;
import com.zenika.back.model.Response;
import com.zenika.back.model.WriterSettings;

public interface JmxtransService {

    Collection<String> findHosts() throws JsonProcessingException;

    Response findServersByHost(String host) throws JsonParseException, JsonMappingException, IOException, InterruptedException, ExecutionException;

    void deleteServerById(String id);

    void addServer(Document server) throws JsonProcessingException, InterruptedException, ExecutionException, IOException;

    void updateServer(String id, Document server) throws JsonProcessingException, InterruptedException, ExecutionException;

	WriterSettings getSettings() throws JsonParseException, JsonMappingException, IOException;

	void updateSettings(WriterSettings settings) throws JsonProcessingException;

	void deleteServer(String host);

	void refresh(String host, int port) throws JsonProcessingException, InterruptedException, ExecutionException;

}
