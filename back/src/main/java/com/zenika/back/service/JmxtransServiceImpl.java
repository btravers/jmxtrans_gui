package com.zenika.back.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.zenika.back.model.*;
import com.zenika.back.repository.ObjectNameRepository;
import com.zenika.back.repository.SettingsRepository;
import com.zenika.back.utils.JmxUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zenika.back.repository.ConfRepository;

@Service
public class JmxtransServiceImpl implements JmxtransService {

    private ConfRepository confRepository;
    private SettingsRepository settingsRepository;
    private ObjectNameRepository objectNameRepository;

    @Autowired
    public void setConfRepository(ConfRepository confRepository) {
        this.confRepository = confRepository;
    }

    @Autowired
    public void setSettingsRepository(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    @Autowired
    public void setObjectNameRepository(ObjectNameRepository objectNameRepository) {
        this.objectNameRepository = objectNameRepository;
    }

    @Override
    public Collection<Map<String, Object>> findAllHostsAndPorts() {
        return this.confRepository.findAllHostsAndPorts();
    }

    @Override
    public Response findDocumentByHostAndPort(String host, int port) throws IOException, InterruptedException, ExecutionException {
        return this.confRepository.get(host, port);
    }

    @Override
    public void deleteDocument(String host, int port) {
        this.confRepository.delete(host, port);
        this.objectNameRepository.delete(host, port);
    }

    @Override
    public void addDocument(Document document) throws InterruptedException, ExecutionException, IOException {
        Server server = document.getServers().iterator().next();
        Response response = this.confRepository.get(server.getHost(), server.getPort());

        if (response.getId() != null) {
            this.updateDocument(response.getId(), document);
        } else {
            this.confRepository.save(document);
        }
    }

    @Override
    public void updateDocument(String id, Document document) throws JsonProcessingException, InterruptedException, ExecutionException {
        this.confRepository.updateOne(id, document);
    }

    @Override
    public void upload(Document document) throws IOException, ExecutionException, InterruptedException {
        OutputWriter writer = this.getSettings();
        if (writer.writer == null) {
            writer = document.getServers().iterator().next().getQueries().iterator().next().getOutputWriters().iterator().next();
            this.updateSettings(writer);
        }

        for (Server server : document.getServers()) {
            // Use the default output writer.
            for (Query query : server.getQueries()) {
                query.getOutputWriters().clear();
                query.getOutputWriters().add(writer);
            }

            Document d = new Document();
            List<Server> servers = new ArrayList<>();
            servers.add(server);
            d.setServers(servers);

            this.addDocument(d);
        }
    }

    @Override
    public OutputWriter getSettings() throws IOException {
        OutputWriter writer = this.settingsRepository.get();

        if (writer == null) {
            writer = new OutputWriter();
        }

        return writer;
    }

    @Override
    public void updateSettings(OutputWriter settings) throws IOException, ExecutionException, InterruptedException {
        this.settingsRepository.delete();
        this.settingsRepository.save(settings);
        Map<String, Document> documents = this.confRepository.getAll();

        for (Map.Entry<String, Document> doc : documents.entrySet()) {
            // The number of server should be 1
            for (Server server : doc.getValue().getServers()) {
                for (Query query : server.getQueries()) {
                    query.getOutputWriters().clear();
                    query.getOutputWriters().add(settings);
                }
            }
            this.updateDocument(doc.getKey(), doc.getValue());
        }
    }

    @Override
    public boolean refreshObjectNames(String host, int port, String username, String password) throws JsonProcessingException, InterruptedException, ExecutionException {
        this.objectNameRepository.delete(host, port);

        List<ObjectNameRepresentation> objectnames = JmxUtils.objectNames(host, port, username, password);

        if (objectnames.isEmpty()) {
            return false;
        }

        for (ObjectNameRepresentation obj : objectnames) {
            this.objectNameRepository.save(obj);
        }

        return true;
    }

    @Override
    public Collection<String> prefixNameSuggestion(String host, int port) {
        return this.objectNameRepository.prefixNameSuggestion(host, port);
    }

    @Override
    public Collection<String> prefixAttrSuggestion(String host, int port, String name) {
        return this.objectNameRepository.prefixAttrSuggestion(host, port, name);
    }

}
