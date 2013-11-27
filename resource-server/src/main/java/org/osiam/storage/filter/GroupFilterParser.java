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

package org.osiam.storage.filter;

import org.osiam.storage.entities.GroupEntity;
import org.springframework.stereotype.Service;

@Service
public class GroupFilterParser extends FilterParser<GroupEntity> {

    @Override
    protected FilterChain<GroupEntity> createFilterChain(String filter) {
        return new GroupSimpleFilterChain(entityManager.getCriteriaBuilder(), filter);
    }

}
