package com.zenika.back.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zenika.back.AppConfig;
import com.zenika.back.model.ObjectNameRepresentation;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.management.*;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@Repository
public class ObjectNameRepositoryImpl implements ObjectNameRepository {

    private static final Logger logger = LoggerFactory
            .getLogger(ObjectNameRepositoryImpl.class);

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
    public void refresh(String host, int port) throws JsonProcessingException, InterruptedException, ExecutionException {
        client.prepareDeleteByQuery(AppConfig.INDEX)
                .setTypes(AppConfig.OBJECTNAME_TYPE)
                .setQuery(QueryBuilders.termQuery("host", host)).execute()
                .actionGet();

        List<ObjectNameRepresentation> objectnames = this.objectNames(host,
                port);

        for (ObjectNameRepresentation obj : objectnames) {
            String json = mapper.writeValueAsString(obj);

            this.client.prepareIndex(AppConfig.INDEX, AppConfig.OBJECTNAME_TYPE)
                    .setSource(json)
                    .execute().actionGet();
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

            List<ObjectNameRepresentation> result = new ArrayList();

            Set<ObjectName> beanSet = mbeanConn.queryNames(null, null);
            for (ObjectName name : beanSet) {
                ObjectNameRepresentation tmp = new ObjectNameRepresentation();
                tmp.setHost(host);
                tmp.setPort(port);
                tmp.setName(name.toString());

                List<String> attributes = new ArrayList();
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

        return new ArrayList<>();
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
        for (Terms.Bucket bucket : agg.getBuckets()) {
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

    @Override
    public void delete(String host, int port) {
        this.client.prepareDeleteByQuery(AppConfig.INDEX)
                .setTypes(AppConfig.OBJECTNAME_TYPE)
                .setQuery(QueryBuilders.boolQuery()
                        .must(QueryBuilders.termQuery("host", host))
                        .must(QueryBuilders.termQuery("port", port)))
                .execute().actionGet();
    }
}
