package com.zenika.jmxtrans.gui.service;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zenika.jmxtrans.gui.model.Document;
import com.zenika.jmxtrans.gui.model.OutputWriter;
import com.zenika.jmxtrans.gui.model.Response;

import javax.management.MalformedObjectNameException;

public interface JmxtransService {

    Collection<Map<String, Object>> findAllHostsAndPorts();

    Response findDocumentByHostAndPort(String host, int port) throws InterruptedException, ExecutionException, IOException;

    void addDocument(Document document) throws InterruptedException, ExecutionException, IOException;

    void updateDocument(String id, Document document) throws JsonProcessingException, InterruptedException, ExecutionException;

    OutputWriter getSettings() throws IOException;

    void deleteDocument(String host, int port);

    void upload(Document document) throws IOException, ExecutionException, InterruptedException;

    void updateSettings(OutputWriter settings) throws IOException, ExecutionException, InterruptedException;

    Collection<String> objectNames(String host, int port, String obj);

    boolean refreshObjectNames(String host, int port, String username, String password) throws JsonProcessingException, InterruptedException, ExecutionException;

    Collection<String> attributes(String host, int port, String username, String password, String objectname) throws MalformedObjectNameException;

    boolean existJMXAgent(String host, int port, String username, String password);
}
