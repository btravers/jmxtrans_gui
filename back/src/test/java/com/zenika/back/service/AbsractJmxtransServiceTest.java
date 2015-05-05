package com.zenika.back.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zenika.back.model.*;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
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

    protected abstract void flushChanges();

    /**
     * Load some data in the repository:
     *  - /test/bluefloodWriter.json
     *  - /test/document1.json
     *  - /test/document2.json
     *  - /test/objectnames.json
     */
    @Before
    public abstract void setUp();

    /**
     * If your repository allows transactional actions, you do not need to remove data.
     * Otherwise clean all data in your repository.
     */
    @After
    public abstract void tearDown();

    @Test
    public void shouldFindAllHostsAndPorts() throws InterruptedException, JsonProcessingException {
        Collection<Map<String, Object>> servers = this.jmxtransService.findAllHostsAndPorts();
        Assertions.assertThat(servers.size()).isEqualTo(2);

        Collection<Map<String, Object>> expectedServers = new ArrayList<>();

        Map<String, Object> expectedServer = new HashMap<>();
        expectedServer.put("host", "localhost");
        expectedServer.put("port", 9991);
        expectedServers.add(expectedServer);

        expectedServer = new HashMap<>();
        expectedServer.put("host", "192.168.0.1");
        expectedServer.put("port", 9991);
        expectedServers.add(expectedServer);

        Assertions.assertThat(servers).containsAll(expectedServers);
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
        this.flushChanges();
        Assertions.assertThat(this.jmxtransService.findDocumentByHostAndPort("192.168.0.2", 9991).getId()).isNotNull();
    }

    @Test
    public void shouldUpdateDocument() throws InterruptedException, ExecutionException, IOException {
        Response response = this.jmxtransService.findDocumentByHostAndPort("localhost", 9991);
        Assertions.assertThat(response.getSource().getServers().iterator().next().getUsername()).isNullOrEmpty();

        String newUserName = "tmp";
        response.getSource().getServers().iterator().next().setUsername(newUserName);

        this.jmxtransService.updateDocument(response.getId(), response.getSource());
        this.flushChanges();

        response = this.jmxtransService.findDocumentByHostAndPort("localhost", 9991);
        Assertions.assertThat(response.getSource().getServers().iterator().next().getUsername()).isEqualTo(newUserName);
    }

    @Test
    public void shouldDeleteServer() throws InterruptedException, ExecutionException, IOException {
        Response response = this.jmxtransService.findDocumentByHostAndPort("localhost", 9991);
        Assertions.assertThat(response.getId()).isNotNull();

        this.jmxtransService.deleteDocument("localhost", 9991);
        this.flushChanges();

        response = this.jmxtransService.findDocumentByHostAndPort("localhost", 9991);
        Assertions.assertThat(response.getId()).isNullOrEmpty();

        Collection<String> objectnames = this.jmxtransService.prefixNameSuggestion("localhost", 9991);
        Assertions.assertThat(objectnames).isEmpty();
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
        Response response = this.jmxtransService.findDocumentByHostAndPort("localhost", 9991);

        OutputWriter outputWriter = response.getSource().getServers().iterator().next().getQueries().iterator().next().getOutputWriters().iterator().next();
        Assertions.assertThat(outputWriter.writer).isEqualTo("com.googlecode.jmxtrans.model.output.BluefloodWriter");
        Assertions.assertThat(outputWriter.getSettings().get("host")).isEqualTo("localhost");
        Assertions.assertThat(outputWriter.getSettings().get("port")).isEqualTo(19000);

        InputStream writer = getClass().getResourceAsStream("/test/graphiteWriter.json");

        this.jmxtransService.updateSettings(mapper.readValue(writer, OutputWriter.class));
        this.flushChanges();

        outputWriter = this.jmxtransService.getSettings();

        Assertions.assertThat(outputWriter.writer).isEqualTo("com.googlecode.jmxtrans.model.output.GraphiteWriter");
        Assertions.assertThat(outputWriter.getSettings().get("host")).isEqualTo("localhost");
        Assertions.assertThat(outputWriter.getSettings().get("port")).isEqualTo(2003);

        response = this.jmxtransService.findDocumentByHostAndPort("localhost", 9991);
        outputWriter = response.getSource().getServers().iterator().next().getQueries().iterator().next().getOutputWriters().iterator().next();
        Assertions.assertThat(outputWriter.writer).isEqualTo("com.googlecode.jmxtrans.model.output.GraphiteWriter");
        Assertions.assertThat(outputWriter.getSettings().get("host")).isEqualTo("localhost");
        Assertions.assertThat(outputWriter.getSettings().get("port")).isEqualTo(2003);
    }

    @Test
    public void shouldFindNameSuggestion() {
        List<String> names = new ArrayList<>();
        names.add("java.lang:type=Memory");
        names.add("java.lang:type=Threadingy");
        names.add("Catalina:type=ThreadPool,name=\"http-bio-8501\"");

        Collection<String> objectnames = this.jmxtransService.prefixNameSuggestion("localhost", 9991);

        Assertions.assertThat(objectnames).isNotEmpty();
        Assertions.assertThat(objectnames).containsAll(names);

        objectnames = this.jmxtransService.prefixNameSuggestion("192.168.0.1", 9991);
        Assertions.assertThat(objectnames).isEmpty();
    }

    @Test
    public void  shouldFindAttrSuggestion() {
        List<String> attributes = new ArrayList<>();
        attributes.add("HeapMemoryUsage");
        attributes.add("NonHeapMemoryUsage");

        Collection<String> attr = this.jmxtransService.prefixAttrSuggestion("localhost", 9991, "java.lang:type=Memory");

        Assertions.assertThat(attr).isNotEmpty();
        Assertions.assertThat(attr).containsAll(attributes);

        attr = this.jmxtransService.prefixAttrSuggestion("localhost", 9991, "yolo");
        Assertions.assertThat(attr).isEmpty();
    }

    @Test
    public void shouldUploadDocument() throws IOException, ExecutionException, InterruptedException {
        InputStream writer = getClass().getResourceAsStream("/test/graphiteWriter.json");

        this.jmxtransService.updateSettings(mapper.readValue(writer, OutputWriter.class));
        this.flushChanges();

        InputStream inputStream = getClass().getResourceAsStream("/test/multiServersDocument.json");

        this.jmxtransService.upload(mapper.readValue(inputStream, Document.class));
        this.flushChanges();

        Collection<Map<String, Object>> servers = this.jmxtransService.findAllHostsAndPorts();
        Assertions.assertThat(servers.size()).isEqualTo(4);

        Response response = this.jmxtransService.findDocumentByHostAndPort("192.168.0.2", 9991);
        Assertions.assertThat(response.getId()).isNotNull();
        Assertions.assertThat(response.getSource().getServers().iterator().next().getQueries().iterator().next().getOutputWriters().iterator().next().writer).isEqualTo("com.googlecode.jmxtrans.model.output.GraphiteWriter");

        response = this.jmxtransService.findDocumentByHostAndPort("192.168.0.3", 9991);
        Assertions.assertThat(response.getId()).isNotNull();
        Assertions.assertThat(response.getSource().getServers().iterator().next().getQueries().iterator().next().getOutputWriters().iterator().next().writer).isEqualTo("com.googlecode.jmxtrans.model.output.GraphiteWriter");
    }

}
