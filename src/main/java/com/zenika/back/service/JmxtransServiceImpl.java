package com.zenika.back.service;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.zenika.back.model.Document;
import com.zenika.back.model.OutputWriter;
import com.zenika.back.model.Response;
import com.zenika.back.repository.ServerRepositoryCustom;

@Service
public class JmxtransServiceImpl implements JmxtransService {

    private ServerRepositoryCustom serverRepositoryCustom;

    @Autowired
    public void setElasticsearchOperations(
	    ServerRepositoryCustom serverRepositoryCustom) {
	this.serverRepositoryCustom = serverRepositoryCustom;
    }

    @Override
    public Collection<String> findHosts() throws JsonProcessingException {
	return this.serverRepositoryCustom.findAllHost();
    }

    @Override
    public Response findServersByHost(String host) throws JsonParseException,
	    JsonMappingException, IOException, InterruptedException,
	    ExecutionException {
	return this.serverRepositoryCustom.getByHost(host);
    }

    @Override
    public void deleteServer(String host) {
	this.serverRepositoryCustom.delete(host);
    }

    @Override
    public void addServer(Document server) throws InterruptedException,
	    ExecutionException, IOException {
	this.serverRepositoryCustom.save(server);
    }

    @Override
    public void updateServer(String id, Document server)
	    throws JsonProcessingException, InterruptedException,
	    ExecutionException {
	this.serverRepositoryCustom.updateOne(id, server);
    }

    @Override
    public OutputWriter getSettings() throws JsonParseException,
	    JsonMappingException, IOException {
	return this.serverRepositoryCustom.settings();
    }

    @Override
    public void updateSettings(OutputWriter settings) throws IOException {
	this.serverRepositoryCustom.updateSettings(settings);
    }

    @Override
    public void refresh(String host, int port) throws JsonProcessingException,
	    InterruptedException, ExecutionException {
	this.serverRepositoryCustom.refresh(host, port);
    }

    @Override
    public Collection<String> prefixNameSuggestion(String host) {
	return this.serverRepositoryCustom.prefixNameSuggestion(host);
    }

    @Override
    public Collection<String> prefixAttrSuggestion(String host, String name) {
	return this.serverRepositoryCustom.prefixAttrSuggestion(host, name);
    }

}
