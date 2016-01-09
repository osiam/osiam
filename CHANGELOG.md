# OSIAM

## 3.0 - Unreleased

**Breaking changes!**

### Changes

- Remove usage of old, method-based OAuth scopes
- Remove support for old, method-based OAuth scopes
- Add Flyway migration to replace method-based scopes

    The migration will remove all old, method-based scopes from all well-known
    clients and then add the new scopes `ADMIN` and `ME`. See the
    [migration notes] (docs/Migration.md#from-22-to-30) for further details.

- Allow colons (:) as field separators for URNs of extensions, since this is
  what the SCIM specification defines. Using periods (.) is still possible,
  but will log a warning message. 

- Fields of the core schemas for user and group can be fully qualified, i.e.
  `filter=urn:ietf:params:scim:schemas:core:2.0:User:userName sw "J"` 

- All invalid search queries now respond with a `400 BAD REQUEST` instead of
  `409 CONFLICT` status code.
- Respond with `401 UNAUTHORIZED` when revoking or validating an access token
  fails because of invalid access token.
- Remove configuration property `org.osiam.*.db.dialect`
- Remove self written profiling solution since we now use the [Metrics](https://github.com/dropwizard/metrics)
  framework. This removes the configuration property `org.osiam.*.profiling`

## Old Versions

You can find the changelog of older versions < 3.x here:

- [auth-server](https://github.com/osiam/auth-server/blob/master/CHANGELOG.md)
- [resource-server](https://github.com/osiam/resource-server/blob/master/CHANGELOG.md)

