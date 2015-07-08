package com.zenika.jmxtrans.gui.web;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.management.MalformedObjectNameException;
import javax.validation.Valid;

import com.zenika.jmxtrans.gui.exception.JmxtransException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zenika.jmxtrans.gui.model.Document;
import com.zenika.jmxtrans.gui.model.OutputWriter;
import com.zenika.jmxtrans.gui.model.Response;
import com.zenika.jmxtrans.gui.service.JmxtransService;

@Controller
public class JmxtransController {

    private static final Logger logger = LoggerFactory.getLogger(JmxtransController.class);

    private JmxtransService jmxtransService;
    private ObjectMapper mapper;

    @Autowired
    public void setJmxtransService(JmxtransService jmxtransService) {
        this.jmxtransService = jmxtransService;
    }

    @Autowired
    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @RequestMapping("/")
    public String index() {
        return "index.html";
    }

    @RequestMapping(value = "/server/all", method = RequestMethod.GET)
    @ResponseBody
    public Collection<Map<String, Object>> listHosts() throws JmxtransException {
        logger.info("GET request: /server/all");
        return this.jmxtransService.findAllHostsAndPorts();
    }

    @RequestMapping(value = "/server", method = RequestMethod.GET)
    @ResponseBody
    public Response showServer(
            @RequestParam(value = "host", required = true) String host,
            @RequestParam(value = "port", required = true) int port) throws JmxtransException {
        logger.info("GET request: /server");
        try {
            return this.jmxtransService.findDocumentByHostAndPort(host, port);
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
            throw new JmxtransException(e.getMessage());
        } catch (ExecutionException e) {
            logger.error(e.getMessage());
            throw new JmxtransException(e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new JmxtransException(e.getMessage());
        }
    }

    @RequestMapping(value = "/server/_download", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> downloadServer(
            @RequestParam(value = "host", required = true) String host,
            @RequestParam(value = "port", required = true) int port)
            throws JmxtransException {
        logger.info("GET request: /server/_download");
        try {
            Response response = this.jmxtransService.findDocumentByHostAndPort(host,
                    port);

            byte[] content = this.mapper
                    .writeValueAsBytes(response.getSource());

            HttpHeaders respHeaders = new HttpHeaders();
            respHeaders.setContentType(MediaType.APPLICATION_JSON);
            respHeaders.setContentLength(content.length);
            respHeaders.setContentDispositionFormData("attachment", "server_"
                    + host + ":" + port + ".json");

            return new ResponseEntity<InputStreamResource>(
                    new InputStreamResource(new ByteArrayInputStream(content)),
                    respHeaders, HttpStatus.OK);
        } catch (JsonParseException e) {
            logger.error(e.getMessage());
            throw new JmxtransException(e.getMessage());
        } catch (JsonMappingException e) {
            logger.error(e.getMessage());
            throw new JmxtransException(e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new JmxtransException(e.getMessage());
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
            throw new JmxtransException(e.getMessage());
        } catch (ExecutionException e) {
            logger.error(e.getMessage());
            throw new JmxtransException(e.getMessage());
        }
    }

    @RequestMapping(value = "/server", method = RequestMethod.DELETE)
    @ResponseBody
    public void deleteServer(
            @RequestParam(value = "host", required = true) String host,
            @RequestParam(value = "port", required = true) int port) {
        logger.info("DELETE request: /server");
        this.jmxtransService.deleteDocument(host, port);
    }

    @RequestMapping(value = "/server", method = RequestMethod.POST)
    @ResponseBody
    public void addServer(@Valid @RequestBody Document server) throws JmxtransException {
        logger.info("POST request: /server");
        try {
            this.jmxtransService.addDocument(server);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
            throw new JmxtransException(e.getMessage());
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
            throw new JmxtransException(e.getMessage());
        } catch (ExecutionException e) {
            logger.error(e.getMessage());
            throw new JmxtransException(e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new JmxtransException(e.getMessage());
        }
    }

    @RequestMapping(value = "/server/_update", method = RequestMethod.POST)
    @ResponseBody
    public void updateServer(
            @RequestParam(value = "id", required = true) String id,
            @Valid @RequestBody Document server) throws JmxtransException {
        logger.info("POST request: /server/_update");
        try {
            this.jmxtransService.updateDocument(id, server);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
            throw new JmxtransException(e.getMessage());
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
            throw new JmxtransException(e.getMessage());
        } catch (ExecutionException e) {
            logger.error(e.getMessage());
            throw new JmxtransException(e.getMessage());
        }
    }

    @RequestMapping(value = "/settings", method = RequestMethod.POST)
    @ResponseBody
    public void updateSettings(@Valid @RequestBody OutputWriter settings) throws JmxtransException {
        try {
            logger.info("POST request: /settings");
            this.jmxtransService.updateSettings(settings);
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new JmxtransException(e.getMessage());
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
            throw new JmxtransException(e.getMessage());
        } catch (ExecutionException e) {
            logger.error(e.getMessage());
            throw new JmxtransException(e.getMessage());
        }
    }

    @RequestMapping(value = "/settings", method = RequestMethod.GET)
    @ResponseBody
    public OutputWriter getSettings() throws JmxtransException {
        logger.info("GET request: /settings");
        try {
            return this.jmxtransService.getSettings();
        } catch (JsonParseException e) {
            logger.error(e.getMessage());
            throw new JmxtransException(e.getMessage());
        } catch (JsonMappingException e) {
            logger.error(e.getMessage());
            throw new JmxtransException(e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new JmxtransException(e.getMessage());
        }
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public void upload(@RequestParam("file") MultipartFile file) throws JmxtransException {
        logger.info("POST request: /upload");
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                String confFile = new String(bytes);

                Document doc = mapper.readValue(confFile, Document.class);

                this.jmxtransService.upload(doc);
            } catch (IOException e) {
                logger.error(e.getMessage());
                throw new JmxtransException(e.getMessage());
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
                throw new JmxtransException(e.getMessage());
            } catch (ExecutionException e) {
                logger.error(e.getMessage());
                throw new JmxtransException(e.getMessage());
            }
        }
    }

    @RequestMapping(value = "/autocomplete/load", method = RequestMethod.GET)
    @ResponseBody
    public void loadObjectNames(
            @RequestParam(value = "host", required = true) String host,
            @RequestParam(value = "port", required = true) int port,
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "password", required = false) String password) throws JmxtransException {
        logger.info("GET request: /autocomplete/load");
        try {
            this.jmxtransService.refreshObjectNames(host, port, username, password);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
            throw new JmxtransException(e.getMessage());
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
            throw new JmxtransException(e.getMessage());
        } catch (ExecutionException e) {
            logger.error(e.getMessage());
            throw new JmxtransException(e.getMessage());
        }
    }

    @RequestMapping(value = "/autocomplete/name", method = RequestMethod.GET)
    @ResponseBody
    public Collection<String> prefixNameSuggestion(
            @RequestParam(value = "host", required = true) String host,
            @RequestParam(value = "port", required = true) int port) {
        logger.info("GET request: /autocomplete/name");
        return this.jmxtransService.objectNames(host, port);
    }

    @RequestMapping(value = "/autocomplete/attr", method = RequestMethod.GET)
    @ResponseBody
    public Collection<String> prefixAttrSuggestion(
            @RequestParam(value = "host", required = true) String host,
            @RequestParam(value = "port", required = true) int port,
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "objectname", required = true) String objectname) throws JmxtransException {
        logger.info("GET request: /autocomplete/attr");
        try {
            logger.info("GET request: /autocomplete/attr");
            return this.jmxtransService.attributes(host, port, username, password, objectname);
        } catch (MalformedObjectNameException e) {
            logger.error(e.getMessage());
            throw new JmxtransException(e.getMessage());
        }
    }

}
