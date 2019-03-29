/*
 *  Copyright 2016 SteelBridge Laboratories, LLC.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  For more information: http://steelbridgelabs.com
 */

package com.steelbridgelabs.oss.neo4j.structure;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.Transaction;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.neo4j.driver.v1.Values;
import org.neo4j.driver.v1.types.Node;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Rogelio J. Baucells
 */
@RunWith(MockitoJUnitRunner.class)
public class Neo4JVertexWhileRollbackTest {
    @Rule
    public ErrorCollector collector = new ErrorCollector();

    @Mock
    private Neo4JGraph graph;

    @Mock
    private Transaction transaction;

    @Mock
    private Neo4JSession session;

    @Mock
    private Neo4JReadPartition partition;

    @Mock
    private Node node;

    @Mock
    private Neo4JElementIdProvider provider;

    @Mock
    private Graph.Features.VertexFeatures vertexFeatures;

    @Mock
    private Graph.Features features;

    @Test
    public void givenStringPropertyShouldRollbackToOriginalValue() {
        // arrange
        Mockito.when(vertexFeatures.getCardinality(Mockito.anyString())).thenAnswer(invocation -> VertexProperty.Cardinality.single);
        Mockito.when(features.vertex()).thenAnswer(invocation -> vertexFeatures);
        Mockito.when(graph.tx()).thenAnswer(invocation -> transaction);
        Mockito.when(graph.getPartition()).thenAnswer(invocation -> partition);
        Mockito.when(graph.features()).thenAnswer(invocation -> features);
        Mockito.when(node.get(Mockito.eq("id"))).thenAnswer(invocation -> Values.value(1L));
        Mockito.when(node.labels()).thenAnswer(invocation -> Collections.singletonList("l1"));

        Mockito.when(node.keys()).thenAnswer(invocation -> Neo4JPropertySamples.getKeys());
        for ( Neo4JPropertySamples sam : Neo4JPropertySamples.values() ) {
            Mockito.when(node.get(Mockito.eq(sam.title))).thenAnswer(invocation -> Values.value(sam.value2));
        }
        Mockito.when(provider.fieldName()).thenAnswer(invocation -> "id");
        Mockito.when(provider.generate()).thenAnswer(invocation -> 2L);
        Neo4JVertex vertex = new Neo4JVertex(graph, session, provider, provider, node);

        Map<Neo4JPropertySamples, VertexProperty<?>> results = new HashMap<>(Neo4JPropertySamples.values().length);
        for ( Neo4JPropertySamples sam : Neo4JPropertySamples.values() ) {
            try {
                VertexProperty<?> res = vertex.property(sam.title, sam.value);
                results.put(sam, res);
            } catch (IllegalArgumentException ex) {
                if (! sam.supported) continue;

                StringBuffer sb = new StringBuffer("could not add property ")
                        .append(" [").append(sam).append("] ")
                        .append('\n').append("Stacktrace:").append('\n');
                for (StackTraceElement element : ex.getStackTrace() ) {
                    sb.append(element.toString());
                };
                collector.addError(new Throwable(sb.toString()));
            }
        }
        // act
        vertex.rollback();
        // assert
        for ( Map.Entry<Neo4JPropertySamples,VertexProperty<?>> entry : results.entrySet() ) {
            Neo4JPropertySamples sam = entry.getKey();
            Assert.assertNotNull(vertex.property(sam.title));
            Property<String> property = vertex.property(sam.title);
            if (! sam.supported) continue;
            String errmsg = String.format("orig: %s, prop: %s", sam.toString(), property.toString());
            Assert.assertTrue(errmsg, property.isPresent());
            Assert.assertEquals(errmsg, sam.value2, property.value());
        }
    }

    @Test
    public void givenDirtyVertexShouldRollbackToOriginalState() {
        // arrange
        Mockito.when(vertexFeatures.getCardinality(Mockito.anyString())).thenAnswer(invocation -> VertexProperty.Cardinality.single);
        Mockito.when(features.vertex()).thenAnswer(invocation -> vertexFeatures);
        Mockito.when(graph.tx()).thenAnswer(invocation -> transaction);
        Mockito.when(graph.getPartition()).thenAnswer(invocation -> partition);
        Mockito.when(graph.features()).thenAnswer(invocation -> features);
        Mockito.when(node.get(Mockito.eq("id"))).thenAnswer(invocation -> Values.value(1L));
        Mockito.when(node.labels()).thenAnswer(invocation -> Collections.singletonList("l1"));
        Mockito.when(node.keys()).thenAnswer(invocation -> Collections.singleton("key1"));
        Mockito.when(node.get(Mockito.eq("key1"))).thenAnswer(invocation -> Values.value("value1"));
        Mockito.when(provider.generate()).thenAnswer(invocation -> 2L);
        Mockito.when(provider.fieldName()).thenAnswer(invocation -> "id");
        Neo4JVertex vertex = new Neo4JVertex(graph, session, provider, provider, node);

        Map<Neo4JPropertySamples, VertexProperty<?>> results = new HashMap<>(Neo4JPropertySamples.values().length);
        for ( Neo4JPropertySamples sam : Neo4JPropertySamples.values() ) {

            try {
                results.put(sam, vertex.property(sam.title, sam.value));
            } catch (IllegalArgumentException ex) {
                if (! sam.supported) continue;

                StringBuffer sb = new StringBuffer("could not add property ")
                        .append(" [").append(sam).append("] ")
                        .append('\n').append("Stacktrace:").append('\n');
                for (StackTraceElement element : ex.getStackTrace() ) {
                    sb.append(element.toString());
                };
                collector.addError(new Throwable(sb.toString()));
            }
        }
        // act
        vertex.rollback();
        // assert
        Assert.assertFalse("Failed to rollback vertex state", vertex.isDirty());
    }
}
