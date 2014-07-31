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

package org.osiam.resources.controller;

import java.util.Set;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.osiam.client.exception.ConflictException;
import org.osiam.resources.scim.User;
import org.osiam.security.authorization.AccessTokenValidationService;
import org.osiam.security.helper.AccessTokenHelper;
import org.osiam.storage.dao.UserDao;
import org.osiam.storage.entities.EmailEntity;
import org.osiam.storage.entities.NameEntity;
import org.osiam.storage.entities.UserEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * This Controller is used for getting information about the user who initialised the access_token.
 *
 */
@Controller
@RequestMapping(value = "/me")
@Transactional
public class MeController {

    @Inject
    private AccessTokenValidationService accessTokenValidationService;

    @Inject
    private UserDao userDao;

    /**
     * This method is used to get information about the user who initialised the authorization process.
     * <p/>
     * The result should be in json format and look like:
     * <p/>
     * {
     * "id": "73821979327912",
     * "name": "Arthur Dent",
     * "first_name": "Arthur
     * "last_name": "Dent",
     * "link": "https://www.facebook.com/arthur.dent.167",
     * "username": "arthur.dent.167",
     * "gender": "male",
     * "email": "...@....de",
     * "timezone": 2,
     * "locale": "en_US",
     * "verified": true,
     * "updated_time": "2012-08-20T08:03:30+0000"
     * }
     * <p/>
     * if some information are not available then ... will happen.
     *
     * @return an object to represent the json format.
     */
    @RequestMapping(value = "/**", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public FacebookInformationConstruct getInformation(HttpServletRequest request) {
        String accessToken = getAccessToken(request);
        
        OAuth2Authentication oAuth = accessTokenValidationService.loadAuthentication(accessToken);
        if(oAuth.isClientOnly()) {
            throw new ConflictException("Can't return an user. This access token belongs to a client.");
        }
        
        Authentication userAuthentication = oAuth.getUserAuthentication();
        
        Object principal = userAuthentication.getPrincipal();
        if (principal instanceof User) {
            User user = (User) principal;
            UserEntity userEntity = userDao.getById(user.getId());
            return new FacebookInformationConstruct(userEntity);
        } else {
            throw new IllegalArgumentException("User was not authenticated with OSIAM.");
        }
    }

    private String getAccessToken(HttpServletRequest request) {
        String accessToken = request.getParameter("access_token");
        return accessToken != null ? accessToken : AccessTokenHelper.getBearerToken(request);
    }

    public static class FacebookInformationConstruct {

        @JsonIgnore
        private final DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTime();
        private String id;
        private String name;
        private String first_name; // NOSONAR - needed pattern due to json serializing
        private String last_name; // NOSONAR - needed pattern due to json serializing
        private String link = "not supported.";
        private String userName;
        private String gender = "not supported.";
        private String email;
        private int timezone = 2; //The user's timezone offset from UTC
        private String locale;
        private boolean verified = true;
        private String updated_time; // NOSONAR - needed pattern due to json serializing

        public FacebookInformationConstruct(UserEntity userEntity) {
            this.id = userEntity.getId().toString();
            setName(userEntity);
            this.email = lookForEmail(userEntity.getEmails());
            this.locale = userEntity.getLocale();
            this.updated_time = dateTimeFormatter.print(userEntity.getMeta().getLastModified().getTime());
            this.userName = userEntity.getUserName();
        }

        private String lookForEmail(Set<EmailEntity> emails) {
            for (EmailEntity e : emails) {
                if (e.isPrimary()) {
                    return e.getValue();
                }
            }
            return null;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        private void setName(UserEntity userEntity) {
            NameEntity nameEntity = userEntity.getName();
            if (nameEntity != null) {
                this.name = nameEntity.getFormatted();
                this.first_name = nameEntity.getGivenName();
                this.last_name = nameEntity.getFamilyName();
            }
        }

        public String getFirst_name() { // NOSONAR - needed pattern due to json serializing
            return first_name;
        }

        public String getLast_name() { // NOSONAR - needed pattern due to json serializing
            return last_name;
        }

        public String getLink() {
            return link;
        }

        public String getUserName() { // NOSONAR - (confusing names) UserEntity.getUsername() is overwritten from super class
            return userName;
        }

        public String getGender() {
            return gender;
        }

        public String getEmail() {
            return email;
        }

        public int getTimezone() {
            return timezone;
        }

        public String getLocale() {
            return locale;
        }

        public boolean isVerified() {
            return verified;
        }

        public String getUpdated_time() { // NOSONAR - needed pattern due to json serializing
            return updated_time;
        }
    }
}