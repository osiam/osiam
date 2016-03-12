# Migration Notes

## From 2.5 to 3.0

In this release, the auth-server and resource-server have been merged into a single application.
Therefore you need to merge the databases and configuration files too.
Also, the configuration file format is now YAML and deployment has changed.
This document will guide you through the necessary steps to update OSIAM to version 3.0.

Prerequisites:

- You are using the latest version of OSIAM 2.x
- You are using the latest version of the add-ons (if applicable)
- Your Java Applications are using at least version 1.8 of the Connector4Java.

**NOTE:** It is highly recommended to try the migration on a staging system, before updating a production system.
You should thoroughly test your complete staging system.
And, of course, always make a backup of all the data before updating a production system!

### Update Your JVM

OSIAM now requires at least Java 8.
Update Java on your machines, if you did not do it already.
OSIAM runs with both Oracle Java 8 and OpenJDK 8.

### Create OSIAM's Home Directory

OSIAM now loads assets and configuration from a directory in the filesystem.
Create this directory now, we will refer to it as OSIAM's home directory from now on:

```sh
java -jar osiam.war initHome --osiam.home=/var/lib/osiam
```

You can, of course, choose any other directory instead of `/var/lib/osiam`.
Make sure that the user, who runs the command, can create or write the directory.

If you have not changed any files under `/etc/osiam` (except for the configuration file),
there is nothing else to do.
If you have changed any files under `/etc/osiam` (except for the configuration file),
you have to manually copy your existing configuration to the new home directory:

```sh
rm -rf /var/lib/osiam/i18n/*
cp -r /etc/osiam/auth-server/i18n/* /var/lib/osiam/i18n/

rm -rf /var/lib/osiam/css/*
cp -r /etc/osiam/auth-server/resources/css/* /var/lib/osiam/css/

rm -rf /var/lib/osiam/js/*
cp -r /etc/osiam/auth-server/resources/js/* /var/lib/osiam/js/

rm -rf /var/lib/osiam/templates/*
cp -r /etc/osiam/auth-server/templates/* /var/lib/osiam/templates/
```

### Create a new Configuration File

The new configuration file can be found under `$OSIAM_HOME/config/osiam.yaml`.
Read it carefully, as this file contains an explanation of every setting.
Open the old configuration files and compare the settings to the new one.
First set the the connection properties to the new database under `osiam.db`.
Merge the connection pool properties:

- `org.osiam.auth-server.db.maximum-pool-size` and `org.osiam.resource-server.db.maximum-pool-size`
  become `osiam.db.maximum-pool-size`
- `org.osiam.auth-server.db.connection-timeout-ms` `org.osiam.resource-server.db.connection-timeout-ms`
  become `osiam.db.connection-timeout-ms`

Migrate the LDAP configuration:

```
org.osiam.auth-server.ldap.enabled=true
org.osiam.auth-server.ldap.server.url=ldap://ldap:389/dc=osiam,dc=org
org.osiam.auth-server.ldap.dn.patterns=uid={0},ou=people
org.osiam.auth-server.ldap.sync-user-data=true
org.osiam.auth-server.ldap.mapping=userName:uid\
,email:mail\
,name.familyName:sn\
,name.givenName:givenName\
,displayName:displayName\
```

becomes

```yaml
osiam:

  [...]

  ldap:
    enabled: true
    server-url: ldap://ldap:389/dc=osiam,dc=org
    dn-patterns:
      - uid={0},ou=people
    sync-user-data: true
    user-mapping:
      userName: uid
      email: mail
      name.familyName: sn
      name.givenName: givenName
      displayName: displayName
``

Migrate the temporary lock configuration:

```
org.osiam.auth-server.tempLock.count=10
org.osiam.auth-server.tempLock.timeout=30
```

becomes

```yaml
osiam:

  [...]

  tempLock:
    count: 10
    timeout: 30
```

The following settings can be ignored, as they have been removed:

- `org.osiam.*-server.home`
- `org.osiam.*-server.connector.max-connections`
- `org.osiam.*-server.connector.read-timeout-ms`
- `org.osiam.*-server.connector.connect-timeout-ms`

### Choose a Way of Deployment

You probably deployed OSIAM in a Tomcat Servlet container before this update.
This is still supported, but now there also is the option to run OSIAM as a standalone application.
Think about the ways of deployment for a minute and read the [documentation]
(detailed-reference-installation.md#starting-osiam) on this.
If you decide to run OSIAM as a standalone application from now on, follow the instructions in the documentation.

**Note:** The distribution artifacts have been dropped.
Everything you need to install OSIAM is now contained in `osiam.war`.

### Merge the Databases

If you are using two separated databases, you will have to merge them now into one database.
You can actually do this without updating OSIAM afterwards, as OSIAM 2.x already supported a single database.
Verify that everything is still working after the merge.

It is recommended to stop OSIAM now and clone the old database, as it makes rollback easier.
Remove the old schema migration tables from the new database:

- `auth_server_schema_version`
- `resource_server_schema_version`

They will be replaced by a single schema migration table.
It is recommended, but not necessary, to run the database migrations now:

```sh
java -jar osiam.war migrateDb --osiam.home=/var/lib/osiam
```
**NOTE:** Make a backup of all the data before running this on a production database!

### Change Tomcat's Configuration

If you still want to use the deployment with Tomcat, you have to set the home directory.
The best way to do this, is to add a Tomcat context descriptor with the following content:

```xml
<Context>
    <Environment name="osiam.home" value="/var/lib/osiam" type="java.lang.String"/>
</Context>
```

Put this file under Tomcat's `conf` directory, e.g. `/var/lib/tomcat7/conf/Catalina/localhost/osiam.xml`.
Replace `/var/lib/osiam` with the home directory you chose before.

### Start OSIAM

Now, start OSIAM and verify that it is basically working:

```sh
# For standalone deployments
curl http://localhost:8080/ServiceProviderConfig

# For Tomcat deployments
curl http://localhost:8080/osiam/ServiceProviderConfig
```

If OSIAM is running correctly, you will get the following (or similar) response:

```json
{"schemas":["urn:ietf:params:scim:schemas:core:2.0:User"],"patch":{"supported":true},"bulk":{"supported":false},"filter":{"supported":true,"maxResults":100},"changePassword":{"supported":false},"sort":{"supported":true},"etag":{"supported":false},"xmlDataFormat":{"supported":false},"authenticationSchemes":{"authenticationSchemes":[{"name":"Oauth2 Bearer","description":"OAuth2 Bearer access token is used for authorization.","specUrl":"http://tools.ietf.org/html/rfc6749","documentationUrl":"http://oauth.net/2/"}]}}
```

### Clean Up

Remove the old deployments from Tomcat:

```sh
rm -f /var/lib/tomcat7/webapps/osiam-auth-server.war
rm -rf /var/lib/tomcat7/webapps/osiam-auth-server/
rm -f /var/lib/tomcat7/webapps/osiam-resource-server.war
rm -rf /var/lib/tomcat7/webapps/osiam-resource-server/
```

Remove the old configuration files from `/etc/osiam`:

```sh
rm -rf /etc/osiam/auth-server*
rm -rf /etc/osiam/resource-server*
```

If you don't use the add-ons, edit the Tomcat properties file, e.g. `/etc/tomcat7/catalina.properties`.
Remove the value `/etc/osiam` from the parameter `shared.loader` and delete the directory `/etc/osiam`.
Finally, remove the old database.

### Configure the Add-Ons

To connect to OSIAM 3.0, the configuration of the add-ons has to be changed.
Edit the file `/etc/osiam/addon-administration.properties` and remove the properties:

- `org.osiam.authServerEndpoint`
- `org.osiam.resourceServerEndpoint`

Add the property `org.osiam.endpoint` and set it to the base URL of OSIAM, e.g. 

```
# Standalone deployment
org.osiam.endpoint=http://localhost:8080/

# Tomcat deployment
org.osiam.endpoint=http://localhost:8080/osiam
```

Next, edit the file `/etc/osiam/addon-self-administration.properties` and remove the properties:

- `org.osiam.auth-server.home`
- `org.osiam.resource-server.home`

Add the property `org.osiam.home` and set it to the base URL of OSIAM, e.g. 

```
# Standalone deployment
org.osiam.home=http://localhost:8080/

# Tomcat deployment
org.osiam.home=http://localhost:8080/osiam
```

### Update your Applications

#### Update Scopes

This release removes support of the deprecated HTTP method based scopes.
They are replaced with the new, functionally-motivated scopes `ADMIN` and `ME`.
Before updating OSIAM, you should update your existing clients:

1. Examine the current scopes and usages of the client.
2. Should access tokens granted to this client have full administrative access to OSIAM?
  1. Add scope `ADMIN`
3. Should access tokens granted to this client have only access to the associated user?
  1. Add scope `ME`
4. Remove the old scopes (`GET`, `POST`, `PUT`, `PATCH`, `DELETE`)

**WARNING:** Adding the scope `ADMIN` to an untrusted client can lead to security issues.
Access tokens granted to it will have full access to OSIAM.

**NOTE:** Remember to change your applications too, so they use the new scopes.

The new scopes `ADMIN` and `ME` have been supported since version 2.1.
This way it is possible to switch without updating OSIAM and still have everything work as expected.
This way you can schedule an update at your own convenience and minimize downtime.

#### Configure the Connector4Java

If you write Java applications that integrate with OSIAM and are using the Connector4Java,
then you need to change the configuration of the connector:

```java
new OsiamConnector.Builder()
        [...]
        .setAuthServerEndpoint(AUTH_ENDPOINT_ADDRESS)
        .setResourceServerEndpoint(RESOURCE_ENDPOINT_ADDRESS)
        [...]
        .build();
```

becomes

```java
new OsiamConnector.Builder()
        [...]
        .withEndpoint(OSIAM_ENDPOINT)
        [...]
        .build();
```

#### API Changes

There have been some minor API changes, namely:

- Invalid filters now get a reply of 400 BAD REQUEST.
- Unexpected errors now reply with 500 INTERNAL SERVER ERROR instead of 409 CONFLICT.
- The URL of the service provider configuration was changed from `/ServiceProviderConfigs` to `/ServiceProviderConfig`.

Please update your applications accordingly.

## Old Versions

You can find the migration notes of older versions < 3.x here:

- [auth-server](https://github.com/osiam/auth-server/blob/master/docs/migration.md)
- [resource-server](https://github.com/osiam/resource-server/blob/master/docs/migration.md)
