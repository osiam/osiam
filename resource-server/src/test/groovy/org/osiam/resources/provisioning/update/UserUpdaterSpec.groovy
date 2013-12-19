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

package org.osiam.resources.provisioning.update

import javax.inject.Inject

import org.osiam.resources.exceptions.OsiamException
import org.osiam.resources.exceptions.ResourceExistsException
import org.osiam.resources.scim.Group
import org.osiam.resources.scim.Meta
import org.osiam.resources.scim.User
import org.osiam.storage.dao.UserDao
import org.osiam.storage.entities.UserEntity

import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll

class UserUpdaterSpec extends Specification {

    static IRRELEVANT = 'irrelevant'

    NameUpdater nameUpdater = Mock()
    EmailUpdater emailUpdater = Mock()
    PhoneNumberUpdater phoneNumberUpdater = Mock()
    ImUpdater imUpdater = Mock()
    PhotoUpdater photoUpdater = Mock()
    EntitlementsUpdater entitlementUpdater = Mock()
    RoleUpdater roleUpdater = Mock()
    X509CertificateUpdater x509CertificateUpdater = Mock()
    AddressUpdater addressUpdater = Mock()
    UserDao userDao = Mock()

    ResourceUpdater resourceUpdater = Mock()
    UserUpdater userUpdater = new UserUpdater(resourceUpdater: resourceUpdater,
            nameUpdater: nameUpdater,
            emailUpdater: emailUpdater,
            phoneNumberUpdater: phoneNumberUpdater,
            imUpdater: imUpdater,
            photoUpdater: photoUpdater,
            entitlementUpdater: entitlementUpdater,
            roleUpdater: roleUpdater,
            x509CertificateUpdater: x509CertificateUpdater,
            addressUpdater: addressUpdater,
            userDao: userDao)

    User user
    UserEntity userEntity = Mock()

    def 'updating a user triggers resource updater'() {
        given:
        user = new User.Builder().build()

        when:
        userUpdater.update(user, userEntity)

        then:
        1 * resourceUpdater.update(user, userEntity)
    }

    def 'removing the userName attribute raises exception'() {
        given:
        Meta meta = new Meta(attributes: ['userName'] as Set)
        user = new User.Builder(meta: meta).build()

        when:
        userUpdater.update(user, userEntity)

        then:
        def oe = thrown(OsiamException)
        oe.getMessage().contains('userName')
    }

    def 'updating the userName is possible' () {
        given:
        def uuid = UUID.randomUUID()
        User user = new User.Builder(IRRELEVANT).build()
        userEntity.getId() >> uuid

        when:
        userUpdater.update(user, userEntity)

        then:
        1 * userEntity.setUserName(IRRELEVANT)
    }

    @Unroll
    def 'updating userName to #value is not possible'() {
        given:
        User user = new User.Builder(userName: userName).build()

        when:
        userUpdater.update(user, userEntity)

        then:
        0 * userEntity.setUserName(_)

        where:
        value          | userName
        'null'         | null
        'empty string' | ''
    }

    def 'updating a user triggers name updater'() {
        given:
        user = new User.Builder().build()

        when:
        userUpdater.update(user, userEntity)

        then:
        1 * nameUpdater.update(null, userEntity, new HashSet())
    }

    def 'updating a user triggers email updater'() {
        given:
        user = new User.Builder().build()

        when:
        userUpdater.update(user, userEntity)

        then:
        1 * emailUpdater.update([] as List, userEntity, new HashSet())
    }

    def 'updating a user triggers phoneNumbers updater'() {
        given:
        user = new User.Builder().build()

        when:
        userUpdater.update(user, userEntity)

        then:
        1 * phoneNumberUpdater.update([] as List, userEntity, new HashSet())
    }

    def 'updating a user triggers im updater'() {
        given:
        user = new User.Builder().build()

        when:
        userUpdater.update(user, userEntity)

        then:
        1 * imUpdater.update([] as List, userEntity, new HashSet())
    }

    def 'updating a user triggers photo updater'() {
        given:
        user = new User.Builder().build()

        when:
        userUpdater.update(user, userEntity)

        then:
        1 * photoUpdater.update([] as List, userEntity, new HashSet())
    }

    def 'updating a user triggers entitlement updater'() {
        given:
        user = new User.Builder().build()

        when:
        userUpdater.update(user, userEntity)

        then:
        1 * entitlementUpdater.update([] as List, userEntity, new HashSet())
    }

    def 'updating a user triggers role updater'() {
        given:
        user = new User.Builder().build()

        when:
        userUpdater.update(user, userEntity)

        then:
        1 * roleUpdater.update([] as List, userEntity, new HashSet())
    }

    def 'updating a user triggers x509Certificate updater'() {
        given:
        user = new User.Builder().build()

        when:
        userUpdater.update(user, userEntity)

        then:
        1 * x509CertificateUpdater.update([] as List, userEntity, new HashSet())
    }

    def 'updating a user triggers address updater'() {
        given:
        user = new User.Builder().build()

        when:
        userUpdater.update(user, userEntity)

        then:
        1 * addressUpdater.update([] as List, userEntity, new HashSet())
    }

    def 'removing the displayName attribute is possible'() {
        given:
        Meta meta = new Meta(attributes: ['displayName'] as Set)
        user = new User.Builder(meta: meta).build()

        when:
        userUpdater.update(user, userEntity)

        then:
        1 * userEntity.setDisplayName(null)
    }

    def 'updating the displayName is possible' () {
        given:
        User user = new User.Builder(displayName : IRRELEVANT).build()

        when:
        userUpdater.update(user, userEntity)

        then:
        1 * userEntity.setDisplayName(IRRELEVANT)
    }

    @Unroll
    def 'updating displayName to #value is not possible'() {
        given:
        User user = new User.Builder(displayName: displayName).build()

        when:
        userUpdater.update(user, userEntity)

        then:
        0 * userEntity.setDisplayName(_)

        where:
        value          | displayName
        'null'         | null
        'empty string' | ''
    }

    def 'removing the nickName attribute is possible'() {
        given:
        Meta meta = new Meta(attributes: ['nickName'] as Set)
        user = new User.Builder(meta: meta).build()

        when:
        userUpdater.update(user, userEntity)

        then:
        1 * userEntity.setNickName(null)
    }

    def 'updating the nickName is possible' () {
        given:
        User user = new User.Builder(nickName : IRRELEVANT).build()

        when:
        userUpdater.update(user, userEntity)

        then:
        1 * userEntity.setNickName(IRRELEVANT)
    }

    @Unroll
    def 'updating nickName to #value is not possible'() {
        given:
        User user = new User.Builder(nickName: nickName).build()

        when:
        userUpdater.update(user, userEntity)

        then:
        0 * userEntity.setNickName(_)

        where:
        value          | nickName
        'null'         | null
        'empty string' | ''
    }

    def 'removing the profileUrl attribute is possible'() {
        given:
        Meta meta = new Meta(attributes: ['profileUrl'] as Set)
        user = new User.Builder(meta: meta).build()

        when:
        userUpdater.update(user, userEntity)

        then:
        1 * userEntity.setProfileUrl(null)
    }

    def 'updating the profileUrl is possible' () {
        given:
        User user = new User.Builder(profileUrl : IRRELEVANT).build()

        when:
        userUpdater.update(user, userEntity)

        then:
        1 * userEntity.setProfileUrl(IRRELEVANT)
    }

    @Unroll
    def 'updating profileUrl to #value is not possible'() {
        given:
        User user = new User.Builder(profileUrl: profileUrl).build()

        when:
        userUpdater.update(user, userEntity)

        then:
        0 * userEntity.setProfileUrl(_)

        where:
        value          | profileUrl
        'null'         | null
        'empty string' | ''
    }

    def 'removing the title attribute is possible'() {
        given:
        Meta meta = new Meta(attributes: ['title'] as Set)
        user = new User.Builder(meta: meta).build()

        when:
        userUpdater.update(user, userEntity)

        then:
        1 * userEntity.setTitle(null)
    }

    def 'updating the title is possible' () {
        given:
        User user = new User.Builder(title : IRRELEVANT).build()

        when:
        userUpdater.update(user, userEntity)

        then:
        1 * userEntity.setTitle(IRRELEVANT)
    }

    @Unroll
    def 'updating title to #value is not possible'() {
        given:
        User user = new User.Builder(title: title).build()

        when:
        userUpdater.update(user, userEntity)

        then:
        0 * userEntity.setTitle(_)

        where:
        value          | title
        'null'         | null
        'empty string' | ''
    }

    def 'removing the userType attribute is possible'() {
        given:
        Meta meta = new Meta(attributes: ['userType'] as Set)
        user = new User.Builder(meta: meta).build()

        when:
        userUpdater.update(user, userEntity)

        then:
        1 * userEntity.setUserType(null)
    }

    def 'updating the userType is possible' () {
        given:
        User user = new User.Builder(userType : IRRELEVANT).build()

        when:
        userUpdater.update(user, userEntity)

        then:
        1 * userEntity.setUserType(IRRELEVANT)
    }

    @Unroll
    def 'updating userType to #value is not possible'() {
        given:
        User user = new User.Builder(userType: userType).build()

        when:
        userUpdater.update(user, userEntity)

        then:
        0 * userEntity.setUserType(_)

        where:
        value          | userType
        'null'         | null
        'empty string' | ''
    }

    def 'removing the preferredLanguage attribute is possible'() {
        given:
        Meta meta = new Meta(attributes: ['preferredLanguage'] as Set)
        user = new User.Builder(meta: meta).build()

        when:
        userUpdater.update(user, userEntity)

        then:
        1 * userEntity.setPreferredLanguage(null)
    }

    def 'updating the preferredLanguage is possible' () {
        given:
        User user = new User.Builder(preferredLanguage : IRRELEVANT).build()

        when:
        userUpdater.update(user, userEntity)

        then:
        1 * userEntity.setPreferredLanguage(IRRELEVANT)
    }

    @Unroll
    def 'updating preferredLanguage to #value is not possible'() {
        given:
        User user = new User.Builder(preferredLanguage: preferredLanguage).build()

        when:
        userUpdater.update(user, userEntity)

        then:
        0 * userEntity.setPreferredLanguage(_)

        where:
        value          | preferredLanguage
        'null'         | null
        'empty string' | ''
    }

    def 'removing the locale attribute is possible'() {
        given:
        Meta meta = new Meta(attributes: ['locale'] as Set)
        user = new User.Builder(meta: meta).build()

        when:
        userUpdater.update(user, userEntity)

        then:
        1 * userEntity.setLocale(null)
    }

    def 'updating the locale is possible' () {
        given:
        User user = new User.Builder(locale : IRRELEVANT).build()

        when:
        userUpdater.update(user, userEntity)

        then:
        1 * userEntity.setLocale(IRRELEVANT)
    }

    @Unroll
    def 'updating locale to #value is not possible'() {
        given:
        User user = new User.Builder(locale: locale).build()

        when:
        userUpdater.update(user, userEntity)

        then:
        0 * userEntity.setLocale(_)

        where:
        value          | locale
        'null'         | null
        'empty string' | ''
    }

    def 'removing the timezone attribute is possible'() {
        given:
        Meta meta = new Meta(attributes: ['timezone'] as Set)
        user = new User.Builder(meta: meta).build()

        when:
        userUpdater.update(user, userEntity)

        then:
        1 * userEntity.setTimezone(null)
    }

    def 'updating the timezone is possible' () {
        given:
        User user = new User.Builder(timezone : IRRELEVANT).build()

        when:
        userUpdater.update(user, userEntity)

        then:
        1 * userEntity.setTimezone(IRRELEVANT)
    }

    @Unroll
    def 'updating timezone to #value is not possible'() {
        given:
        User user = new User.Builder(timezone: timezone).build()

        when:
        userUpdater.update(user, userEntity)

        then:
        0 * userEntity.setTimezone(_)

        where:
        value          | timezone
        'null'         | null
        'empty string' | ''
    }

    def 'removing the active attribute is possible'() {
        given:
        Meta meta = new Meta(attributes: ['active'] as Set)
        user = new User.Builder(meta: meta).build()

        when:
        userUpdater.update(user, userEntity)

        then:
        1 * userEntity.setActive(null)
    }

    def 'updating the active is possible' () {
        given:
        User user = new User.Builder(active : true).build()

        when:
        userUpdater.update(user, userEntity)

        then:
        1 * userEntity.setActive(true)
    }

    @Unroll
    def 'updating active to #value is not possible'() {
        given:
        User user = new User.Builder(active: active).build()

        when:
        userUpdater.update(user, userEntity)

        then:
        0 * userEntity.setActive(_)

        where:
        value          | active
        'null'         | null
    }

    def 'removing the password attribute raises exception'() {
        given:
        Meta meta = new Meta(attributes: ['password'] as Set)
        user = new User.Builder(meta: meta).build()

        when:
        userUpdater.update(user, userEntity)

        then:
        def oe = thrown(OsiamException)
        oe.getMessage().contains('password')
    }

    def 'updating the password is possible' () {
        given:
        User user = new User.Builder(password : IRRELEVANT).build()

        when:
        userUpdater.update(user, userEntity)

        then:
        1 * userEntity.setPassword(IRRELEVANT)
    }

    @Unroll
    def 'updating password to #value is not possible'() {
        given:
        User user = new User.Builder(password: password).build()

        when:
        userUpdater.update(user, userEntity)

        then:
        0 * userEntity.setPassword(_)

        where:
        value          | password
        'null'         | null
        'empty string' | ''
    }

    def 'deleting all groups raises an exception'(){
        given:
        Meta meta = new Meta(attributes: ['groups'] as Set)
        user = new User.Builder(meta: meta).build()

        when:
        userUpdater.update(user, userEntity)

        then:
        def oe = thrown(OsiamException)
        oe.getMessage().contains('group')
    }

    def 'modify a single group raises an exception'(){
        given:
        user = new User.Builder(groups : [new Group()] as List).build()

        when:
        userUpdater.update(user, userEntity)

        then:
        def oe = thrown(OsiamException)
        oe.getMessage().contains('group')
    }

    def 'updating the userName checks if the userName already exists' () {
        given:
        def uuid = UUID.randomUUID()
        User user = new User.Builder(IRRELEVANT).build()
        userEntity.getId() >> uuid

        when:
        userUpdater.update(user, userEntity)

        then:
        1 * userDao.isUserNameAlreadyTaken(IRRELEVANT, uuid.toString())
    }

    def 'updating the userName to an existing userName raises exception' () {
        given:
        def uuid = UUID.randomUUID()
        User user = new User.Builder(IRRELEVANT).build()
        userEntity.getId() >> uuid

        when:
        userUpdater.update(user, userEntity)

        then:
        1 * userDao.isUserNameAlreadyTaken(IRRELEVANT, uuid.toString()) >> true
        thrown(ResourceExistsException)
    }
}
