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
package org.osiam.resources.scim

import spock.lang.Specification

class UpdateUserSpec extends Specification {

    Address newAddress
    Address deleteAddress
    Email newEmail
    Email deleteEmail
    Entitlement newEntit
    Entitlement deleteEntit
    Im newIm
    Im deleteIm
    PhoneNumber newPhoneNumber
    PhoneNumber deletePhoneNumber
    Photo newPhoto
    Photo deletePhoto
    Role newRole
    Role deleteRole
    X509Certificate newX509Certificate
    X509Certificate deleteX509Certificate
    Extension extension
    String DELETE = "delete"

    def 'building a update User for deleting works as aspected'(){

        given:
        createUserValueObjects()
        UpdateUser updateUser = createUpdateUserForDeletion()

        when:
        User scimUser = updateUser.getScimConformUpdateUser()

        then:
        scimUser.getAddresses().size() == 1
        scimUser.getAddresses().get(0) == deleteAddress

        scimUser.getEmails().size() == 1
        scimUser.getEmails().get(0) == deleteEmail

        scimUser.getEntitlements().size() == 1
        scimUser.getEntitlements().get(0) == deleteEntit

        scimUser.getIms().size() == 1
        scimUser.getIms().get(0) == deleteIm

        scimUser.getPhoneNumbers().size() == 1
        scimUser.getPhoneNumbers().get(0) == deletePhoneNumber

        scimUser.getPhotos().size() == 1
        scimUser.getPhotos().get(0) == deletePhoto

        scimUser.getRoles().size() == 1
        scimUser.getRoles().get(0) == deleteRole

        scimUser.getX509Certificates().size() == 1
        scimUser.getX509Certificates().get(0) == deleteX509Certificate

        scimUser.getMeta().getAttributes().contains('addresses')
        scimUser.getMeta().getAttributes().contains('displayName')
        scimUser.getMeta().getAttributes().contains('emails')
        scimUser.getMeta().getAttributes().contains('entitlements')
        scimUser.getMeta().getAttributes().contains('some.ext')
        scimUser.getMeta().getAttributes().contains('extension.field')
        scimUser.getMeta().getAttributes().contains('externalId')
        scimUser.getMeta().getAttributes().contains('ims')
        scimUser.getMeta().getAttributes().contains('locale')
        scimUser.getMeta().getAttributes().contains('nickName')
        scimUser.getMeta().getAttributes().contains('phoneNumbers')
        scimUser.getMeta().getAttributes().contains('photos')
        scimUser.getMeta().getAttributes().contains('preferredLanguage')
        scimUser.getMeta().getAttributes().contains('profileUrl')
        scimUser.getMeta().getAttributes().contains('roles')
        scimUser.getMeta().getAttributes().contains('timezone')
        scimUser.getMeta().getAttributes().contains('title')
        scimUser.getMeta().getAttributes().contains('x509Certificates')
    }

    def 'building a update User for add works as aspected'(){

        given:
        createUserValueObjects()
        UpdateUser updateUser = createUpdateUserForAdd()

        when:
        User scimUser = updateUser.getScimConformUpdateUser()

        then:
        scimUser.getAddresses().size() == 1
        scimUser.getAddresses().get(0) == newAddress

        scimUser.getEmails().size() == 1
        scimUser.getEmails().get(0) == newEmail

        scimUser.getEntitlements().size() == 1
        scimUser.getEntitlements().get(0) == newEntit

        scimUser.getIms().size() == 1
        scimUser.getIms().get(0) == newIm

        scimUser.getPhoneNumbers().size() == 1
        scimUser.getPhoneNumbers().get(0) == newPhoneNumber

        scimUser.getPhotos().size() == 1
        scimUser.getPhotos().get(0) == newPhoto

        scimUser.getRoles().size() == 1
        scimUser.getRoles().get(0) == newRole

        scimUser.getX509Certificates().size() == 1
        scimUser.getX509Certificates().get(0) == newX509Certificate
    }

    def 'building a update User for update works as aspected'(){

        given:
        createUserValueObjects()
        User compareUser = createCompareUser()
        UpdateUser updateUser = createUpdateUserForUpdate(compareUser)

        when:
        User scimUser = updateUser.getScimConformUpdateUser()

        then:
        scimUser.getAddresses().size() == 2
        scimUser.getAddresses().get(0) == deleteAddress
        scimUser.getAddresses().get(0).getOperation() == DELETE
        scimUser.getAddresses().get(1) == newAddress

        scimUser.getEmails().size() == 2
        scimUser.getEmails().get(0) == deleteEmail
        scimUser.getEmails().get(0).getOperation() == DELETE
        scimUser.getEmails().get(1) == newEmail

        scimUser.getEntitlements().size() == 2
        scimUser.getEntitlements().get(0) == deleteEntit
        scimUser.getEntitlements().get(0).getOperation() == DELETE
        scimUser.getEntitlements().get(1) == newEntit

        scimUser.getIms().size() == 2
        scimUser.getIms().get(0) == deleteIm
        scimUser.getIms().get(0).getOperation() == DELETE
        scimUser.getIms().get(1) == newIm

        scimUser.getPhoneNumbers().size() == 2
        scimUser.getPhoneNumbers().get(0) == deletePhoneNumber
        scimUser.getPhoneNumbers().get(0).getOperation() == DELETE
        scimUser.getPhoneNumbers().get(1) == newPhoneNumber

        scimUser.getPhotos().size() == 2
        scimUser.getPhotos().get(0) == deletePhoto
        scimUser.getPhotos().get(0).getOperation() == DELETE
        scimUser.getPhotos().get(1) == newPhoto

        scimUser.getRoles().size() == 2
        scimUser.getRoles().get(0) == deleteRole
        scimUser.getRoles().get(0).getOperation() == DELETE
        scimUser.getRoles().get(1) == newRole

        scimUser.getX509Certificates().size() == 2
        scimUser.getX509Certificates().get(0) == deleteX509Certificate
        scimUser.getX509Certificates().get(0).getOperation() == DELETE
        scimUser.getX509Certificates().get(1) == newX509Certificate

        scimUser.isActive() == compareUser.isActive()
        scimUser.getDisplayName() == compareUser.getDisplayName()
        scimUser.getExternalId() == compareUser.getExternalId()
        scimUser.getLocale() == compareUser.getLocale()
        scimUser.getName() == compareUser.getName()
        scimUser.getNickName() == compareUser.getNickName()
        scimUser.getPassword() == compareUser.getPassword()
        scimUser.getPreferredLanguage() == compareUser.getPreferredLanguage()
        scimUser.getProfileUrl() == compareUser.getProfileUrl()
        scimUser.getTimezone() == compareUser.getTimezone()
        scimUser.getTitle() == compareUser.getTitle()
        scimUser.getUserName() == compareUser.getUserName()
        scimUser.getUserType() == compareUser.getUserType()

        scimUser.getSchemas().contains(extension.getUrn())
    }

    private createUserValueObjects(){
        newAddress = new Address.Builder()
                .setCountry('country')
                .setFormatted('new formatted address')
                .setLocality('local').build()
        deleteAddress = new Address.Builder()
                .setCountry('del country')
                .setFormatted('delete formatted address')
                .setLocality('del local')
                .setOperation(DELETE)
                .build()

        newEmail = new Email.Builder()
                .setValue('newEmail@test.com').build()
        deleteEmail = new Email.Builder()
                .setValue('deleteEmail@test.com')
                .setOperation(DELETE)
                .build()

        newEntit = new Entitlement.Builder()
                .setValue('new Entitlement').build()
        deleteEntit = new Entitlement.Builder()
                .setValue('delete Entitlement')
                .setOperation(DELETE)
                .build()

        newIm = new Im.Builder()
                .setValue('icq').build()
        deleteIm = new Im.Builder()
                .setValue('msn')
                .setOperation(DELETE)
                .build()

        newPhoneNumber = new PhoneNumber.Builder()
                .setValue('new PhoneNumber').build()
        deletePhoneNumber = new PhoneNumber.Builder()
                .setValue('delete PhoneNumber')
                .setOperation(DELETE)
                .build()

        newPhoto = new Photo.Builder()
                .setValue('new Photo').build()
        deletePhoto = new Photo.Builder()
                .setValue(new URI('delete_Photo'))
                .setOperation(DELETE)
                .build()

        newRole = new Role.Builder()
                .setValue('new Role').build()
        deleteRole = new Role.Builder()
                .setValue('delete Role')
                .setOperation(DELETE)
                .build()

        newX509Certificate = new X509Certificate.Builder()
                .setValue('new X509Certificate').build()
        deleteX509Certificate = new X509Certificate.Builder()
                .setValue('delete X509Certificate')
                .setOperation(DELETE)
                .build()

        extension = new Extension.Builder('extension').setField('field', 'value').build()
    }

    private createUpdateUserForDeletion(){
        UpdateUser.Builder updateBuilder = new UpdateUser.Builder()
        updateBuilder.deleteAddress(deleteAddress)
        updateBuilder.deleteAddresses()
        updateBuilder.deleteDisplayName()
        updateBuilder.deleteEmail(deleteEmail)
        updateBuilder.deleteEmails()
        updateBuilder.deleteEntitlement(deleteEntit)
        updateBuilder.deleteEntitlements()
        updateBuilder.deleteExtension('some.ext')
        updateBuilder.deleteExtensionField('extension', 'field')
        updateBuilder.deleteExternalId()
        updateBuilder.deleteIms()
        updateBuilder.deleteIm(deleteIm)
        updateBuilder.deleteLocal()
        updateBuilder.deleteName()
        updateBuilder.deleteNickName()
        updateBuilder.deletePhoneNumber(deletePhoneNumber)
        updateBuilder.deletePhoneNumbers()
        updateBuilder.deletePhoto(deletePhoto)
        updateBuilder.deletePhotos()
        updateBuilder.deletePreferredLanguage()
        updateBuilder.deleteProfileUrl()
        updateBuilder.deleteRole(deleteRole)
        updateBuilder.deleteRoles()
        updateBuilder.deleteTimezone()
        updateBuilder.deleteTitle()
        updateBuilder.deleteX509Certificate(deleteX509Certificate)
        updateBuilder.deleteX509Certificates()

        updateBuilder.build()
    }

    private createUpdateUserForAdd(){
        UpdateUser.Builder updateBuilder = new UpdateUser.Builder()

        updateBuilder.addAddress(newAddress)
        updateBuilder.addEmail(newEmail)
        updateBuilder.addEntitlement(newEntit)
        updateBuilder.addIm(newIm)
        updateBuilder.addPhoneNumber(newPhoneNumber)
        updateBuilder.addPhoto(newPhoto)
        updateBuilder.addRole(newRole)
        updateBuilder.addX509Certificate(newX509Certificate)

        updateBuilder.build()
    }

    private createUpdateUserForUpdate(User compareUser){
        UpdateUser.Builder updateBuilder = new UpdateUser.Builder()

        updateBuilder.updateActive(compareUser.isActive())
        updateBuilder.updateAddress(deleteAddress, newAddress)
        updateBuilder.updateDisplayName(compareUser.getDisplayName())
        updateBuilder.updateEmail(deleteEmail, newEmail)
        updateBuilder.updateEntitlement(deleteEntit, newEntit)
        updateBuilder.updateExtension(extension)
        updateBuilder.updateExternalId(compareUser.getExternalId())
        updateBuilder.updateIm(deleteIm, newIm)
        updateBuilder.updateLocale(compareUser.getLocale())
        updateBuilder.updateName(compareUser.getName())
        updateBuilder.updateNickName(compareUser.getNickName())
        updateBuilder.updatePassword(compareUser.getPassword())
        updateBuilder.updatePhoneNumber(deletePhoneNumber, newPhoneNumber)
        updateBuilder.updatePhoto(deletePhoto, newPhoto)
        updateBuilder.updatePreferredLanguage(compareUser.getPreferredLanguage())
        updateBuilder.updateProfileUrl(compareUser.getProfileUrl())
        updateBuilder.updateRole(deleteRole, newRole)
        updateBuilder.updateTimezone(compareUser.getTimezone())
        updateBuilder.updateTitle(compareUser.getTitle())
        updateBuilder.updateUserName(compareUser.getUserName())
        updateBuilder.updateUserType(compareUser.getUserType())
        updateBuilder.updateX509Certificate(deleteX509Certificate, newX509Certificate)

        updateBuilder.build()
    }

    private createCompareUser(){
        List<Address> addresses = new ArrayList<Address>()
        addresses.add(newAddress)
        addresses.add(deleteAddress)

        List<Email> emails = new ArrayList<Email>()
        emails.add(newEmail)
        emails.add(deleteEmail)

        List<Entitlement> entitlements = new ArrayList<Entitlement>()
        entitlements.add(newEntit)
        entitlements.add(deleteEntit)

        List<Im> ims = new ArrayList<Im>()
        ims.add(newIm)
        ims.add(deleteIm)

        Name name = new Name.Builder()
                .setFamilyName('family')
                .setGivenName('given').build()

        List<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>()
        phoneNumbers.add(newPhoneNumber)
        phoneNumbers.add(deletePhoneNumber)

        List<Photo> photos = new ArrayList<Photo>()
        photos.add(newPhoto)
        photos.add(deletePhoto)

        List<Role> roles = new ArrayList<Role>()
        roles.add(newRole)
        roles.add(deleteRole)

        List<X509Certificate> x509Certificates = new ArrayList<X509Certificate>()
        x509Certificates.add(newX509Certificate)
        x509Certificates.add(deleteX509Certificate)

        User.Builder userBuilder = new User.Builder('username')
        userBuilder.setActive(true)
        userBuilder.addAddresses(addresses)
        userBuilder.setDisplayName('displayN')
        userBuilder.addEmails(emails)
        userBuilder.addEntitlements(entitlements)
        userBuilder.setExternalId('external id')
        userBuilder.addIms(ims)
        userBuilder.setLocale('locale')
        userBuilder.setName(name)
        userBuilder.setNickName('nick')
        userBuilder.setPassword('password')
        userBuilder.addPhoneNumbers(phoneNumbers)
        userBuilder.addPhotos(photos)
        userBuilder.setPreferredLanguage('pr language')
        userBuilder.addRoles(roles)
        userBuilder.setTimezone('timez')
        userBuilder.setTitle('title')
        userBuilder.addX509Certificates(x509Certificates)
        userBuilder.setUserType('King Jorge')

        userBuilder.build()
    }
}
