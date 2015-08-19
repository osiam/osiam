# OSIAM resource server

## Unreleased

### Changes

- Update OSIAM connector4java
- Remove support for old, method-based OAuth scopes
- Remove configuration property `org.osiam.resource-server.db.dialect`

## 2.2 - 2015-06-18

### Changes

- Bump connector to make use of more concurrent HTTP connections

## 2.1 - 2015-06-02

### Features

- Support for new `ME` scope
- Support for new `ADMIN` scope

### Fixes

- Secure search endpoint on `/`
- PostalCode should not be retrieved as literal `null` string when not set

### Other

- resource-server now lives in its own Git repo
- Changed artifact id from `osiam-resource-server` to `resource-server`

## 2.0 - 2015-04-29

**Breaking changes!**

This release introduces breaking changes, due to the introduction of automatic
database schema updates powered by Flyway. See the
[migration notes](docs/Migration.md#from-13x-to-20) for further details.

- [feature] Support automatic database migrations
- [feature] create JAR containing the classes of app
- [fix] lower constraint index lengths for MySQL
- [fix] replace Windows line endings with Unix ones in SQL scripts
- [change] decrease default verbosity
- [change] bump dependency versions
- [docs] move documentation from Wiki to repo
- [docs] rename file RELEASE.NOTES to CHANGELOG.md

## 1.3 - 2014-10-17

- [fix] Infinite recursion when filtering or sorting by x509certivicates.value
- [fix] Sorting by name sub-attribute breaks the result list

    For a detailed description and migration see:
    https://github.com/osiam/server/wiki/Migration#from-12-to-13

## 1.2 - 2014-09-30

- [feature] Introduced an interface to get the extension definitions (/osiam/extension-definition) 

## 1.1 - 2014-09-19

- [feature] support for mysql as database
- [enhancement] Force UTF-8 encoding of requests and responses
- [enhancement] better error message on search
  When searching for resources and forgetting the surrounding double quotes for
  values, a non-understandable error message was responded. the error message
  was changed to explicitly tell that the error occurred due to missing
  double quotes.
- [enhancement] updated dependencies: Spring 4.1.0, Spring Security 3.2.5,
  Spring Metrics 3.0.2, Jackson 2.4.2, Hibernate 4.3.6, AspectJ 1.8.2,
  Joda Time 2.4, Joda Convert 1.7, Apache Commons Logging 1.2, Guava 18.0,
  Postgres JDBC Driver 9.3-1102-jdbc41
