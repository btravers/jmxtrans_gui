package com.zenika.back.repository;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Collection;
import java.util.concurrent.ExecutionException;

public interface ObjectNameRepository {

    void refresh(String host, int port) throws JsonProcessingException, InterruptedException, ExecutionException;

    Collection<String> prefixAttrSuggestion(String host, int port, String name);

    Collection<String> prefixNameSuggestion(String host, int port);

    void delete(String host, int port);

}
