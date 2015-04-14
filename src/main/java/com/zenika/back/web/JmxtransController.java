package com.zenika.back.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zenika.back.model.Document;
import com.zenika.back.model.OutputWriter;
import com.zenika.back.model.Query;
import com.zenika.back.model.Response;
import com.zenika.back.model.Server;
import com.zenika.back.service.JmxtransService;

@RestController
public class JmxtransController {

    private JmxtransService jmxtransService;

    @Autowired
    public void setJmxtransService(JmxtransService jmxtransService) {
	this.jmxtransService = jmxtransService;
    }

    @RequestMapping(value = "/server/all", method = RequestMethod.GET)
    public Collection<String> listHosts() {
	try {
	    return this.jmxtransService.findHosts();
	} catch (JsonProcessingException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return null;
    }

    @RequestMapping(value = "/server", method = RequestMethod.GET)
    public Response showServer(
	    @RequestParam(value = "host", required = true) String host) {
	try {
	    return this.jmxtransService.findServersByHost(host);
	} catch (JsonParseException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (JsonMappingException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (InterruptedException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (ExecutionException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return null;
    }

    @RequestMapping(value = "/server", method = RequestMethod.DELETE)
    public void deleteServer(
	    @RequestParam(value = "host", required = true) String host) {
	this.jmxtransService.deleteServer(host);
    }

    @RequestMapping(value = "/server", method = RequestMethod.POST)
    public void addServer(@RequestBody Document server) {
	try {
	    this.jmxtransService.addServer(server);
	} catch (JsonProcessingException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (InterruptedException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (ExecutionException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    @RequestMapping(value = "/server/_update", method = RequestMethod.POST)
    public void updateServer(
	    @RequestParam(value = "id", required = true) String id,
	    @RequestBody Document server) {
	try {
	    this.jmxtransService.updateServer(id, server);
	} catch (JsonProcessingException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (InterruptedException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (ExecutionException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	;
    }

    @RequestMapping(value = "/settings", method = RequestMethod.POST)
    public void updateSettings(@RequestBody OutputWriter settings) {
	try {
	    this.jmxtransService.updateSettings(settings);
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    @RequestMapping(value = "/settings", method = RequestMethod.GET)
    public OutputWriter getSettings() {
	try {
	    return this.jmxtransService.getSettings();
	} catch (JsonParseException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (JsonMappingException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return null;
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public void upload(@RequestParam("file") MultipartFile file) {
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
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (ExecutionException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
    }

    @RequestMapping(value = "/refresh", method = RequestMethod.GET)
    public void refresh(
	    @RequestParam(value = "host", required = true) String host,
	    @RequestParam(value = "port", required = true) int port) {
	try {
	    this.jmxtransService.refresh(host, port);
	} catch (JsonProcessingException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (InterruptedException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (ExecutionException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    @RequestMapping(value = "/suggest_name", method = RequestMethod.GET)
    public Collection<String> prefixNameSuggestion(
	    @RequestParam(value = "host", required = true) String host,
	    @RequestParam(value = "prefix", required = true) String prefix) {
	return this.jmxtransService.prefixNameSuggestion(host, prefix);
    }

    @RequestMapping(value = "/suggest_attr", method = RequestMethod.GET)
    public Collection<String> prefixAttrSuggestion(
	    @RequestParam(value = "host", required = true) String host,
	    @RequestParam(value = "name", required = true) String name,
	    @RequestParam(value = "prefix", required = true) String prefix) {
	return this.jmxtransService.prefixAttrSuggestion(host, name, prefix);
    }

}
