# Installation and configuration

This document describes how to install and configure OSIAM.

For convenience reasons we will provide OSIAM in the near future also packaged
as .deb or .rpm.

Please check also the migration files, if you already installed OSIAM before.

Chapters:

- [Requirements](#requirements)
- [Download OSIAM](#download-osiam)
- [Configuration](#configuration)
- [Starting OSIAM](#starting-osiam)
- [Default setup](#default-setup)
- [Customize setup](#customize-setup)
- [Using OSIAM](#using-osiam)
- [Advanced Configuration](#advanced-configuration)
    - [Configuring Scim Extension](#configuring-scim-extension)
    - [Customizing the Login via Authorization Code Grant](#customizing-the-login-via-authorization-code-grant)
    - [Configuring LDAP Login](#configuring-ldap-login)

## Requirements

This document takes the following prerequisites as basis:

<dl>
    <dt>Operating Systems</dt>
    <dd>Any OS that supports running Java applications</dd>
    <dt>Runtime Environment</dt>
    <dd>Java 7, OpenJDK 7 JRE</dd>
    <dt>Application Server</dt>
    <dd>Tomcat 7</dd>
    <dt>Database</dt>
    <dd>PostgreSQL 9.3 or MySQL 5.7</dd>
</dl>

All of the items have to be installed and running. For the installation of OSIAM
you need a PostgreSQL or MySQL database. In this document we are using the database
"osiam" and with the user "osiam". Please make sure that the database and user
exists before going on.

**WARNING:** SCIM and OAuth 2 are standards that require TLS to be enabled. An
exhaustive explanation how to setup TLS for HTTP connections in Tomcat 7 can be
found here: http://tomcat.apache.org/tomcat-7.0-doc/ssl-howto.html

## Download OSIAM

We recommend to choose the latest OSIAM release version.
You can easily download the distribution from the release page: https://github.com/osiam/osiam/releases
Available formats are .zip or .tar.gz.

The distribution includes

* OSIAM server as a .war file
* an example configuration file
* static assets like templates, css and javascript files

After download, please unpack the distribution archive to a location of your choice.

## Configuration

The configuration should be self-explanatory. Just have a look at the
[default configuration file](../src/main/deploy/osiam.properties).

Create the file `/etc/osiam/osiam.properties` with content based on this example:

```
# Home URL (needed for self reference)
org.osiam.home=http://localhost:8080/osiam

#
# Database configuration (PostgreSQL)
#
org.osiam.db.vendor=postgresql
org.osiam.db.driver=org.postgresql.Driver
org.osiam.db.url=jdbc:postgresql://localhost:5432/osiam
org.osiam.db.username=osiam
org.osiam.db.password=<YOUR_PASSWORD>

#
# Database configuration (MySQL)
#
org.osiam.db.vendor=mysql
org.osiam.db.driver=com.mysql.jdbc.Driver
org.osiam.db.url=jdbc:mysql://localhost:3306/osiam
org.osiam.db.username=osiam
org.osiam.db.password=<YOUR_PASSWORD>

#
# LDAP configuration
#

# Enable LDAP integration
org.osiam.ldap.enabled=false

# LDAP URL with search base
#org.osiam.ldap.server.url=ldap://localhost:389/dc=mycorp,dc=org

# DN pattern for users
#org.osiam.ldap.dn.patterns=uid={0},ou=people

# Synchronize data from LDAP to OSIAM on re-login
#org.osiam.ldap.sync-user-data=true

# How SCIM attributes of a user are mapped to LDAP attributes
#org.osiam.ldap.mapping=userName:uid\
#,email:mail\
#,name.familyName:sn\
#,name.givenName:givenName\
#,displayName:displayName

#
# Login lock configuration
#

# Lock after x login failures, 0 = disabled
org.osiam.tempLock.count=10
# Lock for x seconds
org.osiam.tempLock.timeout=30
```

To provide the HTML templates, static assets and message property files, you have to copy some files.
First, find the folder `/configuration/osiam` within the distribution archive.
Then copy the complete folder to `/etc/osiam/` while preserving the directory structure.

## Starting OSIAM

Before you can deploy OSIAM in Tomcat 7, you have to make some changes to
Tomcat's configuration. Edit the file `/etc/tomcat7/catalina.properties`. Add to
the parameter ``shared.loader`` the complete path of the directory where the
file `osiam.properties` has been put earlier, e.g.

    shared.loader=/var/lib/tomcat7/shared/classes,/var/lib/tomcat7/shared/*.jar,/etc/osiam

Edit the file `/etc/default/tomcat7` and change the size of the heap space
allocated for Tomcat by modifying the following line

    JAVA_OPTS="-Djava.awt.headless=true -Xmx128m -XX:+UseConcMarkSweepGC"

to

    JAVA_OPTS="-Djava.awt.headless=true -Xms512m -Xmx1024m -XX:+UseConcMarkSweepGC"

Now restart Tomcat:

    $ sudo /etc/init.d/tomcat7 restart

To start OSIAM with Tomcat, put the downloaded .war files into Tomcat's `webapp`
directory:

    $ sudo mv osiam-<VERSION>.war /var/lib/tomcat7/webapps/osiam.war

Do the same for the addons, if you like to use them. Check the Tomcat's log
files to see the startup progress. After some seconds, OSIAM should be fully
deployed and ready. You can check from the commandline whether OSIAM is started
by using the following command:

    $ curl http://localhost:8080/osiam/ServiceProviderConfigs

Everything is fine when you see a JSON string as response that looks like this:

    {"schemas":["urn:ietf:params:scim:schemas:core:2.0:User"],"patch":{"supported":true},"bulk":{"supported":false},"filter":{"supported":true,"maxResults":100},"changePassword":{"supported":false},"sort":{"supported":true},"etag":{"supported":false},"xmlDataFormat":{"supported":false},"authenticationSchemes":{"authenticationSchemes":[{"name":"Oauth2 Bearer","description":"OAuth2 Bearer access token is used for authorization.","specUrl":"http://tools.ietf.org/html/rfc6749","documentationUrl":"http://oauth.net/2/"}]}}
    
## Default setup

OSIAM creates an initial admin user on startup:

<dl>
    <dt>Username</dt>
    <dd>admin</dd>
    <dt>Password</dt>
    <dd>koala</dd>
</dl>

OSIAM also creates an initial OAuth 2 client:

<dl>
    <dt>Client ID</dt>
    <dd>example client</dd>
    <dt>Client secret</dt>
    <dd>secret</dd>
    <dt>Client redirect URI</dt>
    <dd>http://localhost:5000/oauth2</dd>
    <dt>Client scopes</dt>
    <dd>ADMIN</dd>
</dl>

The base64-encoded value for the default client's ID and secret is `ZXhhbXBsZS1jbGllbnQ6c2VjcmV0`.

## Customize Setup

[Earlier](#default-setup) you saw what the default client and user credentials
are. Of course, you should use your own client and user credentials and not rely
on the default ones. Here is an example of how to change the default setup:

You need an access token in order to send commands to the server. This is how
you get one:

    $ curl -H "Authorization: Basic ZXhhbXBsZS1jbGllbnQ6c2VjcmV0" -X POST -d "grant_type=client_credentials&scope=ADMIN" http://localhost:8080/osiam/oauth/token

You will get an answer like:

    {"access_token":“<YOUR_ACCESS_TOKEN>","token_type":"bearer","expires_in":716,"scope":"ADMIN", ...}

Take the value from the field `access_token` (shown in the example as
`<YOUR_ACCESS_TOKEN>`) and store it in an environment variable.

    $ YOUR_ACCESS_TOKEN=<YOUR_ACCESS_TOKEN>

Get the initial admin user's ID

    curl -i -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" -X GET "localhost:8080/osiam/Users?filter=userName%20eq%20\"admin\""

Extract the ID of the user and use it in the following request to update the
user with your preferred credentials:

    curl -i -H "Accept:  application/json" -H "Content-type:
    application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" -X PUT
    localhost:8080/osiam/Users/<DEFAULT_USERS_ID> -d
    '{"schemas":["urn:ietf:params:scim:schemas:core:2.0:User"], "externalId":"","userName":"<YOUR_USERNAME>","password":"<YOUR_USERS_PASSWORD>"}' 

Add your client (see also [Parameters for clients]
(api_documentation.md#client-configuration)):

    curl -i -H "Accept:  application/json" -H "Content-type:  application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" -X POST localhost:8080/osiam/Client -d '{"id": "<YOUR_CLIENT_ID>", "accessTokenValiditySeconds": "9999", "refreshTokenValiditySeconds": "9999", "redirectUri": "<YOUR_REDIRECT_URI>", "scope": ["ADMIN", "ME"], "validityInSeconds": "9999", "implicit": "false", "grants": ["authorization_code", "client_credentials", "refreshtoken"]}'

The client's secret will be autogenerated and can only be read from the database
directly at the moment:

    select id, client_secret from osiam_client;

Delete the default client:

    curl -i -H "Accept:  application/json" -H "Content-type:  application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" -X DELETE localhost:8080/osiam/Client/example-client

## Using OSIAM

Now you are ready to work with OSIAM, either using a [connector]
(osiam-connectors.md) or the [API](api_documentation.md) directly.

## Advanced Configuration

### Configuring SCIM Extension

At the moment you can register an extension and all users will have this
additional self-defined fields. There are no pure SCIM core schema users
anymore. The additional fields could be empty, so it will be optional for the
users to fill them out with values.

To add some self-defined fields your previously have to define an extension with
a URN. This URN must be unique. Here is an example of such an insert for
postgres:

`INSERT INTO scim_extension VALUES (<UNIQUE_EXTENSION_ID>, <UNIQUE_URN>)`

**NOTE:** You may leave the `UNIQUE_EXTENSION_ID` off and let your database
generate a unique id.

for example:

`INSERT INTO scim_extension VALUES (39, 'urn:org.osiam.extensions:Test:1.0')`

For adding the fields, you have to reference the previously added extension. You
need to execute an insert something like this for postgres:

```sql
INSERT INTO scim_extension_field VALUES(
  <UNIQUE_EXT_FIELD_ID>,
  <REQUIRED - currently only false>,
  <SELFDEFINED_FIELD_NAME>,
  <TYPE>,
  <UNIQUE_EXTENSION_ID>)`
```

**NOTE:** You may leave the UNIQUE_EXT_FIELD_ID off and let your database
generate a unique id, for example:

`INSERT INTO scim_extension_field VALUES(213, false, 'gender', 'STRING', 39)`

The supported types are:

- `STRING`
- `BOOLEAN`
- `BINARY`
- `DATE_TIME` 
- `DECIMAL` (BigDecimal)
- `INTEGER` (BigInteger)
- `REFERENCE` (URI)

### Customizing the Login via Authorization Code Grant

**NOTE:** Before you configure OSIAM like in the next steps explained, be sure
you read all the steps above. Especially your client information are required.

A login page will be shown, if you redirect your users to the following link and
they are not logged in:

    https://localhost/osiam/oauth/authorize?client_id=<CLIENT_ID>&response_type=code&redirect_uri=<CLIENT_REDIRECT_URI>&scope=<SCOPES>

The attributes are explained [here](api_documentation.md#authorization-request)
and the detailed flow of this grant [here]
(api_documentation.md#authorization-code-grant).

The login page is made with the template engine [Thymeleaf]
(http://www.thymeleaf.org/) and the [Bootstrap CSS framework]
(http://getbootstrap.com/). If you like to customize the files, just edit them
or create your own. Just have a look at the folder [osiam/templates/web]
(../src/main/deploy/osiam/templates/web). There are three HTML files, which will
be rendered for the specific case:

The [login.html](../src/main/deploy/osiam/templates/web/login.html) shows the
two import fields to log in as a user: username and password. Also an login
provider pick list will be shown when the LDAP login is activated (described
below).

If the login failed, an error will be shown above the form, but only if the
attribute `loginError` is true. The message `login.error` comes from the
language message property file. You can find the message key in the
[osiam/l10n/login.properties](../src/main/deploy/osiam/l10n/login.properties).
This file is for the fallback language English. In the `login.html` you have
access to all message keys with the Thymeleaf expression language
`#{message.key}`, that you specify in the message files. You can write your own
messages in the files or also support new languages by create, e.g.
`login_es_ES.properties`. 

If you like to enable the LDAP login you have to set the correct [configuration]
(#configuring-ldap-login). If you create your own `login.html` file, just be
sure that you set the correct method and action in the form, namely `POST` and
`/osiam/login/check`, and send the two parameters `username` and
`password`. A third parameter `provider` with the value `ldap` can be send if
you like to use the LDAP login. This could be like in the default login.html
template done with a dropdown list or you can set a hidden field: 

    <input type="hidden" name="provider value="ldap"/>

If you don't set this field the default selection will be `internal` for the
internal authentication. 

The template file `[access_confirmation.html]
(../src/main/deploy/osiam/templates/web/access_confirmation.html)` will be
rendered, if the user should give your application the access to their data
(mostly their access token). This page only show two buttons. One for authorize
and the other to deny the access to his data by your application. If you create
your own `access_confirmation.html` file, just be sure that you set the correct
method and action in the form, namely `POST` and
`/osiam/oauth/authorize`, and send the the parameter
`user_oauth_approval` which can be either `true` or `false`. If there is any
internal error and the user comes to this page, it will be shown an error which
has access to the current client object: 

    <p th:text="#{confirmation.request.description(${client.clientId})}">

In this case the message key `confirmation.request.description` is called like a
method with the parameter `${client.clientId}`. This is just a replacement of
the first parameter `{0}` with id of the client in the message key, e.g.:

    confirmation.request.description=You hereby authorize {0} to access your protected resources.

If you like to add more information, you have access to the whole client object.

The last template file [oauth_error.html]
(../src/main/deploy/osiam/templates/web/oauth_error.html) you can modify or
replace, is just rendering an unspecific error to the user if any internal error
appears. You can show your own message if you change the message of the key
`oauth.error.message`.

### Configuring LDAP Login

**NOTE:** LDAP authentication currently only works with the Authorization Code
grant of OAuth 2.

First you have to set the LDAP configuration in the [osiam.properties]
(../src/main/deploy/osiam.properties) as described [here](#configuration).

The available properties are:

**org.osiam.auth.ldap.enabled**

Default = false

Set to true if you want to enable LDAP login besides the normal OSIAM login. If
LDAP is enabled a drop down list is shown in the login page to choose between
the internal login and the LDAP login.

**org.osiam.auth.ldap.server.url**

The url of you LDAP server:

    <protocol>://<host>:<port>/<root>

Example: ldap://localhost:33389/dc=example,dc=org

**org.osiam.auth.ldap.dn.patterns**

search pattern to find the user who wants to login

Example: uid={0},ou=people

**org.osiam.auth.ldap.sync.user.data**

Default = true

If set to `true` the data from LDAP will be copied to OSIAM at every login.
Additional data saved at the user won't be overridden.

If set to false the user data will only be copied with the first login.

**org.osiam.auth.ldap.mapping**

The given Attributes will be copied from the LDAP user into the OSIAM resource
server. The attributes are always an pair of

    <scim attribute>:<ldap attribute>

At the start of OSIAM this configuration will be checked and an
exception will be thrown if not all SCIM attributes can be recognized.

Example: 

```
userName:uid\
,email:mail\
,name.familyName:sn\
,displayName:displayName
```

The userName always will be copied with the LDAP attribute 'uid'. If in the LDAP
user the userName has an other attribute value it can also be set.

The available SCIM attributes are:

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

At a re-login from a LDAP user the set attribute will update the SCIM user
attributes. Attributes that are not configured will not be updated and stay the
same.
