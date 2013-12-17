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

package org.osiam.storage.query;

import org.osiam.storage.dao.ExtensionDao;
import org.osiam.storage.entities.UserEntity;
import org.osiam.storage.helper.NumberPadder;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Locale;

@Service
public class UserFilterParser extends FilterParser<UserEntity> {

    @Inject
    private ExtensionDao extensionDao;

    @Inject
    private NumberPadder numberPadder;

    @Override
    protected FilterChain<UserEntity> createFilterChain(ScimExpression filter) {
        return new UserSimpleFilterChain(entityManager.getCriteriaBuilder(), extensionDao, filter, numberPadder);
    }

    @Override
    protected QueryField<UserEntity> getFilterField(String sortBy) {
        return UserQueryField.fromString(sortBy.toLowerCase(Locale.ENGLISH));
    }

}
