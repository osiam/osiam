# OSIAM - Open Source Identity and Access Management [![Circle CI](https://circleci.com/gh/osiam/osiam.svg?style=svg)](https://circleci.com/gh/osiam/osiam) [![Codacy Badge](https://api.codacy.com/project/badge/grade/c63f2618f4c64292ae47a1decf1a7270)](https://www.codacy.com/app/OSIAM/osiam) [![Codacy Badge](https://api.codacy.com/project/badge/coverage/c63f2618f4c64292ae47a1decf1a7270)](https://www.codacy.com/app/OSIAM/osiam)

**Active development of this project has stopped.**

OSIAM is a secure identity management solution providing REST based services for
authentication and authorization. We achieve this by implementing two important
open standards:

* [OAuth 2.0](http://oauth.net/2/)
* [SCIM 2.0](http://www.simplecloud.info/)

OSIAM is published under the MIT licence, giving you the greatest freedom
possible to utilize OSIAM in you project or product.
Watch [our blog](http://osiam.github.io/) to stay informed about releases and upcoming changes.

## Use cases

* [Single app with OSIAM](docs/single-app-use-case.md) as free backing store for
  users and groups and local auth service.
* [Distributed app with OSIAM](docs/distributed-app-use-case.md) as central
  identity store and auth service.
* [Publish your app's API with OSIAM](docs/protected-api-use-case.md) as auth
  service for 3rd-parties and allow safe access to your user's data.

## Quick Start

[Download](https://dl.bintray.com/osiam/downloads/osiam/3.0/osiam-3.0.war) the latest version of OSIAM:

```
curl -L https://dl.bintray.com/osiam/downloads/osiam/3.0/osiam-3.0.war -o osiam.war
```

Make the `.war` file executable:

```
chmod +x osiam.war
```

Start OSIAM:

```
./osiam.war
```

After some seconds, OSIAM should be fully running. You can now retrieve your first access token:

```
curl -H "Authorization: Basic ZXhhbXBsZS1jbGllbnQ6c2VjcmV0" -X POST -d "grant_type=client_credentials&scope=ADMIN" http://localhost:8080/oauth/token
```

You can now start to setup OSIAM, by changing the administrator's password and add your own OAuth client.
Please see the [Installation and Configuration Manual](docs/detailed-reference-installation.md#customize-setup) for details.

## Documentation

Learn how to install and configure OSIAM for production in the
[documentation](docs/README.md).

## Components

* [`osiam`](https://github.com/osiam/osiam)
  handles the authentication and authorization based on OAuth 2.0 and holds the SCIM based user data
* [`addon-self-administration`](https://github.com/osiam/addon-self-administration)
  provides account management self-service as a web application
* [`addon-administration`](https://github.com/osiam/addon-administration)
  lets you administer users and groups via a web application

### Easy customization

Every visual aspect of OSIAM's components can be easily customized by supplying
your own templates, stylesheets and scripts to make OSIAM integrate seamlessly
with your application. Read the
[customization guide](docs/customization-guide.md) to learn more.

## Snapshots

To use the latest snapshot of OSIAM just download it from JFrog OSS:
https://oss.jfrog.org/repo/org/osiam/osiam/latest-SNAPSHOT/

Scroll down to the bottom of the page and select the latest WAR file.

## Issue Tracker

Please report issues, bugs and feature requests via [the issue tracker]
(https://github.com/osiam/osiam/issues).

## Get involved

Help is very appreciated. Please read the
[contributors guide](CONTRIBUTING.md) to learn how to get started.
