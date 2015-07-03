package com.zenika.jmxtrans.gui.repository;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zenika.jmxtrans.gui.model.Document;
import com.zenika.jmxtrans.gui.model.Response;

public interface ConfRepository {
    Collection<Map<String, Object>> findAllHostsAndPorts();

    Response get(String host, int port) throws IOException, InterruptedException, ExecutionException;

    Map<String, Document> getAll() throws IOException;

    void save(Document document) throws InterruptedException, ExecutionException, IOException;

    void deleteOne(String id);

    void updateOne(String id, Document document) throws JsonProcessingException, InterruptedException, ExecutionException;

    void delete(String host, int port);
}
