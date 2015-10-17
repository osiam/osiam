## from 1.3.x to 2.0

Flyway was added to support schema upgrades for the next versions. To
distinguish between the currently supported database systems you have to change
the file `/etc/osiam/resource-server.properties` like this:

**NOTE:** these steps must be taken **BEFORE** the deployment of the new
web apps!

In `/etc/osiam/resource-server.properties` add a line containing

    org.osiam.resource-server.db.vendor=<database vendor>

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
`/etc/osiam/resource-server.properties`. Next, set `flyway.user` and
`flyway.password` using the settings from
`/etc/osiam/resource-server.properties`like before. Uncomment and set the
property `flyway.table` to `resource_server_schema_version`. Now it's time to
run the baseline process:

    $ ./flyway baseline

You can now deploy the new web apps using your favorite way of deployment.
