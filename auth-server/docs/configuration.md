# Configuration

The configuration should be self-explanatory. Just have a look at the
[default configuration file](https://github.com/osiam/auth-server/blob/master/src/main/deploy/auth-server.properties).

Create the file `/etc/osiam/auth-server.properties` with content based on this example:

```
# Database properties
org.osiam.resource-server.db.vendor=postgresql
org.osiam.auth-server.db.driver=org.postgresql.Driver
org.osiam.auth-server.db.dialect=org.hibernate.dialect.PostgresPlusDialect
org.osiam.auth-server.db.url=jdbc:postgresql://localhost:5432/ong
org.osiam.auth-server.db.username=ong
org.osiam.auth-server.db.password=<YOUR_PASSWORD>

# OSIAM authentication-server configuration
# Home URL (needed for self reference)
org.osiam.auth-server.home=http://localhost:8080/osiam-auth-server

# OSIAM resource server configuration
org.osiam.resource-server.home=http://localhost:8080/osiam-resource-server
# ATTENTION: you have to set a random secret for the resource server client!
# It has to be the same as in the properties file of the resource server!
org.osiam.resource-server.client.secret=secret

# LDAP config for auth server
org.osiam.auth-server.ldap.enabled=true
org.osiam.auth-server.ldap.server.url=ldap://localhost:33389/dc=springframework,dc=org
org.osiam.auth-server.ldap.dn.patterns=uid={0},ou=people
org.osiam.auth-server.ldap.sync-user-data=true
org.osiam.auth-server.ldap.mapping=userName:uid\
,email:mail\
,name.familyName:sn\
,name.givenName:givenName\
,displayName:displayName\
```

To provide the html template, css and message property files, you have to copy the 'auth-server' folder, which is in
the [src/main/deploy](https://github.com/osiam/auth-server/blob/master/src/main/deploy) folder of this project

To customize these files and add the 'Authorization Code Grant' to your application, please have a look at [this](#login-with-authorization-code-grant) section.

If you like to customize the login page, just follow the instruction. If you like to use LDAP just jump [below](#ldap-login).

## Login with Authorization Code Grant

Before you configure the auth server like in the next steps explained, be sure you read all the steps above. Especially your client information are required.
 
Copy the auth-server folder in the /etc/osiam (or the folder you choose as a shared classpath in tomcat), like already explained. Now you could start the auth-server and have access to the login page:

`https://<OSIAMHOST>/osiam-auth-server/login`

This page will be shown if you redirect your users to the following link and they are not logged in:

`https://<OSIAMHOST>/osiam-auth-server/oauth/authorize?client_id=<CLIENT_ID>&response_type=code&redirect_uri=<CLIENT_REDIRECT_URI>&scope=<SCOPES>`

The attributes are explained [here](api_documentation.md#authorization-request) and the detailed flow of this grant [here](api_documentation.md#authorization-code-grant).

If the user who redirect to the **/oauth/authorize** is not logged in, he will be redirect to the login page of the OSIAM auth-server. The not logged in user will be land on this login page: /osiam-auth-server/login which is made with the template engine [thymeleaf](http://www.thymeleaf.org/) and the [bootstrap css framework](http://getbootstrap.com/).

If you like to customize the files just edit them or create your own. Just have a look at the folder [/auth-server/templates/web](https://github.com/osiam/server/tree/master/auth-server/src/main/deploy/auth-server/templates/web). There are three HTML files, which will be rendered for the specific case.

The [login.html](https://github.com/osiam/server/blob/master/auth-server/src/main/deploy/auth-server/templates/web/login.html) shows the two import fields to log in as a user: username and password. Also an login provider pick list will be shown when the LDAP login is activated (described below).

If the login failed, an error will be shown above the form, but only if the attribute 'loginError' is true. The message 'login.error' comes from the language message property file. You can find the message key in the [/auth-server/l10n/login.properties](https://github.com/osiam/server/blob/master/auth-server/src/main/deploy/auth-server/l10n/login.properties). This file is for the standard language english. In the login.html you have access to all message keys with the thymeleaf expression language '#{message.key}', which you specify in the message files. You can write your own messages in the files or also support new languages by create e.g. login_es_ES.properties. 

I you like to enable the LDAP login you have to set the correct [configuration](#ldap-login). 

If you create your own login.html file, just be sure that you set the correct action in the form (/osiam-auth-server/login/check with a POST) and send the two parameters 'username' and 'password'. A third parameter 'provider' with the value 'ldap' can be send if you like to use the LDAP login. This could be like in the default login.html template done with a dropdown list or you can set a hidden field: 

`<input type="hidden" name="provider value="ldap"/>`

If you don't set this field the default selection will be "internal" for the internal authentication. 

The [access_confirmation.html](https://github.com/osiam/server/blob/master/auth-server/src/main/deploy/auth-server/templates/web/access_confirmation.html) will be rendered if the user should give your application the access to his data (mostly his access token). This page only show two buttons. One for authorize and the other to deny the access to his data by your application. If you create your own page, please be sure to send the decision of the user to the endpoint /osiam-auth-server/oauth/authorize as POST with the parameter 'user_oauth_approval' which can be true or false. If there is any internal error and the user comes to this page, it will be shown an error which has access to the current client object: 

`<p th:text="#{confirmation.request.description(${client.clientId})}">`

In this case the message key 'confirmation.request.description' is called like a method with the parameter '${client.clientId}'. This is just a replacement of the first parameter '{0}' with id of the client in the message key:

`confirmation.request.description=You hereby authorize {0} to access your protected resources.`

If you like to add more information, you have access to the whole client object!

The last template file [oauth_error.html](https://github.com/osiam/server/blob/master/auth-server/src/main/deploy/auth-server/templates/web/oauth_error.html) you can modify or replace, just shown a unspecific error to the user if any internal error appears. You can show your own message if you change the message of the key 'oauth.error.message'

So if the user comes from your application and logged in correct, he will redirect to the /osiam-auth-server/oauth/confirm_access page if the implicit approval is set to false where he could confirm the access to his datas or if implicit approval set to true he will directly redirect back to your application.

## LDAP Login

For detailed information about LDAP please look [here](http://www.zytrax.com/books/ldap/).

**PRECONDITION**

You have to configure the auth server like illustrated in the [chapter](#login-with-authorization-code-grant) before!

First you have to set the LDAP configuration in the [auth-server.properties](https://github.com/osiam/auth-server/blob/master/src/main/deploy/auth-server.properties).

The property fields are:

##### org.osiam.auth.ldap.enabled

Default = false

Set to true if you want to enable LDAP login besides the normal OSIAM login. If LDAP is enabled a drop down list is shown in the login page to choose between the internal login and the LDAP login.

##### org.osiam.auth.ldap.server.url

The url of you LDAP server:

```
<protocol>://<host>:<port>/<root>
```

Example: ldap://localhost:33389/dc=example,dc=org

##### org.osiam.auth.ldap.dn.patterns

search pattern to find the user who wants to login

Example: uid={0},ou=people

##### org.osiam.auth.ldap.sync.user.data

Default = true

If set to true the data from LDAP will be copied into the OSIAM resource server at every login. Additional data saved at the user won't be overridden.

If set to false the user data will only be copied with the first login.

##### org.osiam.auth.ldap.mapping

The given Attributes will be copied from the LDAP user into the OSIAM resource server. The attributes are always an pair of ```<scim attribute>:<ldap attribute>```. At the start of the auth server this configuration will be checked and an exception will be thrown if not all 'scim attributes' can be recognized.

Example: 
```
userName:uid\
,email:mail\
,name.familyName:sn\
,displayName:displayName
```

The userName always will be copied with the ldap attribute 'uid'. If in the ldap user the userName has an other attribtue value it can also be set.

The allowed scim fields are

```
userName
displayName
email
entitlement
externalId
im
locale
nickName
phoneNumber
photo
preferredLanguage
profileUrl
role
timezone
title
userType
x509Certificate
address.country
address.formatted
address.locality
address.postalCode
address.region
address.streetAddress
name.familyName
name.formatted
name.givenName
name.honorificPrefix
name.honorificSuffix
name.middleName
```

At a re-login from a ldap user the set attribute will update the scim user attributes. Attributes that are not configured will not be updated and stay the same.

## Next Steps

[Deploy](deployment.md) the auth-server.
