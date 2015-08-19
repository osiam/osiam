# Configuration

OSIAM needs to be configured correctly to work. Create the file
`/etc/osiam/resource-server.properties` with content based on this example:

```
# Home URL (needed for self reference)
org.osiam.resource-server.home=http://localhost:8080/osiam-resource-server
# ATTENTION: you have to set a random secret for the resource server client!
#org.osiam.resource-server.client.secret=

# OSIAM auth server configuration
org.osiam.auth-server.home=http://localhost:8080/osiam-auth-server
```

The example properties file can also be found on [GitHub](https://github.com/osiam/server/blob/master/resource-server/src/main/deploy/resource-server.properties).

OSIAM needs to be instructed on how to connect to the database, too. The actual
values for the properties in `/etc/osiam/resource-server.properties` depend on
whether you use PostgreSQL or MySQL as the database.

## PostgreSQL

Here is an example, how the connection properties should look like for PostgreSQL:

```
org.osiam.resource-server.db.vendor=postgresql
org.osiam.resource-server.db.driver=org.postgresql.Driver
org.osiam.resource-server.db.url=jdbc:postgresql://localhost:5432/osiam
org.osiam.resource-server.db.username=osiam
org.osiam.resource-server.db.password=<YOUR_PASSWORD>
```

## MySQL

Here is an example, how the connection properties should look like for MySQL:

```
org.osiam.auth-server.db.vendor=mysql
org.osiam.auth-server.db.driver=com.mysql.jdbc.Driver
org.osiam.auth-server.db.url=jdbc:mysql://localhost:3306/osiam
org.osiam.auth-server.db.username=osiam
org.osiam.auth-server.db.password=<YOUR_PASSWORD>
```

## Default User

The resource-server creates following user on startup:

<table>
<tr><td>Username</td><td>admin</td></tr>
<tr><td>Password</td><td>koala</td></tr>
</table>

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

## Next Steps

[Deploy](deployment.md) the resource-server.
