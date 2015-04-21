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

public interface ServerRepositoryCustom {
    Collection<Map<String, String>> findAllHost() throws JsonProcessingException, IOException;

    Response getByHost(String host, int port) throws JsonParseException,
	    JsonMappingException, IOException, InterruptedException,
	    ExecutionException;

    void save(Document server) throws JsonProcessingException,
	    InterruptedException, ExecutionException, IOException;

    void deleteOne(String id);

    void updateOne(String id, Document server) throws JsonProcessingException,
	    InterruptedException, ExecutionException;

    void delete(String host, int port);

    OutputWriter settings() throws JsonParseException, JsonMappingException,
	    IOException;

    void refresh(String host, int port) throws JsonProcessingException,
	    InterruptedException, ExecutionException;

    void updateSettings(OutputWriter settings) throws IOException;

    Collection<String> prefixAttrSuggestion(String host, int port, String name);

    Collection<String> prefixNameSuggestion(String host, int port);

}
