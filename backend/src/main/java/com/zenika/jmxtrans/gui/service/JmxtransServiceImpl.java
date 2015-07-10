package com.zenika.jmxtrans.gui.service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

import com.zenika.jmxtrans.gui.model.*;
import com.zenika.jmxtrans.gui.repository.MBeanInformation;
import com.zenika.jmxtrans.gui.repository.SettingsRepository;
import com.zenika.jmxtrans.gui.utils.JmxUtils;
import com.zenika.jmxtrans.gui.repository.ConfRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import javax.management.MalformedObjectNameException;

@Service
public class JmxtransServiceImpl implements JmxtransService {

    private static final Logger logger = LoggerFactory.getLogger(JmxtransServiceImpl.class);

    private ConfRepository confRepository;
    private SettingsRepository settingsRepository;
    private MBeanInformation MBeanInformation;

    @Autowired
    public void setConfRepository(ConfRepository confRepository) {
        this.confRepository = confRepository;
    }

    @Autowired
    public void setSettingsRepository(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    @Autowired
    public void setMBeanInformation(MBeanInformation MBeanInformation) {
        this.MBeanInformation = MBeanInformation;
    }

    @Override
    public Collection<Map<String, Object>> findAllHostsAndPorts() {
        return this.confRepository.findAllHostsAndPorts();
    }

    @Override
    public Response findDocumentByHostAndPort(String host, int port) throws InterruptedException, ExecutionException, IOException {
        Response response = this.confRepository.get(host, port);

        if (response.getId() != null) {
            Server server = response.getSource().getServers().iterator().next();
            try {
                this.refreshObjectNames(host, port, server.getUsername(), server.getPassword());
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }

        return response;
    }

    @Override
    public void deleteDocument(String host, int port) {
        this.confRepository.delete(host, port);
        this.MBeanInformation.delete(host, port);
    }

    @Override
    public void addDocument(Document document) throws InterruptedException, ExecutionException, IOException {
        Server server = document.getServers().iterator().next();
        Response response = this.confRepository.get(server.getHost(), server.getPort());

        if (response.getId() != null) {
            this.updateDocument(response.getId(), document);
        } else {
            this.confRepository.save(document);
            try {
                this.refreshObjectNames(server.getHost(), server.getPort(), server.getUsername(), server.getPassword());
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    @Override
    public void updateDocument(String id, Document document) throws JsonProcessingException, InterruptedException, ExecutionException {
        this.confRepository.updateOne(id, document);

        Server server = document.getServers().iterator().next();
        try {
            this.refreshObjectNames(server.getHost(), server.getPort(), server.getUsername(), server.getPassword());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
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
        List<ObjectNameRepresentation> objectnames = JmxUtils.objectNames(host, port, username, password);

        if (objectnames == null) {
            return false;
        }

        this.MBeanInformation.delete(host, port);
        this.MBeanInformation.save(objectnames);

        return true;
    }

    @Override
    public Collection<String> objectNames(String host, int port, String obj) {
        return this.MBeanInformation.getObjectNames(host, port, obj);
    }

    @Override
    public Collection<String> attributes(String host, int port, String username, String password, String objectname) throws MalformedObjectNameException {
        Collection<String> objectnames = this.MBeanInformation.getObjectName(objectname);

        Set<String> attributes = new HashSet<>();
        for (String obj : objectnames) {
            attributes.addAll(JmxUtils.attributes(host, port, username, password, obj));
        }
        return attributes;
    }

    @Override
    public boolean existJMXAgent(String host, int port, String username, String password) {
        return JmxUtils.existJMXAgent(host, port, username, password);
    }
}
