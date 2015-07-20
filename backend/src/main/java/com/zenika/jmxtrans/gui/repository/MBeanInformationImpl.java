package com.zenika.jmxtrans.gui.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zenika.jmxtrans.gui.AppConfig;
import com.zenika.jmxtrans.gui.model.ObjectNameRepresentation;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.suggest.SuggestResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class MBeanInformationImpl implements MBeanInformation {

    private static final Logger logger = LoggerFactory.getLogger(MBeanInformationImpl.class);

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
    public void save(List<ObjectNameRepresentation> objectNames) throws JsonProcessingException {
        logger.info("Saving objectnames");

        BulkRequestBuilder bulkRequestBuilder = this.client.prepareBulk().setRefresh(true);
        for (ObjectNameRepresentation objectName : objectNames) {
            bulkRequestBuilder.add(this.client.prepareIndex(AppConfig.INDEX, AppConfig.OBJECTNAME_TYPE)
                            .setSource(mapper.writeValueAsString(objectName))
            );
        }
        bulkRequestBuilder.execute().actionGet();
    }

    @Override
    public Collection<String> getObjectNames(String host, int port, String obj) {
        logger.info("Retrieving object names");
//        SuggestResponse response = this.client.prepareSuggest(AppConfig.INDEX)
//                .addSuggestion(SuggestBuilders.completionSuggestion("objectnames")
//                        .field("suggest")
//                        .text(obj).size(20))
//                .execute().actionGet();
//
//        Collection<String> result = new ArrayList<>();
//
//        for (Suggest.Suggestion.Entry.Option option : response.getSuggest().getSuggestion("objectnames").getEntries().get(0).getOptions()) {
//            result.add(option.getText().string());
//        }

        SearchResponse response = this.client.prepareSearch(AppConfig.INDEX)
                .setQuery(
                        QueryBuilders
                                .boolQuery()
                                .must(QueryBuilders.matchQuery("suggest", obj))
                                .must(QueryBuilders.termQuery("host", host))
                                .must(QueryBuilders.termQuery("port", port))
                )
                .setSize(0)
                .addAggregation(
                        AggregationBuilders
                                .terms("names")
                                .field("name")
                                .order(Terms.Order.term(true))
                                .size(20))
                .execute().actionGet();

        Collection<String> result = new ArrayList<>();

        Terms names = response.getAggregations().get("names");
        for (Terms.Bucket nameBucket : names.getBuckets()) {
            result.add(nameBucket.getKey());
        }

        return result;
    }

    @Override
    public Collection<String> getObjectName(String name) {
        logger.info("Replacing *");

        SearchResponse response = this.client.prepareSearch(AppConfig.INDEX)
                .setTypes(AppConfig.OBJECTNAME_TYPE)
                .setQuery(
                        QueryBuilders.wildcardQuery("name", name)
                )
                .addAggregation(
                        AggregationBuilders.terms("names").field("name").size(0)
                )
                .execute().actionGet();

        Collection<String> result = new ArrayList<>();
        Terms agg = response.getAggregations().get("names");
        for (Terms.Bucket bucket : agg.getBuckets()) {
            result.add(bucket.getKey());
        }

        return result;
    }

    @Override
    public void delete(String host, int port) {
        logger.info("Deleting object names");
        this.client.prepareDeleteByQuery(AppConfig.INDEX)
                .setTypes(AppConfig.OBJECTNAME_TYPE)
                .setQuery(QueryBuilders.boolQuery()
                        .must(QueryBuilders.termQuery("host", host))
                        .must(QueryBuilders.termQuery("port", port)))
                .execute().actionGet();
    }
}
