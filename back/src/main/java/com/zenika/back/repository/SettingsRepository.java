package com.zenika.back.repository;

import com.zenika.back.model.OutputWriter;

import java.io.IOException;

public interface SettingsRepository {

    OutputWriter settings() throws IOException;

    void save(OutputWriter settings) throws IOException;

}
