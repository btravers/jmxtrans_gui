package com.zenika.jmxtrans.gui.service;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zenika.jmxtrans.gui.model.Document;
import com.zenika.jmxtrans.gui.model.OutputWriter;
import com.zenika.jmxtrans.gui.model.Response;

public interface JmxtransService {

    Collection<Map<String, Object>> findAllHostsAndPorts();

    Response findDocumentByHostAndPort(String host, int port) throws InterruptedException, ExecutionException, IOException;

    void addDocument(Document document) throws InterruptedException, ExecutionException, IOException;

    void updateDocument(String id, Document document) throws JsonProcessingException, InterruptedException, ExecutionException;

    OutputWriter getSettings() throws IOException;

    void deleteDocument(String host, int port);

    void upload(Document document) throws IOException, ExecutionException, InterruptedException;

    void updateSettings(OutputWriter settings) throws IOException, ExecutionException, InterruptedException;

    Collection<String> prefixAttrSuggestion(String host, int port, String name);

    Collection<String> prefixNameSuggestion(String host, int port);

    void refreshObjectNames(String host, int port, String username, String password) throws JsonProcessingException, InterruptedException, ExecutionException;
}
