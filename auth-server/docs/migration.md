# Migration Notes

## from 2.2 to 3.0

This release removes the old, HTTP method-based scopes and replaces them with
the new, functional-motivated scopes `ADMIN` and `ME`. The auth-server will
automatically migrate all well-known clients to the new scopes in the following
way:

1. Remove the old scopes (`GET`, `POST`, `PUT`, `PATCH`, `DELETE`)
2. Add the new scopes (`ADMIN`, `ME`)

Well-known clients and their scopes are:

* `example-client` (`ADMIN`, `ME`)
* `auth-server` (`ADMIN`)
* `addon-self-administration-client` (`ADMIN`)
* `addon-administration-client` (`ADMIN`)

The migration will not touch self-defined scopes nor self-defined clients!
Before deploying the auth-server and resource-server (and thus run the automatic
migration) update your existing clients:

1. Examine the current scopes and usages of the client.
2. Should access tokens granted to this client have full administrative access to OSIAM?
  1. Add scope `ADMIN`
3. Should access tokens granted to this client have only access to the associated user?
  1. Add scope `ME`
4. Remove the old scopes (`GET`, `POST`, `PUT`, `PATCH`, `DELETE`)
5. Deploy the auth-server

**Note:** The new scopes ADMIN and ME have been supported since the last
release, to make it possible to just switch to them without upgrading OSIAM and
still have everything work as expected. This way you can schedule an upgrade at
your own convenience and minimize downtime.

**Beware:** having a scope like `ADMIN` defined for your client can lead to
security issues if you allow untrusted entities to request access tokens with
whatever scopes they want.

## from 1.3.x to 2.0

Flyway was added to support schema upgrades for the next versions. To
distinguish between the currently supported database systems you have to change
the file `/etc/osiam/auth-server.properties` like this:

**NOTE:** these steps must be taken **BEFORE** the deployment of the new
web apps!

In `/etc/osiam/auth-server.properties` add a line containing

    org.osiam.auth-server.db.vendor=<database vendor>

`<database vendor>` can be one of:

* postgresql
* mysql

You have to restart Tomcat (or any other Servlet container you're using)
after this.

If you have an existing database and cannot "throw your data away" (or backup
and restore it) use the following instructions to migrate your database to be
ready for Flyway. Otherwise, purge all data in your database and deploy the new
web apps using your favorite way of deployment.

### Migrate existing database

Get the Flyway command line client from
http://flywaydb.org/getstarted/download.html. Download the
`flyway-commandline-X.X.X.tar.gz (without JRE)` package. Unpack the archive on a
server that has access to your database and change dir into the unpacked folder.
Now, edit the file `conf/flyway.conf` and change the property `flyway.url` to
suit your needs. You can copy the JDBC connection URL from
`/etc/osiam/auth-server.properties`. Next, set `flyway.user` and
`flyway.password` using the settings from
`/etc/osiam/auth-server.properties`like before. Uncomment and set the
property `flyway.table` to `auth_server_schema_version`. Now it's time to
run the baseline process:

    $ ./flyway baseline

You can now deploy the new web apps using your favorite way of deployment.
