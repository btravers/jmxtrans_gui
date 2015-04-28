package com.zenika.back.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zenika.back.model.*;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public abstract class AbsractJmxtransServiceTest {

    protected JmxtransService jmxtransService;
    protected ObjectMapper mapper;

    @Autowired
    public void setJmxtransService(JmxtransService jmxtransService) {
        this.jmxtransService = jmxtransService;
    }

    @Autowired
    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    protected abstract void forceUpdate();

    @Test
    public void shouldFindAllHostsAndPorts() throws InterruptedException, JsonProcessingException {
        Collection<Map<String, String>> servers = this.jmxtransService.findAllHostsAndPorts();
        Assertions.assertThat(servers.size()).isEqualTo(2);
    }

    @Test
    public void shouldFindDocumentByHostAndPort() throws InterruptedException, ExecutionException, IOException {
        Response response = this.jmxtransService.findDocumentByHostAndPort("localhost", 9991);
        Assertions.assertThat(response.getId()).isNotNull();

        response = this.jmxtransService.findDocumentByHostAndPort("127.0.0.1", 9991);
        Assertions.assertThat(response.getId()).isNullOrEmpty();
    }

    @Test
    public void shouldAddDocument() throws InterruptedException, ExecutionException, IOException {
        InputStream document = getClass().getResourceAsStream("/test/document3.json");

        this.jmxtransService.addDocument(mapper.readValue(document, Document.class));
        this.forceUpdate();
        Assertions.assertThat(this.jmxtransService.findDocumentByHostAndPort("192.168.0.2", 9991).getId()).isNotNull();
    }

    @Test
    public void shouldUpdateDocument() throws InterruptedException, ExecutionException, IOException {
        Response response = this.jmxtransService.findDocumentByHostAndPort("localhost", 9991);
        Assertions.assertThat(response.getSource().getServers().iterator().next().getUsername()).isNullOrEmpty();

        String newUserName = "tmp";
        response.getSource().getServers().iterator().next().setUsername(newUserName);

        this.jmxtransService.updateDocument(response.getId(), response.getSource());
        this.forceUpdate();
        response = this.jmxtransService.findDocumentByHostAndPort("localhost", 9991);
        Assertions.assertThat(response.getSource().getServers().iterator().next().getUsername()).isEqualTo(newUserName);
    }

    @Test
    public void shouldDeleteServer() throws InterruptedException, ExecutionException, IOException {
        Response response = this.jmxtransService.findDocumentByHostAndPort("localhost", 9991);
        Assertions.assertThat(response.getId()).isNotNull();

        this.jmxtransService.deleteDocument("localhost", 9991);
        this.forceUpdate();
        response = this.jmxtransService.findDocumentByHostAndPort("localhost", 9991);
        Assertions.assertThat(response.getId()).isNullOrEmpty();
    }

    @Test
    public void shouldGetSettings() throws IOException {
        OutputWriter outputWriter = this.jmxtransService.getSettings();

        Assertions.assertThat(outputWriter.writer).isEqualTo("com.googlecode.jmxtrans.model.output.BluefloodWriter");
        Assertions.assertThat(outputWriter.getSettings().get("host")).isEqualTo("localhost");
        Assertions.assertThat(outputWriter.getSettings().get("port")).isEqualTo(19000);
    }

    @Test
    public void shouldUpdateSettings() throws InterruptedException, ExecutionException, IOException {
        InputStream writer = getClass().getResourceAsStream("/test/graphiteWriter.json");

        this.jmxtransService.updateSettings(mapper.readValue(writer, OutputWriter.class));
        this.forceUpdate();

        OutputWriter outputWriter = this.jmxtransService.getSettings();

        Assertions.assertThat(outputWriter.writer).isEqualTo("com.googlecode.jmxtrans.model.output.GraphiteWriter");
        Assertions.assertThat(outputWriter.getSettings().get("host")).isEqualTo("localhost");
        Assertions.assertThat(outputWriter.getSettings().get("port")).isEqualTo(2003);
    }


// TODO test the following methods
//
//    void upload(Document document) throws IOException, ExecutionException, InterruptedException;
//
//    void refreshObjectNames(String host, int port) throws JsonProcessingException, InterruptedException, ExecutionException;
//
//    Collection<String> prefixAttrSuggestion(String host, int port, String name);
//
//    Collection<String> prefixNameSuggestion(String host, int port);

}
