package com.zenika.back.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
import com.zenika.back.model.Response;
import com.zenika.back.model.WriterSettings;

@Repository
public class ServerRepositoryCustomImpl implements ServerRepositoryCustom {

	private static final String INDEX = ".jmxtrans";
	private static final String TYPE = "conf";
	private static final String SETTINGS_TYPE = "settings";
	private static final String SETTINGS_ID = "writer";

	private Client client;

	@Autowired
	public void setClient(Client client) {
		this.client = client;
	}

	@Override
	public void deleteOne(String id) {
		this.client.prepareDelete(INDEX, TYPE, id).execute().actionGet();
	}

	@Override
	public void delete(String host) {
		this.client.prepareDeleteByQuery(INDEX).setTypes(TYPE)
				.setQuery(QueryBuilders.termQuery("host", host)).execute()
				.actionGet();
	}

	@Override
	public String findAllHost() throws JsonProcessingException {
		String aggregatorTerm = "hosts";

		SearchResponse response = this.client
				.prepareSearch(INDEX)
				.setTypes(TYPE)
				.setQuery(QueryBuilders.matchAllQuery())
				.addAggregation(
						AggregationBuilders.terms(aggregatorTerm).field("host"))
				.execute().actionGet();

		ObjectMapper mapper = new ObjectMapper();
		Terms agg = response.getAggregations().get(aggregatorTerm);

		List<String> hosts = new ArrayList<String>();
		for (Bucket b : agg.getBuckets()) {
			hosts.add(b.getKey());
		}

		return mapper.writeValueAsString(hosts);
	}

	@Override
	public Response getByHost(String host) throws JsonParseException,
			JsonMappingException, IOException, InterruptedException,
			ExecutionException {
		SearchResponse searchResponse = this.client
				.prepareSearch(INDEX)
				.setTypes(TYPE)
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
			indexRequest.type(TYPE);
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
		updateRequest.type(TYPE);
		updateRequest.id(id);
		updateRequest.doc(json);

		this.client.update(updateRequest).get();
	}

	@Override
	public WriterSettings settings() throws JsonParseException,
			JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.getSerializationConfig().without(
				SerializationFeature.WRITE_NULL_MAP_VALUES);

		GetResponse getResponse = this.client
				.prepareGet(INDEX, SETTINGS_TYPE, SETTINGS_ID).execute()
				.actionGet();

		if (getResponse.isExists()) {
			return mapper.readValue(getResponse.getSourceAsString(),
					WriterSettings.class);
		} else {
			WriterSettings settings = new WriterSettings();

			this.client.prepareIndex(INDEX, SETTINGS_TYPE, SETTINGS_ID)
					.setSource(mapper.writeValueAsString(settings)).execute()
					.actionGet();

			return settings;
		}
	}

	@Override
	public void updateSettings(WriterSettings settings)
			throws JsonProcessingException {
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
	}
}
