package com.zenika.back.service;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.zenika.back.model.Document;
import com.zenika.back.model.OutputWriter;
import com.zenika.back.model.Response;

public interface JmxtransService {

    Collection<Map<String, String>> findAllHostsAndPorts();

    Response findDocumentByHostAndPort(String host, int port) throws IOException, InterruptedException, ExecutionException;

    void addDocument(Document document) throws InterruptedException, ExecutionException, IOException;

    void updateDocument(String id, Document document) throws JsonProcessingException, InterruptedException, ExecutionException;

    OutputWriter getSettings() throws IOException;

    void deleteDocument(String host, int port);

    void upload(Document document) throws IOException, ExecutionException, InterruptedException;

    boolean refreshObjectNames(String host, int port) throws JsonProcessingException, InterruptedException, ExecutionException;

    void updateSettings(OutputWriter settings) throws IOException, ExecutionException, InterruptedException;

    Collection<String> prefixAttrSuggestion(String host, int port, String name);

    Collection<String> prefixNameSuggestion(String host, int port);

}
