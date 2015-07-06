package com.zenika.jmxtrans.gui.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zenika.jmxtrans.gui.model.ObjectNameRepresentation;

import java.util.Collection;
import java.util.List;

public interface ObjectNameRepository {

    void save(List<ObjectNameRepresentation> objectNames) throws JsonProcessingException;

    Collection<String> prefixAttrSuggestion(String host, int port, String name);

    Collection<String> prefixNameSuggestion(String host, int port);

    void delete(String host, int port);

}
