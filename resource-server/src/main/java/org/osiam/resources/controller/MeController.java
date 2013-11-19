package org.osiam.resources.controller;

import java.util.LinkedHashMap;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.osiam.security.authorization.AccessTokenValidationService;
import org.osiam.storage.dao.UserDao;
import org.osiam.storage.entities.EmailEntity;
import org.osiam.storage.entities.NameEntity;
import org.osiam.storage.entities.UserEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Controller
@RequestMapping(value = "/me")
/**
 * This Controller is used for getting information about the user who initialised the access_token.
 *
 */
public class MeController {

    @Inject
    private AccessTokenValidationService accessTokenValidationService;

    @Inject
    private UserDao userDAO;

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
        Authentication userAuthentication = accessTokenValidationService.loadAuthentication(accessToken)
                .getUserAuthentication();
        Object o = userAuthentication.getPrincipal();
        if (o instanceof LinkedHashMap) {
            String principalId = (String) ((LinkedHashMap) o).get("id");
            UserEntity userEntity = userDAO.getById(principalId);
            return new FacebookInformationConstruct(userEntity);
        } else {
            throw new IllegalArgumentException("User was not authenticated with OSIAM.");
        }


    }

    private String getAccessToken(HttpServletRequest request) {
        String accessToken = request.getParameter("access_token");
        return accessToken != null ? accessToken : getBearerToken(request);
    }

    private String getBearerToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization == null) {
            throw new IllegalArgumentException("No access_token submitted!");
        }
        return authorization.substring("Bearer ".length(), authorization.length());
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