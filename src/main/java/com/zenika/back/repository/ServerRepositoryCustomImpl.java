package com.zenika.back.repository;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.zenika.back.model.Document;
import com.zenika.back.model.ObjectNameRepresentation;
import com.zenika.back.model.OutputWriter;
import com.zenika.back.model.Query;
import com.zenika.back.model.Response;
import com.zenika.back.model.Server;

@Repository
public class ServerRepositoryCustomImpl implements ServerRepositoryCustom {

	private static final String INDEX = ".jmxtrans";
	private static final String CONF_TYPE = "conf";
	private static final String OBJECTNAME_TYPE = "objectname";
	private static final String SETTINGS_TYPE = "settings";
	private static final String SETTINGS_ID = "writer";

	private Client client;

	@Autowired
	public void setClient(Client client) {
		this.client = client;
	}

	@Override
	public void deleteOne(String id) {
		this.client.prepareDelete(INDEX, CONF_TYPE, id).execute().actionGet();
	}

	@Override
	public void delete(String host) {
		this.client.prepareDeleteByQuery(INDEX).setTypes(CONF_TYPE)
				.setQuery(QueryBuilders.termQuery("host", host)).execute()
				.actionGet();
	}

	@Override
	public Collection<String> findAllHost() throws JsonProcessingException {
		String aggregatorTerm = "hosts";

		SearchResponse response = this.client
				.prepareSearch(INDEX)
				.setTypes(CONF_TYPE)
				.setQuery(QueryBuilders.matchAllQuery())
				.addAggregation(
						AggregationBuilders.terms(aggregatorTerm).field("host"))
				.execute().actionGet();

		Terms agg = response.getAggregations().get(aggregatorTerm);

		List<String> hosts = new ArrayList<String>();
		for (Bucket b : agg.getBuckets()) {
			hosts.add(b.getKey());
		}

		return hosts;
	}

	@Override
	public Response getByHost(String host) throws JsonParseException,
			JsonMappingException, IOException, InterruptedException,
			ExecutionException {
		SearchResponse searchResponse = this.client
				.prepareSearch(INDEX)
				.setTypes(CONF_TYPE)
				.setQuery(QueryBuilders.matchAllQuery())
				.setPostFilter(
						FilterBuilders.boolFilter().must(
								FilterBuilders.termFilter("host", host)))
				.execute().actionGet();

		ObjectMapper mapper = new ObjectMapper();

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
		Response response = this.getByHost(server.getServers().iterator()
				.next().getHost());

		if (response.getSource() != null) {
			response.getSource().getServers().iterator().next().getQueries()
					.addAll(server.getServers().iterator().next().getQueries());

			this.updateOne(response.getId(), response.getSource());
		} else {
			ObjectMapper mapper = new ObjectMapper();
			mapper.getSerializationConfig().without(
					SerializationFeature.WRITE_NULL_MAP_VALUES);

			String json = mapper.writeValueAsString(server);

			IndexRequest indexRequest = new IndexRequest();
			indexRequest.index(INDEX);
			indexRequest.type(CONF_TYPE);
			indexRequest.source(json);

			this.client.index(indexRequest).get();
		}
	}

	@Override
	public void updateOne(String id, Document server)
			throws JsonProcessingException, InterruptedException,
			ExecutionException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.getSerializationConfig().without(
				SerializationFeature.WRITE_NULL_MAP_VALUES);

		String json = mapper.writeValueAsString(server);

		UpdateRequest updateRequest = new UpdateRequest();
		updateRequest.index(INDEX);
		updateRequest.type(CONF_TYPE);
		updateRequest.id(id);
		updateRequest.doc(json);

		this.client.update(updateRequest).get();
	}

	@Override
	public OutputWriter settings() throws JsonParseException,
			JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.getSerializationConfig().without(
				SerializationFeature.WRITE_NULL_MAP_VALUES);

		GetResponse getResponse = this.client
				.prepareGet(INDEX, SETTINGS_TYPE, SETTINGS_ID).execute()
				.actionGet();

		if (getResponse.isExists()) {
			return mapper.readValue(getResponse.getSourceAsString(),
					OutputWriter.class);
		} else {
			OutputWriter settings = new OutputWriter();

			this.client.prepareIndex(INDEX, SETTINGS_TYPE, SETTINGS_ID)
					.setSource(mapper.writeValueAsString(settings)).execute()
					.actionGet();

			return settings;
		}
	}

	@Override
	public void updateSettings(OutputWriter settings)
			throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.getSerializationConfig().without(
				SerializationFeature.WRITE_NULL_MAP_VALUES);

		String json = mapper.writeValueAsString(settings);

		UpdateRequest updateRequest = new UpdateRequest();
		updateRequest.index(INDEX);
		updateRequest.type(SETTINGS_TYPE);
		updateRequest.id(SETTINGS_ID);
		updateRequest.doc(json);

		this.client.update(updateRequest);

		SearchResponse response = this.client.prepareSearch(INDEX).setTypes(CONF_TYPE)
				.setQuery(QueryBuilders.matchAllQuery()).execute().actionGet();
		
		for (SearchHit hit : response.getHits().getHits()) {
			Document doc = mapper.readValue(hit.getSourceAsString(), Document.class);
			
			for (Server server : doc.getServers()) {
				for (Query query : server.getQueries()) {
					query.getOutputWriters().clear();
					query.getOutputWriters().add(settings);
				}
			}
			
			String updatedServer = mapper.writeValueAsString(doc);
			
			UpdateRequest request = new UpdateRequest();
			request.index(INDEX);
			request.type(CONF_TYPE);
			request.id(hit.getId());
			request.doc(updatedServer);
			
			this.client.update(request);
		}
		
	}

	@Override
	public Collection<String> prefixNameSuggestion(String host, String prefix) {
		SearchResponse response = this.client
				.prepareSearch(INDEX)
				.setTypes(OBJECTNAME_TYPE)
				.setQuery(QueryBuilders.termQuery("jmxhost", host))
				.addAggregation(
						AggregationBuilders.terms("nameAgg").field("name")
								.include(prefix + ".*")).execute().actionGet();

		Collection<String> result = new ArrayList<String>();

		Terms agg = response.getAggregations().get("nameAgg");
		for (Bucket b : agg.getBuckets()) {
			result.add(b.getKey());
		}

		return result;
	}

	@Override
	public void refresh(String host, int port) throws JsonProcessingException,
			InterruptedException, ExecutionException {
		client.prepareDeleteByQuery(INDEX).setTypes(OBJECTNAME_TYPE)
				.setQuery(QueryBuilders.termQuery("jmxhost", host)).execute()
				.actionGet();

		List<ObjectNameRepresentation> objectnames = this.objectNames(host,
				port);

		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(objectnames);
		json = "{" + "\"jmxhost\":\"" + host + "\"," + "\"objects\":" + json
				+ "}";

		this.client.prepareIndex(INDEX, OBJECTNAME_TYPE).setSource(json)
				.execute().actionGet();
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstanceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IntrospectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ReflectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (jmxConnector != null) {
				try {
					jmxConnector.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return new ArrayList<ObjectNameRepresentation>();
	}

	@Override
	public Collection<String> prefixAttrSuggestion(String host, String name,
			String prefix) {
		SearchResponse response = this.client
				.prepareSearch(INDEX)
				.setTypes(OBJECTNAME_TYPE)
				.setQuery(
						QueryBuilders.boolQuery().must(
								QueryBuilders.termQuery("jmxhost", host)))
				.addFields("attributes").execute().actionGet();

		Collection<String> result = new ArrayList<String>();

		for (SearchHit hit : response.getHits().getHits()) {
			result.add(hit.field("attributes").getValue().toString());
		}

		return result;
	}
}
