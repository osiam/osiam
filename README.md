# OSIAM - Open Source Identity and Access Management

OSIAM is a secure identity management solution providing REST based services for
authentication and authorization. We achieve this by implementing two important
open standards:

* [OAuth 2.0](http://oauth.net/2/)
* [SCIM v2](http://www.simplecloud.info/) (still in draft phase)

OSIAM is published under the MIT licence, giving you the greatest freedom
possible to utilize OSIAM in you project or product.

## Use cases

* [Single app with OSIAM](docs/single-app-use-case.md) as free backing store for
  users and groups and local auth service.
* [Distributed app with OSIAM](docs/distributed-app-use-case.md) as central
  identity store and auth service.
* [Publish your app's API with OSIAM](docs/protected-api-use-case.md) as auth
  service for 3rd-parties and allow safe access to your user's data.

## Get started

You want to try OSIAM without much effort? Get started with
[OSIAM in 5 Minutes](docs/osiam-in-5-minutes.md) using Docker and Java. Don't
like Docker? No problem! Take the
[10 minutes quickstart](docs/10-minutes-quickstart.md) with Vagrant.

## Documentation

Learn how to install and configure OSIAM for production in the
[documentation](docs/README.md). For additional information visit our official
website https://www.osiam.org.

## Components

* [`auth-server`](https://github.com/osiam/auth-server)
  handles the authentication and authorization based on OAuth 2.0
* [`resource-server`](https://github.com/osiam/resource-server)
  holds the SCIM based user data
* [`addon-self-administration`](https://github.com/osiam/addon-self-administration)
  provides account management self-service as a web application
* [`addon-administration`](https://github.com/osiam/addon-administration)
  lets you administer users and groups via a web application

### Easy customization

Every visual aspect of OSIAM's components can be easily customized by supplying
your own templates, stylesheets and scripts to make OSIAM integrate seamlessly
with your application. Read the
[customization guide](docs/customization-guide.md) to learn more.

## Issue Tracker

Please report issues, bugs and feature requests via [the issue tracker]
(https://github.com/osiam/osiam/issues).

## Get involved

Help is always appreciated. Read the
[contributors guide](docs/contributors-guide.md) to learn more.
