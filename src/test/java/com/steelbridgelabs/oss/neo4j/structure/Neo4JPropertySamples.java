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

public enum Neo4JPropertySamples {
    BOOL("test-bool", true),
    LONG("test-long", 1L),
    DOUBLE("test-double", 4.0),
    STRING("test-string", "this is a test"),
    POINT("test-point", Values.point(CoordinateReferenceSystem.WGS84.getCode(), 90.0, 45.0));

    public String title;
    public Object value;
    public Class<?> clazz;

    private Neo4JPropertySamples(String title, Object value) {
        this.title = title;
        this.value = value;
        this.clazz = value.getClass();
    }

}
