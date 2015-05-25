/*
 * Copyright (C) 2013 tarent AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.osiam.storage.query;

import java.util.Locale;

import org.osiam.storage.dao.ExtensionDao;
import org.osiam.storage.entities.UserEntity;
import org.osiam.storage.helper.NumberPadder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserFilterParser extends FilterParser<UserEntity> {

    @Autowired
    private ExtensionDao extensionDao;

    @Autowired
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
