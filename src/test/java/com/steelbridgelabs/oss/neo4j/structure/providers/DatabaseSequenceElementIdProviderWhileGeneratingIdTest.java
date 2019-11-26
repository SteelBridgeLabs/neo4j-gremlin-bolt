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

package com.steelbridgelabs.oss.neo4j.structure.providers;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.Values;

/**
 * @author Rogelio J. Baucells
 */
@RunWith(MockitoJUnitRunner.class)
public class DatabaseSequenceElementIdProviderWhileGeneratingIdTest {

    @Mock
    private Record record;

    @Mock
    private Result result;

    @Mock
    private Transaction transaction;

    @Mock
    private Session session;

    @Mock
    private Driver driver;

    @Test
    @SuppressWarnings("unchecked")
    public void givenANewProviderShouldRequestPoolOfIdentifiers() {
        // arrange
        Mockito.when(record.get(Mockito.eq(0))).thenAnswer(invocation -> Values.value(2));
        Mockito.when(result.hasNext()).thenAnswer(invocation -> true);
        Mockito.when(result.next()).thenAnswer(invocation -> record);
        Mockito.when(transaction.run(Mockito.any(String.class), Mockito.anyMap())).thenAnswer(invocation -> result);
        Mockito.when(session.beginTransaction()).thenAnswer(invocation -> transaction);
        Mockito.when(driver.session()).thenAnswer(invocation -> session);
        DatabaseSequenceElementIdProvider provider = new DatabaseSequenceElementIdProvider(driver, 2, "field1", "label");
        // act
        Long id = provider.generate();
        // assert
        Assert.assertNotNull("Invalid identifier value", id);
        Assert.assertEquals("Provider returned an invalid identifier value", 1L, (long)id);
        // act
        id = provider.generate();
        // assert
        Assert.assertNotNull("Invalid identifier value", id);
        Assert.assertEquals("Provider returned an invalid identifier value", 2L, (long)id);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void givenTwoIdentifierRequestsShouldRequestPoolOfIdentifiers() {
        // arrange
        Mockito.when(record.get(Mockito.eq(0))).thenAnswer(invocation -> Values.value(1));
        Mockito.when(result.hasNext()).thenAnswer(invocation -> true);
        Mockito.when(result.next()).thenAnswer(invocation -> record);
        Mockito.when(transaction.run(Mockito.any(String.class), Mockito.anyMap())).thenAnswer(invocation -> result);
        Mockito.when(session.beginTransaction()).thenAnswer(invocation -> transaction);
        Mockito.when(driver.session()).thenAnswer(invocation -> session);
        DatabaseSequenceElementIdProvider provider = new DatabaseSequenceElementIdProvider(driver, 1, "field1", "label");
        // act
        Long id = provider.generate();
        // assert
        Assert.assertNotNull("Invalid identifier value", id);
        Assert.assertEquals("Provider returned an invalid identifier value", 1L, (long)id);
        // arrange
        Mockito.when(record.get(Mockito.eq(0))).thenAnswer(invocation -> Values.value(2));
        // act
        id = provider.generate();
        // assert
        Assert.assertNotNull("Invalid identifier value", id);
        Assert.assertEquals("Provider returned an invalid identifier value", 2L, (long)id);
    }
}
