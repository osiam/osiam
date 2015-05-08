# Installation by Debian packages

The debian package sources are integrated in the [distribution repository](https://github.com/osiam/distribution).

For a simple installation we provide `.deb` packages. They should work on all recent debian based distributions and
are mostly tested with ubuntu 12.04 and debian weezy.

## With OSIAM apt repository

    TODO ...
    sudo apt-get install osiam

## With downloaded packages

    sudo dpkg -i osiam-*.deb
    sudo apt-get install -f

###Database configuration

While installation you can choose to get the database setup managed by the installation. In this case you just have to provide a database password. The installation then creates a postgresql database called `osiam`, on the same machine and imports the basic database schema.
If you prefer to do the database configuration by hand, simply choose this option and edit the `/etc/*.properties` files after installation.

## Import basic data

When starting the applications there is already [basic data](detailed-reference-installation.md#default-setup)
imported by Flyway.

## Basic Installation Architecture

### Packages

We have divided OSIAM in the following packages:

* **osiam-common:** OSIAM common package. This package contains the tomcat instance configuring for all osiam services.
* **osiam-auth-server:** OSIAM authorisation server. The server component for login  and ticket granting.
* **osiam-resource-server:** OSIAM resource server. The server component for management of the identity data over a SCIM2 API.
* **osiam-addon-self-administration:** OSIAM self administration web frontends. The OSIAM frontends for registration and password self service.
* **osiam:** Metapackage depending on all OSIAM sub-packages. Does nothing except installing all other osiam packages.

### Distribution over different machines 

You can choose to install all package on the same machine, or distribute them over different machines, depending on your security architecture. In the most cases, the auth-server and the addon-self-administration will be on a public available server (behind an apache), while the resource-server is installed on a machine, not directly connected with the internet.

### The osiam service

OSIAM will be started in it's own process on `port 6001`. You can manage osiam with

    service osiam start|stop|restart

After startup, you can test the services e.g. with curl:

    curl http://localhost:6001/osiam-resource-server/ServiceProviderConfigs

You can find the logfiles at:

    /var/log/osiam/

### Tomcat configuration

OSIAM uses the distributions tomcat7 packages, but with an own configuration. You can modify the osiam tomcat configuration by editing the files in `/etc/osiam/tomcat-conf/`.
