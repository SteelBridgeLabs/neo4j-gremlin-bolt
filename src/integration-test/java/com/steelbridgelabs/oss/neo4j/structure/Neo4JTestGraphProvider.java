/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.steelbridgelabs.oss.neo4j.structure;

import org.apache.commons.configuration.Configuration;
import org.apache.tinkerpop.gremlin.AbstractGraphProvider;
import org.apache.tinkerpop.gremlin.LoadGraphWith;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author Rogelio J. Baucells
 */
public class Neo4JTestGraphProvider extends AbstractGraphProvider {

    private static final Set<Class> implementations = new HashSet<>() {{
        add(Neo4JEdge.class);
        add(Neo4JGraph.class);
        add(Neo4JVertex.class);
    }};

    @Override
    public Map<String, Object> getBaseConfiguration(String graphName, Class<?> test, String testMethodName, LoadGraphWith.GraphData graphData) {
        // build configuration
        Configuration configuration = Neo4JGraphConfigurationBuilder.connect("localhost", "neo4j", "neo4j123", null, false)
            .withIdentifier("localhost-neo4j")
            .withName(graphName)
            .withElementIdProvider(ElementIdProvider.class)
            .build();
        // create property map from configuration
        Map<String, Object> map = StreamSupport.stream(Spliterators.spliteratorUnknownSize(configuration.getKeys(), Spliterator.NONNULL | Spliterator.IMMUTABLE), false)
            .collect(Collectors.toMap(key -> key, configuration::getProperty));
        // append class name
        map.put(Graph.GRAPH, Neo4JGraph.class.getName());
        // return configuration map
        return map;
    }

    @Override
    public void clear(Graph graph, Configuration configuration) throws Exception {
        // check graph instance
        if (graph != null) {
            // close graph instance
            graph.close();
        }
        // create driver instance
        Driver driver = Neo4JGraphFactory.createDriverInstance(configuration);
        // session configuration
        SessionConfig.Builder config = SessionConfig.builder();
        // check database is set
        String database = configuration.getString(Neo4JGraphConfigurationBuilder.Neo4JDatabaseConfigurationKey, null);
        if (database != null) {
            // update session config
            config.withDatabase(database);
        }
        // open session
        try (Session session = driver.session(config.build())) {
            // begin transaction
            try (org.neo4j.driver.Transaction transaction = session.beginTransaction()) {
                // delete everything in database
                transaction.run("MATCH (n) DETACH DELETE n");
                // commit
                transaction.commit();
            }
        }
    }

    @Override
    public Set<Class> getImplementations() {
        return implementations;
    }
}
