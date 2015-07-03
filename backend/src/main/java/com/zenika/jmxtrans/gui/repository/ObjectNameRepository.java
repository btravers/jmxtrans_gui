package com.zenika.jmxtrans.gui.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zenika.jmxtrans.gui.model.ObjectNameRepresentation;

import java.util.Collection;

public interface ObjectNameRepository {

    void save(ObjectNameRepresentation objectName) throws JsonProcessingException;

    Collection<String> prefixAttrSuggestion(String host, int port, String name);

    Collection<String> prefixNameSuggestion(String host, int port);

    void delete(String host, int port);

}
