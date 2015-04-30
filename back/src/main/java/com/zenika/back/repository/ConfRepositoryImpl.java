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
public class ConfRepositoryImpl implements ConfRepository {

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
        this.client.prepareDeleteByQuery(AppConfig.INDEX)
                .setTypes(AppConfig.CONF_TYPE)
                .setQuery(
                        QueryBuilders
                                .boolQuery()
                                .must(QueryBuilders.termQuery("servers.host",
                                        host))
                                .must(QueryBuilders.termQuery("servers.port",
                                        port))).execute().actionGet();
    }

    @Override
    public Collection<Map<String, String>> findAllHostsAndPorts() {
        SearchResponse response = this.client.prepareSearch(AppConfig.INDEX)
                .setTypes(AppConfig.CONF_TYPE)
                .setQuery(QueryBuilders.matchAllQuery())
                .addAggregation(
                        AggregationBuilders.terms("hosts").field("servers.host").order(Terms.Order.term(true)).size(0)
                                .subAggregation(AggregationBuilders.terms("ports").field("servers.port").order(Terms.Order.term(true)).size(0)))
                .execute().actionGet();

        List<Map<String, String>> result = new ArrayList<>();

        Terms hosts = response.getAggregations().get("hosts");
        for (Bucket hostBucket : hosts.getBuckets()) {
            String host = hostBucket.getKey();
            Terms ports = hostBucket.getAggregations().get("ports");
            for (Bucket portBucket : ports.getBuckets()) {
                Map<String, String> res = new HashMap<>();
                res.put("host", host);
                res.put("port", portBucket.getKey());
                result.add(res);
            }
        }

        return result;
    }


    @Override
    public Response get(String host, int port) throws IOException, InterruptedException, ExecutionException {
        SearchResponse searchResponse = this.client.prepareSearch(AppConfig.INDEX)
                .setTypes(AppConfig.CONF_TYPE)
                .setQuery(QueryBuilders.matchAllQuery())
                .setPostFilter(
                        FilterBuilders
                                .boolFilter()
                                .must(FilterBuilders.termFilter("servers.host", host))
                                .must(FilterBuilders.termFilter("servers.port", port)))
                .execute().actionGet();

        // When the method is called, the number of hits is always equal to 1.
        if (searchResponse.getHits().getHits().length > 0) {
            Response response = new Response();
            Document document = null;
            List<String> toDelete = new ArrayList<>();

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
    public void save(Document document) throws InterruptedException, ExecutionException, IOException {
            String json = mapper.writeValueAsString(document);

            this.client.prepareIndex(AppConfig.INDEX, AppConfig.CONF_TYPE)
                    .setRefresh(true)
                    .setSource(json).execute()
                    .actionGet();
    }

    @Override
    public void updateOne(String id, Document document) throws JsonProcessingException, InterruptedException, ExecutionException {
        String json = mapper.writeValueAsString(document);

        this.client.prepareUpdate(AppConfig.INDEX, AppConfig.CONF_TYPE, id)
                .setRefresh(true)
                .setDoc(json)
                .execute().actionGet();
    }

    @Override
    public Map<String, Document> getAll() throws IOException {
        SearchResponse response = this.client.prepareSearch(AppConfig.INDEX)
                .setTypes(AppConfig.CONF_TYPE)
                .setQuery(QueryBuilders.matchAllQuery()).execute().actionGet();

        Map<String, Document> documents = new HashMap<>();
        for (SearchHit hit : response.getHits().getHits()) {
            Document doc = mapper.readValue(hit.getSourceAsString(), Document.class);

            documents.put(hit.getId(), doc);
        }

        return documents;
    }
}
