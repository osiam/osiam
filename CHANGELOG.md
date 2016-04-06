# OSIAM

## 3.0 - Unreleased

**Breaking changes!**

In this release, the auth-server and resource-server have been merged into a
single application. For a detailed explanation about how to migrate from
OSIAM 2.5 to OSIAM 3.0, see the [migration notes](docs/migration.md).

### Features

- Run as a standalone application using the `.war` file as an executable, i.e.
  just run `osiam.war` on the command line like you would run any other command.
- Support for H2 database has been added and a file-based one is the default
  configuration from now on. The usage scenarios are small installations,
  testing and development.
- Load configuration, files and assets from arbitrary paths in the filesystem.
  Introduce the notion of a home directory that contains all these things. The
  home directory will be automatically initialized on startup. The home
  directory can also be initialized on the command line. See the [documentation]
  (docs/detailed-reference-installation.md) for details.
- Migration and initialization of the database can be done from the command line
  with the `migrateDb` command. See [Initialize the Database from the Command Line]
  (docs/detailed-reference-installation.md#initialize-the-database-from-the-command-line).
- Configure SCIM extensions in the configuration file. See
  [Configuring SCIM Extension](docs/detailed-reference-installation.md#configuring-scim-extension).
- Connections via AJP can be used now. This is disabled by default. See
  [Enable AJP support](docs/detailed-reference-installation.md#enable-ajp-support).
- Set the logging level with the configuration property `osiam.logging.level`.

### Changes

- Distribution artifacts have been completely dropped. The `.war` file contains
  all needed files and assets now.
- Use sensible defaults for logging. Default level is now error, Spring stuff
  is logging warnings, and OSIAM logs on info level.
- Remove unneeded attributes from default login template.
- Remove ability to search by a `User`'s password.
- The configuration file has been changed to YAML format.
- All configuration properties have been moved to a new namespace `osiam`.
- Require a Java runtime environment of at least version 8.
- Remove support for deprecated method-based OAuth scopes.
- Allow colons (:) as field separators for URNs of extensions, since this is
  what the SCIM specification defines. Using periods (.) is still possible,
  but will log a warning message.
- Fields of the core schemas for user and group can be fully qualified, i.e.
  `filter=urn:ietf:params:scim:schemas:core:2.0:User:userName sw "J"`.
- Example data will now be created during startup. If there are no clients in
  the database, an example client will be created. If there are no users in the
  database, an initial admin user will be created. The details of the client and
  user will be logged. This removes the creation of initial data during the
  database setup.
- Replace SHA-512 with BCrypt for hashing passwords. When a user logs in, their
  password will be automatically migrated to BCrypt. Support for SHA-512
  password hashes will be removed in OSIAM 4.0.

### Fixes

- Reply with 400 BAD REQUEST to invalid filters.
- Reply with 500 INTERNAL SERVER ERROR, instead of 409 CONFLICT, on unexpected
  errors.
- Change URL of service provider configuration resource from
  `/ServiceProviderConfigs` to `/ServiceProviderConfig`.
- Always return the `id` attribute, when searching for `User`s.
- Return a SCIM 2 compliant `User` when querying `/Me`. This replaces the old Facebook
  connector.

## Old Versions

You can find the changelog of older versions < 3.x here:

- [auth-server](https://github.com/osiam/auth-server/blob/master/CHANGELOG.md)
- [resource-server](https://github.com/osiam/resource-server/blob/master/CHANGELOG.md)
