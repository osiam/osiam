/*
 * Copyright 2013
 *     tarent AG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osiam.resources.helper;


import org.springframework.stereotype.Service;

@Service
public class FilterParser {

    public FilterChain parse(String p, Class clazz) {
        if (CombinedFilterChain.COMBINED_FILTER_CHAIN.matcher(p).matches()) {
            return new CombinedFilterChain(p, clazz);
        }
        return new SingularFilterChain(p, clazz);

    }
}
