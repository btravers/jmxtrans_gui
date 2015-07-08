package com.zenika.jmxtrans.gui.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zenika.jmxtrans.gui.model.ObjectNameRepresentation;

import java.util.Collection;
import java.util.List;

public interface MBeanInformation {

    void save(List<ObjectNameRepresentation> objectNames) throws JsonProcessingException;

    Collection<String> getObjectNames(String host, int port);

    Collection<String> getObjectName(String name);

    void delete(String host, int port);

}
