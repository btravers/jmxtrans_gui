package com.zenika.back.web;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.validation.Valid;

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
import com.zenika.back.exception.JmxtransException;
import com.zenika.back.model.Document;
import com.zenika.back.model.OutputWriter;
import com.zenika.back.model.Query;
import com.zenika.back.model.Response;
import com.zenika.back.model.Server;
import com.zenika.back.service.JmxtransService;

@Controller
public class JmxtransController {

    private static final Logger logger = LoggerFactory
	    .getLogger(JmxtransController.class);

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

    @RequestMapping(value = "/server/all", method = RequestMethod.GET)
    @ResponseBody
    public Collection<Map<String, String>> listHosts() throws JmxtransException {
	try {
	    return this.jmxtransService.findHosts();
	} catch (JsonProcessingException e) {
	    logger.error(e.getMessage());
	    throw new JmxtransException(e.getMessage());
	} catch (IOException e) {
	    logger.error(e.getMessage());
	    throw new JmxtransException(e.getMessage());
	}
    }

    @RequestMapping(value = "/server", method = RequestMethod.GET)
    @ResponseBody
    public Response showServer(
	    @RequestParam(value = "host", required = true) String host,
	    @RequestParam(value = "port", required = true) int port)
	    throws JmxtransException {
	try {
	    return this.jmxtransService.findServersByHost(host, port);
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

    @RequestMapping(value = "/server/_download", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> downloadServer(
	    @RequestParam(value = "host", required = true) String host,
	    @RequestParam(value = "port", required = true) int port)
	    throws JmxtransException {

	try {
	    Response response = this.jmxtransService.findServersByHost(host,
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
	this.jmxtransService.deleteServer(host, port);
    }

    @RequestMapping(value = "/server", method = RequestMethod.POST)
    @ResponseBody
    public void addServer(@Valid @RequestBody Document server) throws JmxtransException {
	try {
	    this.jmxtransService.addServer(server);
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
	try {
	    this.jmxtransService.updateServer(id, server);
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
	    this.jmxtransService.updateSettings(settings);
	} catch (IOException e) {
	    logger.error(e.getMessage());
	    throw new JmxtransException(e.getMessage());
	}
    }

    @RequestMapping(value = "/settings", method = RequestMethod.GET)
    @ResponseBody
    public OutputWriter getSettings() throws JmxtransException {
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
	if (!file.isEmpty()) {
	    try {
		byte[] bytes = file.getBytes();
		String confFile = new String(bytes);

		ObjectMapper mapper = new ObjectMapper();
		Document doc = mapper.readValue(confFile, Document.class);

		OutputWriter writer = this.getSettings();

		for (Server server : doc.getServers()) {
		    // Use the default output writer.
		    for (Query query : server.getQueries()) {
			query.getOutputWriters().clear();
			query.getOutputWriters().add(writer);
		    }

		    Document d = new Document();
		    List<Server> servers = new ArrayList<Server>();
		    servers.add(server);
		    d.setServers(servers);

		    this.jmxtransService.addServer(d);
		}
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

    @RequestMapping(value = "/refresh", method = RequestMethod.GET)
    @ResponseBody
    public void refresh(
	    @RequestParam(value = "host", required = true) String host,
	    @RequestParam(value = "port", required = true) int port) throws JmxtransException {
	try {
	    this.jmxtransService.refresh(host, port);
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

    @RequestMapping(value = "/suggest_name", method = RequestMethod.GET)
    @ResponseBody
    public Collection<String> prefixNameSuggestion(
	    @RequestParam(value = "host", required = true) String host,
	    @RequestParam(value = "port", required = true) int port) {
	return this.jmxtransService.prefixNameSuggestion(host, port);
    }

    @RequestMapping(value = "/suggest_attr", method = RequestMethod.GET)
    @ResponseBody
    public Collection<String> prefixAttrSuggestion(
	    @RequestParam(value = "host", required = true) String host,
	    @RequestParam(value = "port", required = true) int port,
	    @RequestParam(value = "name", required = true) String name) {
	return this.jmxtransService.prefixAttrSuggestion(host, port, name);
    }

}
