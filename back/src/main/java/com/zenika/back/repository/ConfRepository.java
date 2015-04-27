package com.zenika.back.repository;

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

public interface ConfRepository {
    Collection<Map<String, String>> findAllHost();

    Response get(String host, int port) throws IOException, InterruptedException, ExecutionException;

    Map<String, Document> getAll() throws IOException;

    void save(Document server) throws InterruptedException, ExecutionException, IOException;

    void deleteOne(String id);

    void updateOne(String id, Document server) throws JsonProcessingException, InterruptedException, ExecutionException;

    void delete(String host, int port);

}
