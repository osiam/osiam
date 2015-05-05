# Installation

This document describes how to install the OSIAM resource-server.

## Requirements

This document takes the following prerequisites as basis:

<table>
<tr><td>Category</td><td>Prerequisite</td><td>Comment</td></tr>
<tr><td>Operating Systems</td><td>Debian Wheezy, Ubuntu 12.04 and later, Fedora 18 and later</td><td>Debian Wheezy recommended. Install with "Standard system utilities" and "SSH server".</td></tr>
<tr><td>Runtime Environment</td><td>Java 7, OpenJDK 7 JRE</td><td>apt-get install openjdk-7-jre</td></tr>
<tr><td>Application Server</td><td>Tomcat 7</td><td>apt-get install tomcat7</td></tr>
<tr><td>Database</td><td>PostgreSQL 9.1</td><td>apt-get install postgresql-9.1</td></tr>
</table>

All of the items are freshly installed. If you use a minimum Debian
installation you may want to install additional packages that are required for
the described process:

    # apt-get install unzip sudo curl

If you have an existing and older installation, you are using different version
or a different OS, AppServer or DB this document may not match your situation. 

## The operating system

For the installation of OSIAM you can work with one database for the auth- and
the resource-server or each service can have his own database. Using distinct
databases for each service is recommended, though.

For this document we are working with the user "osiam". In principle you can
work with your own non privileged user to follow the steps in this document.

Verify the application server and database server are up and running after the
installation of the requirements above. When looking at the process list:

    $ ps aux 

You should be able to find some processes running under the postgres user and at
least one running under the tomcat7 user:

    tomcat7  18461  0.2  1.5 373412 65468 ?        Sl   10:52   0:16 /usr/lib/jvm/java-7-openjdk-i386/bin/java [..]
    postgres 19926  0.0  0.1  47188  7480 ?        S    10:54   0:01 /usr/lib/postgresql/9.1/bin/postgres [..]
    postgres 19928  0.0  0.0  47188  1528 ?        Ss   10:54   0:01 postgres: writer process
    postgres 19929  0.0  0.0  47188  1284 ?        Ss   10:54   0:01 postgres: wal writer process
    postgres 19930  0.0  0.0  47620  2372 ?        Ss   10:54   0:00 postgres: autovacuum launcher process
    postgres 19931  0.0  0.0  17388  1344 ?        Ss   10:54   0:00 postgres: stats collector process

We recommend to choose the latest OSIAM release version. You can easily download
the distribution as .zip or .tar.gz file here:

* Release Repository: http://maven-repo.evolvis.org/releases/org/osiam/osiam-distribution

This include the resource-server, auth-server, addon-self-administration and
addon-administration, every project has it's own folder with the configuration
and sql files.

If you just want the use the resource-server, then download the server
distribution without the addons here:

* Release Repository: http://maven-repo.evolvis.org/releases/org/osiam/resource-server

Both distributions have in common, that the included projects are packed as .war
files and a configuration folder. Before you start, you have to copy the files
and folders inside the configuration folder to the shared loader of your
application server, like described [here](#deployment-into-the-application-server).

* GitHub Release Tags: https://github.com/osiam/resource-server/releases

You can also download the `.war` files for the OSIAM resource-server:

    $ wget http://maven-repo.evolvis.org/releases/org/osiam/resource-server/<VERSION>/resource-server-<VERSION>.war

## Setup the application server

Edit the file `/etc/tomcat7/catalina.properties`. Add to the parameter
`shared.loader` the complete path of the directory where the file
`resource-server.properties` will be placed. This will almost always be the
directory `/etc/osiam`. For example:

    shared.loader=/var/lib/tomcat7/shared/classes,/var/lib/tomcat7/shared/*.jar,/etc/osiam

Edit the file `/etc/default/tomcat7` and change the size of the heap space
allocated for Tomcat by modifying the following line 

    JAVA_OPTS="-Djava.awt.headless=true -Xmx128m -XX:+UseConcMarkSweepGC"

to 

    JAVA_OPTS="-Djava.awt.headless=true -Xms512m -Xmx1024m -XX:+UseConcMarkSweepGC"

And now restart Tomcat:

    $ sudo /etc/init.d/tomcat7 restart

## Next Steps

[Configure](configuration.md) the resource-server.
