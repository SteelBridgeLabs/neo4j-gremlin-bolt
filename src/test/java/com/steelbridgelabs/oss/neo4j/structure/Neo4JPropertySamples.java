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

import org.neo4j.driver.v1.Values;
import org.neo4j.values.storable.CoordinateReferenceSystem;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Fredrick P. Eisele
 */
public enum Neo4JPropertySamples {
    BOOL("test-bool", true, true, false),
    LONG("test-long", true, 1L, 2L),
    DOUBLE("test-double", true, 4.0, 5.6),
    STRING("test-string", true, "this is a test", "a second text"),
    POINT("test-point", true,
            Values.point(CoordinateReferenceSystem.WGS84.getCode(), 90.0, 45.0).asPoint(),
            Values.point(CoordinateReferenceSystem.WGS84.getCode(), 120.0, -45.0).asPoint()),
    MAP("test-map", false,
            Collections.singletonMap("key1", "value1"),
            Collections.singletonMap("key1", "value2")),
    LIST("test-map", false,
            Collections.singletonList(5L),
            Collections.singletonList(5L));

    public String title;
    public Boolean supported;
    public Object value;
    public Object value2;
    public Class<?> clazz;

    Neo4JPropertySamples(String title, Boolean supported, Object value, Object value2) {
        this.title = title;
        this.supported = supported;
        this.value = value;
        this.value2 = value2;
        this.clazz = value.getClass();
    }

    static public Collection<String> getKeys() {
        Set<String> ret = new HashSet<>(Neo4JPropertySamples.values().length);
        for (Neo4JPropertySamples sam : Neo4JPropertySamples.values()) {
            ret.add(sam.title);
        }
        return ret;
    }

     static public <T> T recast(Class<T> clazz, Object obj) {
        return clazz.cast(obj);
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("title: ").append(this.title).append(", ")
                .append("[").append(this.supported).append("] ")
                .append(" ").append(this.clazz).append(" :: ")
                .append("(").append(this.value)
                .append(",").append(this.value2).append(")")
                .toString();
    }

}
