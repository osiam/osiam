# Installation and configuration

This document describes how to install and configure OSIAM.

If you are looking into rolling out a number of OSIAM installations our [Puppet project](https://github.com/osiam/osiam-puppet) is going to make things a lot easier.

For convenience reasons we will provide OSIAM in the near future also packaged as .deb or .rpm.


Chapters:
- [Requirements](#requirements)
- [The operating system](#the-operating-system)
- [Database setup](#database-setup)
- [Default setup](#default-setup)
- [Customized setup](#customized-setup)
- [Configuring Scim Extension](#configuring-scim-extension)
- [Configuring OSIAM](#configuring-osiam)
- [Deployment into the application server](#deployment-into-the-application-server)
- [Next Steps](#next-steps)
- [Configuring Login with authorization code grant](#configuring-login-with-authorization-code-grant)
- [Configuring LDAP Login](#configuring-ldap-login)


## Requirements

This document takes the following prerequisites as basis:
<table>
<tr><td>Category</td><td>Prerequisite</td><td>Comment</td></tr>
<tr><td>Operating Systems</td><td>Debian Wheezy, Ubuntu 12.04 and later, Fedora 18 and later</td><td>Debian Wheezy recommended. Install with "Standard system utilities" and "SSH server".</td></tr>
<tr><td>Runtime Environment</td><td>Java 7, OpenJDK 7 JRE</td><td>apt-get install openjdk-7-jre</td></tr>
<tr><td>Application Server</td><td>Tomcat 7</td><td>apt-get install tomcat7</td></tr>
<tr><td>Database</td><td>PostgreSQL 9.1</td><td>apt-get install postgresql-9.1</td></tr>
</table>
All of the items are freshly installed. If you use a minimum Debian installation you may want to install additional packages that are required for the described process:

    # apt-get install unzip sudo curl

If you have an existing and older installation, you are using different version or a different OS, AppServer or DB this document may not match your situation. 

## The operating system

For the installation of OSIAM you can work with one database for the auth- and the resource-server or each service can have his own database.

For this document we are working with the user "osiam". In principle you can work with your own non privileged user to follow the steps in this document.

Verify the application server and database server are up and running after the installation of the requirements above. When looking at the process list:

    $ ps aux 

You should be able to find some processes running under the postgres user and at least one running under the tomcat7 user:

    tomcat7  18461  0.2  1.5 373412 65468 ?        Sl   10:52   0:16 /usr/lib/jvm/java-7-openjdk-i386/bin/java [..]
    postgres 19926  0.0  0.1  47188  7480 ?        S    10:54   0:01 /usr/lib/postgresql/9.1/bin/postgres [..]
    postgres 19928  0.0  0.0  47188  1528 ?        Ss   10:54   0:01 postgres: writer process
    postgres 19929  0.0  0.0  47188  1284 ?        Ss   10:54   0:01 postgres: wal writer process
    postgres 19930  0.0  0.0  47620  2372 ?        Ss   10:54   0:00 postgres: autovacuum launcher process
    postgres 19931  0.0  0.0  17388  1344 ?        Ss   10:54   0:00 postgres: stats collector process

We recommend to choose the latest OSIAM release version. You can easily download the distribution as .zip or .tar.gz file here:
* Release Repository: http://maven-repo.evolvis.org/releases/org/osiam/osiam-distribution

This include the resource-server, auth-server, addon-self-administration and addon-administration, every project has
it's own folder with the configuration and sql files.

If you just want the use the server components, then download the server distribution without the addons here:
* Release Repository: http://maven-repo.evolvis.org/releases/org/osiam/osiam-server-distribution

Both distributions have in common, that the included projects are packed as .war files and a configuration folder.
Before you start, you have to copy the files and folders inside the configuration folder to the shared loader of your
application server, like described [here](#deployment-into-the-application-server).

You have to setup your database, like described [here](#database-setup). Please check also the migration files,
if you already installed OSIAM before.

* GitHub Release Tags: https://github.com/osiam/server/releases

You could also download the OSIAM .war files for auth-server and resource-server

    $ wget http://maven-repo.evolvis.org/releases/org/osiam/osiam-auth-server/<VERSION>/osiam-auth-server-<VERSION>.war

    $ wget http://maven-repo.evolvis.org/releases/org/osiam/osiam-resource-server/<VERSION>/osiam-resource-server-<VERSION>.war

and the OSIAM sources

    $ wget http://maven-repo.evolvis.org/releases/org/osiam/osiam-auth-server/<VERSION>/osiam-auth-server-<VERSION>-sources.jar

    $ wget http://maven-repo.evolvis.org/releases/org/osiam/osiam-resource-server/<VERSION>/osiam-resource-server-<VERSION>-sources.jar

and unpack the sources

    $ unzip osiam-auth-server-<VERSION>-sources.jar

    $ unzip osiam-resource-server-<VERSION>-sources.jar

## Database setup

**NOTE:** This section describes setting up the database using PostgreSQL. If you want to use MySQL, then check this [page](Create-and-initialize-MySQL-Database-for-OSIAM-server.md).

This database need to be setup before you deploy the server applications!

First add the user ong and the database ong to the PostgreSQL database.

Start the database commandline:

    $ sudo -u postgres psql

and run the following commands:

    postgres=# CREATE USER ong WITH PASSWORD '<YOURPASSWORD>';
    postgres=# CREATE DATABASE ong;
    postgres=# GRANT ALL PRIVILEGES ON DATABASE ong TO ong;
    postgres=# \q

to create the user and the database, granting the user full access to the database and finally quitting the database commandline.

If your Linux user is not 'ong' you may get an error message like

    psql: FATAL:  Peer authentication failed for user "ong"

In that case you must change the local authentication method for postgreSQL in

    /etc/postgresql/9.1/main/pg_hba.conf

Find the line 

    local   all             all                                     peer

and replace the "peer" with "md5". Afterwards restart the database and rerun the import commands from above:

    $ /etc/init.d/postgresql restart

## Default setup
The resource-server creates on startup the following user

<table>
<tr><td>Username</td><td>admin</td></tr>
<tr><td>Password</td><td>koala</td></tr>
</table>

The auth-server sets a default client in the database for the new OSIAM instance.

<table>
<tr><td>Client ID</td><td>example client</td></tr>
<tr><td>Client secret</td><td>secret</td></tr>
<tr><td>Client redirect URI</td><td>http://localhost:5000/oauth2</td></tr>
<tr><td>Client scopes</td><td>ADMIN</td></tr>
</table>

The base64 encoded value for the default client's ID and secret is `ZXhhbXBsZS1jbGllbnQ6c2VjcmV0`

## Configuring Scim Extension
At the moment you can register an extension and all users will have this additional self-defined fields.
There are no pure scim core schema users anymore. The additional fields could be empty, so it will be
optional for the users to fill them out with values.

To add some self-defined fields your previously have to define an extension with an URN. This URN must be unique.
Here is an example of such an insert for postgres:

`INSERT INTO scim_extension VALUES (<UNIQUE_EXTENSION_ID>, <UNIQUE_URN>)`

**NOTE:** You may leave the UNIQUE_EXTENSION_ID off and let your database generate a unique id.

for example:

`INSERT INTO scim_extension VALUES (39, 'urn:org.osiam.extensions:Test:1.0')`

For adding the fields, you have to reference the previously added extension. You need to execute an insert something like this for postgres:

`INSERT INTO scim_extension_field VALUES(<UNIQUE_EXT_FIELD_ID>, <REQUIRED - currently only false>, <SELFDEFINED_FIELD_NAME>, <TYPE>, <UNIQUE_EXTENSION_ID>)`

**NOTE:** You may leave the UNIQUE_EXT_FIELD_ID off and let your database generate a unique id.

for example:

`INSERT INTO scim_extension_field VALUES(213, false, 'gender', 'STRING', 39)`

The actual supported types are 

```
STRING
BOOLEAN
BINARY
DATE_TIME 
DECIMAL (BigDecimal)
INTEGER (BigInteger)
REFERENCE (URI)
```

## Configuring OSIAM

OSIAM needs to be instructed on how to connect to the database and some additional information. Create the file  `/etc/osiam/resource-server.properties` with content based on this example:

```
# Database properties
org.osiam.resource-server.db.vendor=postgresql
org.osiam.resource-server.db.driver=org.postgresql.Driver
org.osiam.resource-server.db.dialect=org.hibernate.dialect.PostgresPlusDialect
org.osiam.resource-server.db.url=jdbc:postgresql://localhost:5432/ong
org.osiam.resource-server.db.username=ong
org.osiam.resource-server.db.password=<YOUR_PASSWORD>

# OSIAM resource server configuration
org.osiam.resource-server.profiling=false

# Home URL (needed for self reference)
org.osiam.resource-server.home=http://localhost:8080/osiam-resource-server
# ATTENTION: you have to set a random secret for the resource server client!
#org.osiam.resource-server.client.secret=

# OSIAM auth server configuration
org.osiam.auth-server.home=http://localhost:8080/osiam-auth-server
```

The example properties file is also on [GitHub](https://github.com/osiam/server/blob/master/resource-server/src/main/deploy/resource-server.properties)

You also have to create the file `/etc/osiam/auth-server.properties` with content based on this example:

```
# Database properties
org.osiam.auth-server.db.vendor=postgresql
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
the [src/main/deploy](https://github.com/osiam/server/blob/master/auth-server/src/main/deploy) folder of the auth-server project

To customize these files and add the 'Authorization Code Grant' to your application, please have a look at [this](#configuring-login-with-authorization-code-grant) section.

## Deployment into the application server

To deploy OSIAM in the previously installed Tomcat the downloaded .war files need to be renamed and moved into Tomcat's webapp directory:

    $ sudo mv osiam-auth-server-<VERSION>.war /var/lib/tomcat7/webapps/osiam-auth-server.war
    $ sudo mv osiam-resource-server-<VERSION>.war /var/lib/tomcat7/webapps/osiam-resource-server.war

Edit the file ``/etc/tomcat7/catalina.properties``. Add to the parameter ``shared.loader`` the complete path of the directory where the file ``osiam.properties`` was put earlier, e.g.

    shared.loader=/var/lib/tomcat7/shared/classes,/var/lib/tomcat7/shared/*.jar,/etc/osiam

Edit the file ``/etc/default/tomcat7`` and change the size of the heap space allocated for Tomcat by modifying the following line 

    JAVA_OPTS="-Djava.awt.headless=true -Xmx128m -XX:+UseConcMarkSweepGC"

to 

    JAVA_OPTS="-Djava.awt.headless=true -Xms512m -Xmx1024m -XX:+UseConcMarkSweepGC"

And now restart Tomcat:

    $ sudo /etc/init.d/tomcat7 restart

You should see the following output:

    [ ok ] Stopping Tomcat servlet engine: tomcat7. 
    [ ok ] Starting Tomcat servlet engine: tomcat7.

## Next Steps

### Testing OSIAM's availablility

Now you can check from the commandline if OSIAM started correctly using the following command:

    $ curl http://localhost:8080/osiam-resource-server/ServiceProviderConfigs

Everything is fine when you see a JSON string as response that looks like this:

    {"schemas":["urn:scim:schemas:core:1.0"],"patch":{"supported":true},"bulk":{"supported":false},"filter":{"supported":true,"maxResults":100},"changePassword":{"supported":false},"sort":{"supported":true},"etag":{"supported":false},"xmlDataFormat":{"supported":false},"authenticationSchemes":{"authenticationSchemes":[{"name":"Oauth2 Bearer","description":"OAuth2 Bearer access token is used for authorization.","specUrl":"http://tools.ietf.org/html/rfc6749","documentationUrl":"http://oauth.net/2/"}]}}r``

### Customized Setup

[Earlier](#default-setup) you saw what the default client and user credentials are. Of course you should use your own client and user credentials. 

Here is an example of how to replace to default setup:

You need an access token in order to send commands to the server. So this is how you get it.

    $ curl -H "Authorization: Basic ZXhhbXBsZS1jbGllbnQ6c2VjcmV0" -X POST -d "grant_type=client_credentials&scope=ADMIN" http://localhost:8080/osiam-auth-server/oauth/token

You will get an answer like:

    {"access_token":“<YOUR_ACCESS_TOKEN>","token_type":"bearer","expires_in":716,"scope":"ADMIN"}

Take the value from _access_token_ (shown in the example as <YOUR_ACCESS_TOKEN>) and put it in an environment variable.

    $ YOUR_ACCESS_TOKEN=<YOUR_ACCESS_TOKEN>

Add your client - see also [Parameters for clients](api_documentation.md#client-configuration)

    curl -i -H "Accept:  application/json" -H "Content-type:  application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" -X POST localhost:8080/osiam-auth-server/Client -d '{"id": "<YOUR_CLIENT_ID>", "accessTokenValiditySeconds": "9999", "refreshTokenValiditySeconds": "9999", "redirectUri": "<YOUR_REDIRECT_URI>", "scope": ["ADMIN", "ME"], "validityInSeconds": "9999", "implicit": "false", "grants": ["authorization_code", "client_credentials", "refreshtoken"]}'

The client's secret will be autogenerated and can only be read from the database directly at the moment. 

    => select id, client_secret from osiam_client;

Delete default client

    curl -i -H "Accept:  application/json" -H "Content-type:  application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" -X DELETE localhost:8080/osiam-auth-server/Client/example-client

Get the default user‘s ID

    curl -i -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" -X GET ‘localhost:8080/osiam-resource-server/Users?access_token=$YOUR_ACCESSTOKEN&filter=userName%20eq%20"admin"'

Extract the ID of the requested (default) user and use it in the following request to update the user with your preferred user credentials:

    curl -i -H "Accept:  application/json" -H "Content-type:  application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" -X PUT localhost:8080/osiam-resource-server/Users/<DEFAULT_USERS_ID> -d '{"schemas":["urn:scim:schemas:core:2.0:User"], "externalId":"","userName":"<YOUR_USERNAME>","password":"<YOUR_USERS_PASSWORD>"}' 

### SSL in Tomcat

SCIM and OAuth 2.0 are standards that require SSL to be enabled. An exhaustive explanation how to setup SSL for HTTP connections in Tomcat 7 can be found [here](http://tomcat.apache.org/tomcat-7.0-doc/ssl-howto.html). 

### Next steps

Now you are ready to work with OSIAM, either using a [connector](README.md#connector-based-integration) or directly the [API](api_documentation.md#http-api-documentation) itself.

## Configuring Login with Authorization Code Grant

Before you configure the auth server like in the next steps explained, be sure you read all the steps above. Especially your client information are required.
 
Copy the auth-server folder in the /etc/osiam (or the folder you choose as a shared classpath in tomcat), like [here](#configuring-osiam) explained. Now you could start the auth-server and have access to the login page:

`https://<OSIAMHOST>/osiam-auth-server/login`

This page will be shown if you redirect your users to the following link and they are not logged in:

`https://<OSIAMHOST>/osiam-auth-server/oauth/authorize?client_id=<CLIENT_ID>&response_type=code&redirect_uri=<CLIENT_REDIRECT_URI>&scope=<SCOPES>`

The attributes are explained [here](api_documentation.md#authorization-request) and the detailed flow of this grant [here](api_documentation.md#authorization-code-grant).

If the user who redirect to the **/oauth/authorize** is not logged in, he will be redirect to the login page of the OSIAM auth-server. If you configured the server like recommended [here](#deployment-into-the-application-server), the not logged in user will be land on this login page: /osiam-auth-server/login which is made with the template engine [thymeleaf](http://www.thymeleaf.org/) and the [bootstrap css framework](http://getbootstrap.com/).

If you like to customize the files just edit them or create your own. Just have a look at the folder [/auth-server/templates/web](https://github.com/osiam/server/tree/master/auth-server/src/main/deploy/auth-server/templates/web). There are three HTML files, which will be rendered for the specific case.

The [login.html](https://github.com/osiam/server/blob/master/auth-server/src/main/deploy/auth-server/templates/web/login.html) shows the two import fields to log in as a user: username and password. Also an login provider pick list will be shown when the LDAP login is activated (described below).

If the login failed, an error will be shown above the form, but only if the attribute 'loginError' is true. The message 'login.error' comes from the language message property file. You can find the message key in the [/auth-server/l10n/login.properties](https://github.com/osiam/server/blob/master/auth-server/src/main/deploy/auth-server/l10n/login.properties). This file is for the standard language english. In the login.html you have access to all message keys with the thymeleaf expression language '#{message.key}', which you specify in the message files. You can write your own messages in the files or also support new languages by create e.g. login_es_ES.properties. 

I you like to enable the LDAP login you have to set the correct [configuration](#configuring-ldap-login). 

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

## Configuring LDAP Login

For detailed information about LDAP please look [here](http://www.zytrax.com/books/ldap/).

**PRECONDITION**

You have to configure the auth server like illustrated in the [chapter](#configuring-login-with-authorization-code-grant) before!

First you have to set the LDAP configuration in the [auth-server.properties](https://github.com/osiam/server/blob/master/auth-server/src/main/deploy/auth-server.properties) as described [here](#configuring-osiam).

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

At a relogin from a ldap user the set attribute will update the scim user attributes. Attributes that are not configured will not be updated and stay the same.
