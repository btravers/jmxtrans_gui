package com.zenika.back.repository;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.zenika.back.model.Document;
import com.zenika.back.model.Response;
import com.zenika.back.model.WriterSettings;

public interface ServerRepositoryCustom {
    String findAllHost() throws JsonProcessingException;

    Response getByHost(String host) throws JsonParseException, JsonMappingException, IOException, InterruptedException, ExecutionException;

	void save(Document server) throws JsonProcessingException, InterruptedException, ExecutionException, IOException;

	void deleteOne(String id);

	void updateOne(String id, Document server) throws JsonProcessingException, InterruptedException, ExecutionException;

	void delete(String host);

	WriterSettings settings() throws JsonParseException, JsonMappingException, IOException;

	void updateSettings(WriterSettings settings) throws JsonProcessingException;
}
