# OSIAM

## 3.0 - Unreleased

**Breaking changes!**

### Features

- Support for H2 database has been added and a file-based one is the default
  configuration from now on. The usage scenarios are small installations,
  testing and development.
- Migration and initialization of the database can be done from the command line
  with the `migrateDb` command. See [Initialize the Database from the Command Line]
  (docs/detailed-reference-installation.md#initialize-the-database-from-the-command-line).

### Changes

- Require a Java runtime environment of at least version 8
- Remove support for deprecated method-based OAuth scopes
- Allow colons (:) as field separators for URNs of extensions, since this is
  what the SCIM specification defines. Using periods (.) is still possible,
  but will log a warning message.
- Fields of the core schemas for user and group can be fully qualified, i.e.
  `filter=urn:ietf:params:scim:schemas:core:2.0:User:userName sw "J"`
- Example data will now be created during startup. If there are no clients in
  the database, an example client will be created. If there are no users in the
  database, an initial admin user will be created. The details of the client and
  user will be logged. This removes the creation of initial data during the
  database setup.
- Migrate to BCrypt hashes
  When users login, their passwords will be updated to the new BCrypt hashes.
  Until the Release 4.0 the old sha password hashes will be supported.
  After OSIAM 4.0 was released the old password hashes are not usable anymore.

## Old Versions

You can find the changelog of older versions < 3.x here:

- [auth-server](https://github.com/osiam/auth-server/blob/master/CHANGELOG.md)
- [resource-server](https://github.com/osiam/resource-server/blob/master/CHANGELOG.md)
