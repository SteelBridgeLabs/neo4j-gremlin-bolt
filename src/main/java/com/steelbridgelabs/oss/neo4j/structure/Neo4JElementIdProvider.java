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

/**
 * @author Rogelio J. Baucells
 */
public interface Neo4JElementIdProvider<T> {

    /**
     * Gets the field name used for identifiers in {@link Neo4JElement}.
     *
     * @return the field name used for identifiers.
     */
    String idFieldName();

    /**
     * Generates a new identifier value for a {@link Neo4JElement}.
     *
     * @return the new identifier value.
     */
    T generateId();

    /**
     * Process the given identifier converting it to the correct type if necessary.
     *
     * @param id The {@link Neo4JElement} identifier.
     * @return The {@link Neo4JElement} identifier converted to the correct type if necessary.
     */
    T processIdentifier(Object id);
}
