# Installation and configuration

This document describes how to install and configure OSIAM.

For convenience reasons we will provide OSIAM in the near future also packaged
as .deb or .rpm.

Please check also the migration files, if you already installed OSIAM before.

**NOTE:** The URLs used in this document are valid for a standalone deployment only.
If you deploy OSIAM into a Servlet container, i.e. Tomcat, you have to append the context root after the hostname, e.g.:
`http://localhost:8080/ServiceProviderConfig` becomes `http://localhost:8080/osiam/ServiceProviderConfig`.
The context root is probably `osiam` by default.


Chapters:

- [Requirements](#requirements)
- [Download OSIAM](#download-osiam)
- [The Home Directory](#the-home-directory)
    - [Initialize the Home Directory from the Command Line](#initialize-the-home-directory-from-the-command-line)
- [Configuration](#configuration)
    - [Enable AJP support](#enable-ajp-support)
- [Initialize the Database from the Command Line](#initialize-the-database-from-the-command-line)
- [Starting OSIAM](#starting-osiam)
    - [Starting as a Standalone Application](#starting-as-a-standalone-application)
    - [Starting in Tomcat](#starting-in-tomcat)
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
    <dd>Java 8, OpenJDK 8 JRE</dd>
    <dt>Application Server</dt>
    <dd>Tomcat 7</dd>
    <dt>Database Server (optional)</dt>
    <dd>PostgreSQL 9.4 or MySQL 5.7</dd>
</dl>

All of the items have to be installed and running.
The default configuration uses a file-based H2 database that is stored in the home directory under the folder `/data`.
For the installation of OSIAM in a production environment, we recommend to use a PostgreSQL or MySQL database.

**WARNING:** SCIM and OAuth 2 are standards that require TLS to be enabled. An
exhaustive explanation how to setup TLS for HTTP connections in Tomcat 7 can be
found here: http://tomcat.apache.org/tomcat-7.0-doc/ssl-howto.html

## Download OSIAM

OSIAM is distributed as a self-contained `.war` file.
You can easily download it from the release page: https://github.com/osiam/osiam/releases.
We recommend to choose the latest release version of OSIAM.

The `.war` file is actually an executable application.
This makes it possible to run it on the command line like any other command.
So, instead of starting it with `java -jar osiam.war` you can just run `./osiam.war`.

**NOTE:** This probably works on UNIX and similar systems (like Linux or OSX) only.
It might work in a cygwin environment, but that has not been thoroughly tested.
Essentially you need Bash, some standard UNIX commands and Java.

You should make the `.war` file executable now:

```sh
chmod +x osiam.war
```

**NOTE:** This is a feature of [Spring Boot](http://projects.spring.io/spring-boot/), the underlying framework OSIAM uses.
It works by prepending a regular Bash script to the `.war` file.
Most applications will just ignore the script and process the `.war` part of the file.
You can find more detailed information about this in the [Spring Boot Reference Guide]
(http://docs.spring.io/spring-boot/docs/current/reference/html/deployment-install.html#deployment-initd-service).
It is recommended to read the script part at the beginning of the `.war` file to fully understand its powers.

## The Home Directory

OSIAM expects the configuration, runtime data and necessary assets in a directory in the file system.
This directory is known as the home directory.
You can set the home directory by various means, e.g. as environment variable, system property
or JNDI attribute under `java:comp/env`.
If no home directory is specified, `$HOME/.osiam` will be used.
If the home directory does not exist, OSIAM will try to create it.
If the directory is empty, it will be initialized on startup.
You should create a new directory for the home directory now, for instance `/var/lib/osiam`.
We will refer to this directory as `OSIAM_HOME` from now on.

### Initialize the Home Directory from the Command Line

Sometimes it can be handy to initialize the home directory without actually starting OSIAM,
especially when using an automated deployment process.
You can do this by running the following command on the command line:

```sh
./osiam.war initHome --osiam.home=/var/lib/osiam
```

**NOTE:** You can also use an environment variable `OSIAM_HOME` set to OSIAM's home directory.

After that, you can change the configuration file and templates to your needs and then start OSIAM.

## Configuration

The configuration file can be found under `OSIAM_HOME/config/osiam.yaml`.
It is the default configuration file that has been copied during initialization.
Change this file to your needs.
It should be self-explanatory.
You should at least change the database connection to point to a PostgreSQL or MySQL server,
if you install OSIAM in a production environment.
You have to restart OSIAM after making changes to this file.

### Enable AJP support

If you want to proxy OSIAM using AJP, you have to enable it explicitly.
To do this, change the configuration property `osiam.ajp.enabled` to `true`.
By default the AJP connector will listen on port `8009` and binds to `127.0.0.1`.
To customize this, change the corresponding configuration properties.

## Initialize the Database from the Command Line

This is an optional step, but if you want to, you can now initialize the database before starting OSIAM.
This might be helpful if you want to create additional data like clients or define extensions.
Currently, the only way to define extensions is via direct database manipulation.
The configuration should be fully created before you can run the command.
To start the initialization run the following command on the command line:

```sh
./osiam.war migrateDb --osiam.home=/var/lib/osiam
```

Of course, you can also use this command to migrate the database when updating OSIAM.

## Starting OSIAM

OSIAM can be started in two different ways: as a standalone application or as a Tomcat deployment.
Both ways are equally well supported, but we recommend to use standalone application deployment.
It is highly recommended to run OSIAM under a distinct service user.
Never run OSIAM under the root user.

### Starting as a Standalone Application

Starting OSIAM this way is super easy:

```sh
./osiam.war --osiam.home=/var/lib/osiam
```

**NOTE:** You have to find a directory, where you can place the `.war` file in.
The simplest solution being to that issue is to use OSIAM's home directory.
We will use `/var/lib/osiam` here, but it is really up to you, where you actually put it.

Remember, that you can also use an environment variable `OSIAM_HOME` to set the home directory.
You should never run OSIAM as root, but create a distinct system user account for it, e.g. `osiam`.

#### systemd

To start OSIAM using systemd, create the file `/etc/systemd/system/osiam.service` using the following example:

```
[Unit]
Description=OSIAM
After=network.target

[Service]
User=osiam
Environment="RUN_ARGS=--osiam.home=/var/lib/osiam"
Environment="JAVA_OPTS=-Djava.awt.headless=true -Xms512m -Xmx1024m -XX:+UseConcMarkSweepGC"
ExecStart=/var/lib/osiam/osiam.war
SuccessExitStatus=143

[Install]
WantedBy=multi-user.target
```

Adjust this files to your needs.
Basic configuration options of the service are set via environment variables.
Important configuration options are:

<dl>
    <dt><code>RUN_ARGS</code></dt>
    <dd>Arguments for OSIAM</dd>
    <dt><code>JAVA_OPTS</code></dt>
    <dd>Arguments for the JVM</dd>
</dl>

**NOTE:** The complete list of options can be found in the [Spring Boot Reference Guide]
(http://docs.spring.io/spring-boot/docs/current/reference/html/deployment-install.html#deployment-script-customization).

From now on you can treat `osiam` like any other systemd service unit on your system.
You can reload systemd, and enable and start OSIAM now:

```sh
systemctl daemon-reload
systemctl enable osiam.service
systemctl start osiam.service
```

Check the journal to see the startup progress:

```sh
journalctl -u osiam.service
```

#### SysVinit

The `.war` file can also act as an init script when linked to `/etc/init.d/`:

```sh
ln -s /var/lib/osiam/osiam.war /etc/init.d/osiam
```

**WARNING:** You really should read the [Spring Boot Reference Guide](http://docs.spring.io/spring-boot/docs/current/reference/html/deployment-install.html#deployment-initd-service-securing)
on how to secure the SysVinit service.

From now on you can treat `osiam` like any other SysVinit service on your system.
The init script understands the following commands: `start`, `stop`, `restart` and `status`.
It also writes a log file to `/var/log/osiam.log` and creates a pid file under `/var/run/osiam/osiam.pid`
to keep track of the process.

**NOTE:** Read the documentation of your OS on how to enable or automatically start the service.

Next, create a file `osiam.conf` and put it in the same directory, where you put the `.war` file.
In this file you can configure basic options of the service.
It should at least contain the following lines:

```
RUN_ARGS="--osiam.home=/var/lib/osiam"
JAVA_OPTS="-Djava.awt.headless=true -Xms512m -Xmx1024m -XX:+UseConcMarkSweepGC"
```

Important configuration options are:

<dl>
    <dt><code>RUN_ARGS</code></dt>
    <dd>Arguments for OSIAM</dd>
    <dt><code>JAVA_OPTS</code></dt>
    <dd>Arguments for the JVM</dd>
    <dt><code>PID_FOLDER</code></dt>
    <dd>Path to the pid file root folder (default: <code>/var/run</code>)</dd>
    <dt><code>LOG_FOLDER</code></dt>
    <dd>Path to the log folder (default: <code>/var/log</code>)</dd>
    <dt><code>LOG_FILENAME</code></dt>
    <dd>Name of the log file (default: <code>osiam.log</code>)</dd>
</dl>

**NOTE:** The complete list of options can be found in the [Spring Boot Reference Guide]
(http://docs.spring.io/spring-boot/docs/current/reference/html/deployment-install.html#deployment-script-customization).

You can start OSIAM now:

```sh
service osiam start
```

Check the log file under `/var/log/osiam.log` to see the startup progress.

### Starting in Tomcat

Before you can deploy OSIAM in Tomcat 7, you have to make some changes to
Tomcat's configuration.
Edit the file `/etc/default/tomcat7` and change the size of the heap space
allocated for Tomcat by modifying the following line

    JAVA_OPTS="-Djava.awt.headless=true -Xmx128m -XX:+UseConcMarkSweepGC"

to

    JAVA_OPTS="-Djava.awt.headless=true -Xms512m -Xmx1024m -XX:+UseConcMarkSweepGC"

Now restart Tomcat:

    $ sudo /etc/init.d/tomcat7 restart

Now you need to set the home directory.
The best way is to add a Tomcat context descriptor with the following content:

```xml
<Context>
    <Environment name="osiam.home" value="OSIAM_HOME" type="java.lang.String"/>
</Context>
```

Replace `OSIAM_HOME` with the directory you chose in [The Home Directory](#the-home-directory).
Put this file under Tomcat's `conf` directory, e.g. `/var/lib/tomcat7/conf/Catalina/localhost/osiam.xml`.
To start OSIAM put the downloaded .war file into Tomcat's `webapp` directory:

    $ sudo mv osiam-<VERSION>.war /var/lib/tomcat7/webapps/osiam.war

Check Tomcat's log files to see the startup progress.
After some seconds, OSIAM should be fully deployed and ready.
You can check from the commandline whether OSIAM is started by using the following command:

    $ curl http://localhost:8080/ServiceProviderConfig

Everything is fine when you see a JSON string as response that looks like this:

    {"schemas":["urn:ietf:params:scim:schemas:core:2.0:User"],"patch":{"supported":false},"bulk":{"supported":false},"filter":{"supported":true,"maxResults":100},"changePassword":{"supported":false},"sort":{"supported":true},"etag":{"supported":false},"xmlDataFormat":{"supported":false},"authenticationSchemes":{"authenticationSchemes":[{"name":"Oauth2 Bearer","description":"OAuth2 Bearer access token is used for authorization.","specUrl":"http://tools.ietf.org/html/rfc6749","documentationUrl":"http://oauth.net/2/"}]}}
    
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

    $ curl -H "Authorization: Basic ZXhhbXBsZS1jbGllbnQ6c2VjcmV0" -X POST -d "grant_type=client_credentials&scope=ADMIN" http://localhost:8080/oauth/token

You will get an answer like:

    {"access_token":“<YOUR_ACCESS_TOKEN>","token_type":"bearer","expires_in":716,"scope":"ADMIN", ...}

Take the value from the field `access_token` (shown in the example as
`<YOUR_ACCESS_TOKEN>`) and store it in an environment variable.

    $ YOUR_ACCESS_TOKEN=<YOUR_ACCESS_TOKEN>

Get the initial admin user's ID

    curl -i -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" -X GET "localhost:8080/Users?filter=userName%20eq%20\"admin\""

Extract the ID of the user and use it in the following request to update the
user with your preferred credentials:

    curl -i -H "Accept:  application/json" -H "Content-type:
    application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" -X PUT
    localhost:8080/Users/<DEFAULT_USERS_ID> -d
    '{"schemas":["urn:ietf:params:scim:schemas:core:2.0:User"], "externalId":"","userName":"<YOUR_USERNAME>","password":"<YOUR_USERS_PASSWORD>"}' 

Add your client (see also [Parameters for clients]
(api_documentation.md#client-configuration)):

    curl -i -H "Accept:  application/json" -H "Content-type:  application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" -X POST localhost:8080/Client -d '{"id": "<YOUR_CLIENT_ID>", "accessTokenValiditySeconds": "9999", "refreshTokenValiditySeconds": "9999", "redirectUri": "<YOUR_REDIRECT_URI>", "scope": ["ADMIN", "ME"], "validityInSeconds": "9999", "implicit": "false", "grants": ["authorization_code", "client_credentials", "refreshtoken"]}'

The client's secret will be autogenerated and can only be read from the database
directly at the moment:

    select id, client_secret from osiam_client;

Delete the default client:

    curl -i -H "Accept:  application/json" -H "Content-type:  application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" -X DELETE localhost:8080/Client/example-client

## Using OSIAM

Now you are ready to work with OSIAM, either using a [connector]
(osiam-connectors.md) or the [API](api_documentation.md) directly.

## Advanced Configuration

### Configuring SCIM Extension

You can extend OSIAM's data model with custom extensions.
This is a feature of [SCIM 2](http://www.simplecloud.info/), the data model and API that OSIAM implements.
Extensions add new attributes to resources using a unique namespace.
These attributes can be mandatory or optional and have a fixed data type.
See [the section about extensions in the SCIM 2 RFC](https://tools.ietf.org/html/rfc7643#section-3.3) for more information.

**NOTE:** OSIAM currently only supports extensions for User objects.
Support for Group objects will be added in a future version.

You can configure extensions in the `osiam.yaml` configuration file.
The following example configuration should help you get started:

```yaml
osiam:

  [...]

  scim:
    extensions:
      - urn: exampleExtension1
        fields:
          - name: requiredStringField
            type: STRING  # Case-insensitive, so can also be 'string' or 'String'
            required: yes # Optional, by default fields are not required to have a value
          - name: integerField
            type: INTEGER
      - urn: exampleExtension2
        fields:
          - name: booleanField
            type: BOOLEAN
```

Supported data types for attributes are:

- `STRING`: A simple string containing some text
- `BOOLEAN`: true or false
- `BINARY`: Base64 encoded raw binary data
- `DATE_TIME`: A string containing an ISO8601 timestamp
- `DECIMAL`: An arbitrary decimal number, e.g. `3.0`
- `INTEGER`: An arbitrary integral number, e.g `30`
- `REFERENCE`: A String containing a URI

**NOTE:** OSIAM does not yet support complex data types for extensions.
The absence of this feature will be addressed in a future version.

See [the section about data types in the SCIM 2 RFC](https://tools.ietf.org/html/rfc7643#section-2.3) for more information.

OSIAM creates all configured extensions on startup, if they do not exist in the database.
It will only check, if an extension with the same URN is already defined.
This means, that adding or changing a field in the configuration file will not change the extension in the database.
If you want to change an extension, follow these steps:

1. Configure a new extension, with a different name, i.e by increasing a version number in the URN.
2. For each user: copy all data of the old extension to the new one.
3. Manually delete the old extension from the database.

Likewise, extensions missing in the configuration file will not be removed from the database.
You have to do this manually.
A more sophisticated extension management might be added in a future version.

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

First you have to set the LDAP configuration in the [osiam.yaml]
(../src/main/deploy/osiam.yaml) as described [here](#configuration).

The available properties are:

**osiam.ldap.enabled**

Default = false

Set to true if you want to enable LDAP login besides the normal OSIAM login. If
LDAP is enabled a drop down list is shown in the login page to choose between
the internal login and the LDAP login.

**osiam.ldap.server-url**

The url of you LDAP server:

    <protocol>://<host>:<port>/<root>

Example: ldap://localhost:33389/dc=example,dc=org

**osiam.ldap.dn-patterns**

search pattern to find the user who wants to login

Example: uid={0},ou=people

**osiam.ldap.sync-user-data**

Default = true

If set to `true` the data from LDAP will be copied to OSIAM at every login.
Additional data saved at the user won't be overridden.

If set to false the user data will only be copied with the first login.

**osiam.ldap.user-mapping**

The given Attributes will be copied from the LDAP user into the OSIAM resource
server.
At the start of OSIAM this configuration will be checked and an
exception will be thrown if not all SCIM attributes can be recognized.

Example: 

```yaml
osiam:
  ...
  ldap:
    ...
    user-mapping:
      userName: uid
      email: mail
      name.familyName: sn
      name.givenName: givenName
      displayName: displayName
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
