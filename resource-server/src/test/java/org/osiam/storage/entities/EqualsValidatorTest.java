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
package org.osiam.storage.entities;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import org.junit.Test;

/*
 * This test class is here only to validate that all our entities observe the equals/hashCode contract.
 */
public class EqualsValidatorTest {

    @Test
    public void addressEntity_honors_equals_contract() {
        EqualsVerifier.forClass(AddressEntity.class)
                .usingGetClass()
                .verify();
    }

    @Test
    public void emailEntity_honors_equals_contract() {
        EqualsVerifier.forClass(EmailEntity.class)
                .usingGetClass()
                .verify();
    }

    @Test
    public void extensionEntity_honors_equals_contract() {
        EqualsVerifier.forClass(ExtensionEntity.class)
                .usingGetClass()
                .verify();
    }

    @Test
    public void extensionFieldEntity_honors_equals_contract() {
        EqualsVerifier.forClass(ExtensionFieldEntity.class)
                .usingGetClass()
                .verify();
    }

    @Test
    public void extensionFieldValueEntity_honors_equals_contract() {
        EqualsVerifier.forClass(ExtensionFieldValueEntity.class)
                .usingGetClass()
                .verify();
    }


    @Test
    public void groupEntity_honors_equals_contract() {
        EqualsVerifier.forClass(GroupEntity.class)
                .suppress(Warning.NULL_FIELDS)
                .usingGetClass()
                .verify();
    }


    @Test
    public void imEntity_honors_equals_contract() {
        EqualsVerifier.forClass(ImEntity.class)
                .usingGetClass()
                .verify();
    }

    @Test
    public void metaEntity_honors_equals_contract() {
        EqualsVerifier.forClass(MetaEntity.class)
                .usingGetClass()
                .verify();
    }

    @Test
    public void multiValueAttributeEntity_honors_equals_contract() {
        EqualsVerifier.forClass(MultiValueAttributeEntitySkeleton.class)
                .usingGetClass()
                .verify();
    }

    @Test
    public void nameEntity_honors_equals_contract() {
        EqualsVerifier.forClass(NameEntity.class)
                .usingGetClass()
                .verify();
    }

    @Test
    public void phoneNumberEntity_honors_equals_contract() {
        EqualsVerifier.forClass(PhoneNumberEntity.class)
                .usingGetClass()
                .verify();
    }

    @Test
    public void photoEntity_honors_equals_contract() {
        EqualsVerifier.forClass(PhotoEntity.class)
                .usingGetClass()
                .verify();
    }

    @Test
    public void userEntity_honors_equals_contract() {
        EqualsVerifier.forClass(UserEntity.class)
                .suppress(Warning.NULL_FIELDS)
                .usingGetClass()
                .verify();
    }
}
