package com.zenika.back.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zenika.back.model.ObjectNameRepresentation;

import java.util.Collection;
import java.util.concurrent.ExecutionException;

public interface ObjectNameRepository {

    void save(ObjectNameRepresentation objectName) throws JsonProcessingException;

    Collection<String> prefixAttrSuggestion(String host, int port, String name);

    Collection<String> prefixNameSuggestion(String host, int port);

    void delete(String host, int port);

}
