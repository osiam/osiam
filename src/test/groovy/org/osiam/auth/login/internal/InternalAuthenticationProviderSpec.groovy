/**
 * The MIT License (MIT)
 *
 * Copyright (C) 2013-2016 tarent solutions GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.osiam.auth.login.internal

import org.osiam.resources.provisioning.SCIMUserProvisioning
import org.osiam.resources.scim.User
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.encoding.ShaPasswordEncoder
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import spock.lang.Shared
import spock.lang.Specification

class InternalAuthenticationProviderSpec extends Specification {

    // hashs of 'password'
    def VALID_BCRYPT_HASHED_PASSWORD = '$2a$13$PfXyvWHogpPZgZjnk2a0eO7wSSNsMZ6DL4x5k8Bk0CK1cwXwMs0de'
    def VALID_SHA_HASHED_PASSWORD = 'dc9b4c4a06e7dc774e469a16243ff448a02932fd323bb7ecb0ece308d6bb13c964dd7a0b7b99b15dc785004bdb869450f4070a9f83b027af43025149d1bc8f34'

    // hash of 'wrongpassword'
    def WRONG_SHA_HASHED_PASSWORD = 'bfe2ee0a1bd6ca719f7b6855eaa2ce166e9f06678a662accac9b8f84ba84801d7f76c7c9a05d5207301fee9a6a11d3178eb34c47647835e7f888d3ce6e7935ba'

    def userName = 'userName'
    def password = 'password'
    def userId = '618b398c-0110-43f2-95df-d1bc4e7d2b4a'

    def userProvisioning = Mock(SCIMUserProvisioning)
    def bCryptPasswordEncoder = new BCryptPasswordEncoder(13)

    @Shared
    def shaPasswordEncoder = new ShaPasswordEncoder(512)

    InternalAuthenticationProvider provider =
            new InternalAuthenticationProvider(userProvisioning, shaPasswordEncoder, bCryptPasswordEncoder, 1, null)

    def setupSpec() {
        shaPasswordEncoder.iterations = 1000
    }

    def 'the internal provider only supports InternalAuthentication class'() {
        expect:
        provider.supports(InternalAuthentication)
    }

    def 'successful authentication with bcrypt hash is possible'() {
        given:
        User user = new User.Builder(userName)
                .setPassword(VALID_BCRYPT_HASHED_PASSWORD)
                .setId(userId)
                .setActive(true)
                .build()
        InternalAuthentication authentication = new InternalAuthentication(userName, password)
        userProvisioning.getByUsernameWithPassword(_) >> user

        when:
        Authentication successfulAuthentication = provider.authenticate(authentication)

        then:
        ((User) successfulAuthentication.principal).userName == userName
    }

    def 'successful authentication with sha hash is possible and replaces user'() {
        given:
        User user = new User.Builder(userName)
                .setPassword(VALID_SHA_HASHED_PASSWORD)
                .setId(userId)
                .setActive(true)
                .build()
        InternalAuthentication authentication = new InternalAuthentication(userName, password)
        userProvisioning.getByUsernameWithPassword(_) >> user
        User replaceUser = new User.Builder(user).setPassword(password).build();

        when:
        Authentication successfulAuthentication = provider.authenticate(authentication)

        then:
        1 * userProvisioning.replace(user.getId(), replaceUser);
        ((User) successfulAuthentication.principal).userName == userName
    }

    def 'authentication with wrong sha hash fails'() {
        given:
        User user = new User.Builder(userName)
                .setPassword(WRONG_SHA_HASHED_PASSWORD)
                .setId(userId)
                .setActive(true)
                .build()
        InternalAuthentication authentication = new InternalAuthentication(userName, password)
        userProvisioning.getByUsernameWithPassword(_) >> user

        when:
        provider.authenticate(authentication)

        then:
        thrown(BadCredentialsException)
    }
}
