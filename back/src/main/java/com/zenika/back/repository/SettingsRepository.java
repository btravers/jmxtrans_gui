package com.zenika.back.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zenika.back.model.OutputWriter;

import java.io.IOException;

public interface SettingsRepository {

    OutputWriter get() throws IOException;

    void save(OutputWriter settings) throws IOException;

    void update(OutputWriter settings) throws JsonProcessingException;

    void delete();
}
