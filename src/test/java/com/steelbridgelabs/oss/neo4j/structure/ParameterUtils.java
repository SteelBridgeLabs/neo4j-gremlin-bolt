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

import java.util.HashMap;
import java.util.Map;

public class ParameterUtils {

    public static Map<String, Object> createParameters(Object... keyValues) {
        // create map
        Map<String, Object> parameters = new HashMap<>();
        // loop key values
        for (int i = 0; i < keyValues.length; i += 2) {
            // key
            String key = keyValues[i].toString();
            // append pair
            parameters.put(key, keyValues[i + 1]);
        }
        return parameters;
    }
}
