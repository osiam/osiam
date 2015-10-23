# OSIAM auth server

## 2.3 - 2015-10-09

### Features

- JDBC connection pooling

### Changes

- Increase timeouts for connections to resource-server
- Increase maximum number of parallel connections to resource-server

### Fixes

- Make sure `access_token`, `refresh_token` and `token_type` are added only
  once to the returned Access Token (Fixes bug [#42]
  (https://github.com/osiam/auth-server/issues/42)).
- Remove `scopes` from the Access Token (Fixes bug [#51]
  (https://github.com/osiam/auth-server/issues/51)).
- Prevent NPE when `User#active` is null

### Updates

- OSIAM connector4java 1.7
- MySQL JDBC driver 5.1.36
- PostgreSQL JDBC driver 9.4-1203
- Spring 4.1.7.RELEASE
- AspectJ 1.8.7
- SLF4J LOG4J-12 Binding 1.7.12

## 2.2 - 2015-06-18

### Changes

- Bump connector to make use of more concurrent HTTP connections

## 2.1.2 - 2015-06-02

### Fixes

- Revert 'Change OAuth JSON error to comply to spec'

    "old style" error messages are back:
    ```json
    {
      "error_code": "...",
      "description": "..."
    }
    ```

## 2.1.1 - 2015-06-02

### Other

- Append classifier to distribution artifact

## 2.1 - 2015-06-02

### Features

- Support for new `ME` scope
- Support for new `ADMIN` scope
- Remember and check timestamp of approval per client in web session

### Changes

- Remove field `expiry` from OAuth clients
- Bump dependencies

### Fixes

- Revert change that disabled single sign-on
- Secure the '/token/revocation' endpoints
- Change OAuth JSON error to comply to spec

    Was:
    ```json
    {
      "error_code": "...",
      "description": "..."
    }
    ```

    Changed to:
    ```json
    {
      "error": "...",
      "description": "..."
    }
    ```

### Other

- Auth-server now lives in its own Git repo
- Changed artifact id from `osiam-auth-server` to `auth-server`

## 2.0 - 2015-04-29

**Breaking changes!**

This release introduces breaking changes, due to the introduction of automatic
database schema updates powered by Flyway. See the
[migration notes](docs/Migration.md#from-13x-to-20) for further details.

- [feature] Support automatic database migrations
- [feature] create JAR containing the classes of app
- [fix] replace Windows line endings with Unix ones in SQL scripts
- [change] decrease default verbosity
- [change] bump dependency versions
- [docs] move documentation from Wiki to repo
- [docs] rename file RELEASE.NOTES to CHANGELOG.md

## 1.3.2 - 2014-11-24
- release because of fixes in addon-administration

## 1.3.1 - 2014-10-27
- release because of fixes in addon-self-administration

## 1.3 - 2014-10-17
- [fix] Umlauts encoding problems
- [fix] Wrong directory name for translations

  For a detailed description and migration see:
  https://github.com/osiam/server/wiki/Migration#from-12-to-13

## 1.2 - 2014-09-30
- release because of fixes in addon-self-administration

## 1.1 - 2014-09-19
- [feature] support for mysql as database
- [feature] prevent users from login after N failed attempts
- [feature] revocation of access tokens
  It is now possible to revoke access tokens by using the following service
  endpoints:
  * auth-server/token/revocation
    For revocation of the access token sent as bearer token in the
    Authorization header
  * auth-server/token/revocation/<uuid of user>
    For revocation of all access tokens that were issued to or in behalf of a
    given user. This endpoint is protected.
- [feature] revoke all access tokens of a deactivated/deleted user
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
