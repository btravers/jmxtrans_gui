package com.zenika.back.repository;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zenika.back.AppConfig;
import com.zenika.back.model.Document;
import com.zenika.back.model.ObjectNameRepresentation;
import com.zenika.back.model.OutputWriter;
import com.zenika.back.model.Query;
import com.zenika.back.model.Response;
import com.zenika.back.model.Server;

@Repository
public class ServerRepositoryCustomImpl implements ServerRepositoryCustom {

    private static final Logger logger = LoggerFactory
	    .getLogger(ServerRepositoryCustomImpl.class);

    private Client client;
    private ObjectMapper mapper;

    @Autowired
    public void setClient(Client client) {
	this.client = client;
    }

    @Autowired
    public void setMapper(ObjectMapper mapper) {
	this.mapper = mapper;
    }

    @Override
    public void deleteOne(String id) {
	this.client.prepareDelete(AppConfig.INDEX, AppConfig.CONF_TYPE, id)
		.execute().actionGet();
    }

    @Override
    public void delete(String host, int port) {
	this.client
		.prepareDeleteByQuery(AppConfig.INDEX)
		.setTypes(AppConfig.CONF_TYPE)
		.setQuery(
			QueryBuilders
				.boolQuery()
				.must(QueryBuilders.termQuery("servers.host",
					host))
				.must(QueryBuilders.termQuery("servers.port",
					port))).execute().actionGet();

	this.client.prepareDeleteByQuery(AppConfig.INDEX)
		.setTypes(AppConfig.OBJECTNAME_TYPE)
		.setQuery(QueryBuilders.termQuery("host", host)).execute()
		.actionGet();
    }

    @Override
    public Collection<Map<String, String>> findAllHost()
	    throws JsonParseException, JsonMappingException, IOException {
	SearchResponse response = this.client.prepareSearch(AppConfig.INDEX)
		.setTypes(AppConfig.CONF_TYPE)
		//.addFields("host", "port")
		.setQuery(QueryBuilders.matchAllQuery())
		.setSize(Integer.MAX_VALUE).execute().actionGet();

	List<Map<String, String>> hosts = new ArrayList<Map<String, String>>();
	for (SearchHit hit : response.getHits().getHits()) {
	    Document doc = mapper.readValue(hit.getSourceAsString(),
		    Document.class);

	    // Actually, there is only one server per document
	    for (Server server : doc.getServers()) {
		Map<String, String> res = new HashMap<String, String>();
		res.put("host", server.getHost());
		res.put("port", String.valueOf(server.getPort()));
		hosts.add(res);
	    }
	}

	return hosts;
    }

    @Override
    public Response getByHost(String host, int port) throws JsonParseException,
	    JsonMappingException, IOException, InterruptedException,
	    ExecutionException {
	SearchResponse searchResponse = this.client
		.prepareSearch(AppConfig.INDEX)
		.setTypes(AppConfig.CONF_TYPE)
		.setQuery(QueryBuilders.matchAllQuery())
		.setPostFilter(
			FilterBuilders
				.boolFilter()
				.must(FilterBuilders.termFilter("servers.host",
					host))
				.must(FilterBuilders.termFilter("servers.port",
					port))).execute().actionGet();

	// When the method is called, the number of hits is always equal to 1.
	if (searchResponse.getHits().getHits().length > 0) {
	    Response response = new Response();
	    Document document = null;
	    List<String> toDelete = new ArrayList<String>();

	    for (SearchHit hit : searchResponse.getHits().getHits()) {
		if (document == null) {
		    response.setId(hit.getId());
		    document = mapper.readValue(hit.getSourceAsString(),
			    Document.class);
		} else {
		    Document doc = mapper.readValue(hit.getSourceAsString(),
			    Document.class);

		    document.getServers()
			    .iterator()
			    .next()
			    .getQueries()
			    .addAll(doc.getServers().iterator().next()
				    .getQueries());

		    toDelete.add(hit.getId());
		}

	    }

	    this.updateOne(response.getId(), document);

	    for (String id : toDelete) {
		this.deleteOne(id);
	    }

	    response.setSource(document);
	    return response;

	    // No document for the given server
	} else {
	    return new Response();
	}
    }

    @Override
    public void save(Document server) throws InterruptedException,
	    ExecutionException, IOException {

	Server s = server.getServers().iterator().next();
	Response response = this.getByHost(s.getHost(), s.getPort());

	if (response.getSource() != null) {
	    response.getSource().getServers().iterator().next().getQueries()
		    .addAll(server.getServers().iterator().next().getQueries());

	    this.updateOne(response.getId(), response.getSource());
	} else {
	    String json = mapper.writeValueAsString(server);

	    IndexRequest indexRequest = new IndexRequest();
	    indexRequest.index(AppConfig.INDEX);
	    indexRequest.type(AppConfig.CONF_TYPE);
	    indexRequest.source(json);

	    this.client.index(indexRequest).get();
	}
    }

    @Override
    public void updateOne(String id, Document server)
	    throws JsonProcessingException, InterruptedException,
	    ExecutionException {
	String json = mapper.writeValueAsString(server);

	this.client.prepareUpdate(AppConfig.INDEX, AppConfig.CONF_TYPE, id)
		.setDoc(json).execute().actionGet();
    }

    @Override
    public OutputWriter settings() throws JsonParseException,
	    JsonMappingException, IOException {
	GetResponse getResponse = this.client
		.prepareGet(AppConfig.INDEX, AppConfig.SETTINGS_TYPE,
			AppConfig.SETTINGS_ID).execute().actionGet();

	if (getResponse.isExists()) {
	    return mapper.readValue(getResponse.getSourceAsString(),
		    OutputWriter.class);
	} else {
	    OutputWriter settings = new OutputWriter();

	    this.client
		    .prepareIndex(AppConfig.INDEX, AppConfig.SETTINGS_TYPE,
			    AppConfig.SETTINGS_ID).setSource(mapper.writeValueAsString(settings))
		    .execute().actionGet();

	    return settings;
	}
    }

    @Override
    public void saveSettings(OutputWriter settings) throws IOException {
	String json = mapper.writeValueAsString(settings);

	UpdateRequest updateRequest = new UpdateRequest();
	updateRequest.index(AppConfig.INDEX);
	updateRequest.type(AppConfig.SETTINGS_TYPE);
	updateRequest.id(AppConfig.SETTINGS_ID);
	updateRequest.doc(json);

	this.client.update(updateRequest);

	SearchResponse response = this.client.prepareSearch(AppConfig.INDEX)
		.setTypes(AppConfig.CONF_TYPE)
		.setQuery(QueryBuilders.matchAllQuery()).execute().actionGet();

	for (SearchHit hit : response.getHits().getHits()) {
	    Document doc = mapper.readValue(hit.getSourceAsString(),
		    Document.class);

	    for (Server server : doc.getServers()) {
		for (Query query : server.getQueries()) {
		    query.getOutputWriters().clear();
		    query.getOutputWriters().add(settings);
		}
	    }

	    String updatedServer = mapper.writeValueAsString(doc);

	    UpdateRequest request = new UpdateRequest();
	    request.index(AppConfig.INDEX);
	    request.type(AppConfig.CONF_TYPE);
	    request.id(hit.getId());
	    request.doc(updatedServer);

	    this.client.update(request);
	}

    }

    @Override
    public void refresh(String host, int port) throws JsonProcessingException,
	    InterruptedException, ExecutionException {
	client.prepareDeleteByQuery(AppConfig.INDEX)
		.setTypes(AppConfig.OBJECTNAME_TYPE)
		.setQuery(QueryBuilders.termQuery("host", host)).execute()
		.actionGet();

	List<ObjectNameRepresentation> objectnames = this.objectNames(host,
		port);

	for (ObjectNameRepresentation obj : objectnames) {
	    String json = mapper.writeValueAsString(obj);

	    this.client
		    .prepareIndex(AppConfig.INDEX, AppConfig.OBJECTNAME_TYPE)
		    .setSource(json).execute().actionGet();
	}

    }

    private List<ObjectNameRepresentation> objectNames(String host, int port) {
	String url = "service:jmx:rmi:///jndi/rmi://" + host + ":" + port
		+ "/jmxrmi";
	JMXServiceURL serviceURL = null;
	JMXConnector jmxConnector = null;

	try {
	    serviceURL = new JMXServiceURL(url);
	    jmxConnector = JMXConnectorFactory.connect(serviceURL);
	    MBeanServerConnection mbeanConn = jmxConnector
		    .getMBeanServerConnection();

	    List<ObjectNameRepresentation> result = new ArrayList<ObjectNameRepresentation>();

	    Set<ObjectName> beanSet = mbeanConn.queryNames(null, null);
	    for (ObjectName name : beanSet) {
		ObjectNameRepresentation tmp = new ObjectNameRepresentation();
		tmp.setHost(host);
		tmp.setPort(port);
		tmp.setName(name.toString());

		List<String> attributes = new ArrayList<String>();
		for (MBeanAttributeInfo attr : mbeanConn.getMBeanInfo(name)
			.getAttributes()) {
		    attributes.add(attr.getName());
		}
		tmp.setAttributes(attributes);
		result.add(tmp);
	    }

	    return result;
	} catch (MalformedURLException e) {
	    logger.error(e.getMessage());
	} catch (IOException e) {
	    logger.error(e.getMessage());
	} catch (InstanceNotFoundException e) {
	    logger.error(e.getMessage());
	} catch (IntrospectionException e) {
	    logger.error(e.getMessage());
	} catch (ReflectionException e) {
	    logger.error(e.getMessage());
	} finally {
	    if (jmxConnector != null) {
		try {
		    jmxConnector.close();
		} catch (IOException e) {
		    logger.error(e.getMessage());
		}
	    }
	}

	return new ArrayList<ObjectNameRepresentation>();
    }

    @Override
    public Collection<String> prefixNameSuggestion(String host, int port) {
	SearchResponse response = this.client
		.prepareSearch(AppConfig.INDEX)
		.setTypes(AppConfig.OBJECTNAME_TYPE)
		.setQuery(
			QueryBuilders.boolQuery()
				.must(QueryBuilders.termQuery("host", host))
				.must(QueryBuilders.termQuery("port", port)))
		.addAggregation(
			AggregationBuilders.terms("names").field("name")
				.order(Terms.Order.term(true)).size(0))
		.execute().actionGet();

	Collection<String> result = new ArrayList<String>();

	Terms agg = response.getAggregations().get("names");
	for (Bucket bucket : agg.getBuckets()) {
	    result.add(bucket.getKey());
	}

	return result;
    }

    @Override
    public Collection<String> prefixAttrSuggestion(String host, int port,
	    String name) {
	SearchResponse response = this.client
		.prepareSearch(AppConfig.INDEX)
		.setTypes(AppConfig.OBJECTNAME_TYPE)
		.addFields("attributes")
		.setQuery(
			QueryBuilders.boolQuery()
				.must(QueryBuilders.termQuery("host", host))
				.must(QueryBuilders.termQuery("port", port))
				.must(QueryBuilders.termQuery("name", name)))
		.execute().actionGet();

	Collection<String> result = new ArrayList<String>();

	for (SearchHit hit : response.getHits().getHits()) {
	    if (hit.field("attributes") != null
		    && hit.field("attributes").getValues() != null) {
		for (Object value : hit.field("attributes").getValues())
		    result.add(value.toString());
	    }
	}

	return result;
    }
}
