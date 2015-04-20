package com.zenika.back.service;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.zenika.back.model.Document;
import com.zenika.back.model.OutputWriter;
import com.zenika.back.model.Response;

public interface JmxtransService {

    Collection<String> findHosts() throws JsonProcessingException, IOException;

    Response findServersByHost(String host, int port) throws JsonParseException,
	    JsonMappingException, IOException, InterruptedException,
	    ExecutionException;

    void addServer(Document server) throws JsonProcessingException,
	    InterruptedException, ExecutionException, IOException;

    void updateServer(String id, Document server)
	    throws JsonProcessingException, InterruptedException,
	    ExecutionException;

    OutputWriter getSettings() throws JsonParseException, JsonMappingException,
	    IOException;

    void deleteServer(String host, int port);

    void refresh(String host, int port) throws JsonProcessingException,
	    InterruptedException, ExecutionException;

    void updateSettings(OutputWriter settings) throws IOException;

    Collection<String> prefixAttrSuggestion(String host, int port, String name);

    Collection<String> prefixNameSuggestion(String host, int port);

}
