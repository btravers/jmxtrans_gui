package com.zenika.back.repository;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.zenika.back.model.Document;
import com.zenika.back.model.OutputWriter;
import com.zenika.back.model.Response;

public interface ServerRepositoryCustom {
    Collection<String> findAllHost() throws JsonProcessingException;

    Response getByHost(String host) throws JsonParseException,
	    JsonMappingException, IOException, InterruptedException,
	    ExecutionException;

    void save(Document server) throws JsonProcessingException,
	    InterruptedException, ExecutionException, IOException;

    void deleteOne(String id);

    void updateOne(String id, Document server) throws JsonProcessingException,
	    InterruptedException, ExecutionException;

    void delete(String host);

    OutputWriter settings() throws JsonParseException, JsonMappingException,
	    IOException;

    void refresh(String host, int port) throws JsonProcessingException,
	    InterruptedException, ExecutionException;

    Collection<String> prefixNameSuggestion(String host, String prefix);

    Collection<String> prefixAttrSuggestion(String host, String name,
	    String prefix);

    void updateSettings(OutputWriter settings) throws IOException;

}
