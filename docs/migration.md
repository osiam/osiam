# Migration Notes

## from 2.2 to 3.0

This release removes the old, HTTP method-based scopes and replaces them with
the new, functional-motivated scopes `ADMIN` and `ME`. OSIAM will
automatically migrate all well-known clients to the new scopes in the following
way:

1. Remove the old scopes (`GET`, `POST`, `PUT`, `PATCH`, `DELETE`)
2. Add the new scopes (`ADMIN`, `ME`)

Well-known clients and their scopes are:

* `example-client` (`ADMIN`, `ME`)
* `addon-self-administration-client` (`ADMIN`)
* `addon-administration-client` (`ADMIN`)

**NOTE:** The migration will not touch self-defined scopes nor self-defined
clients. Before deploying OSIAM (and thus run the automatic migration) update
your existing clients:

1. Examine the current scopes and usages of the client.
2. Should access tokens granted to this client have full administrative access to OSIAM?
  1. Add scope `ADMIN`
3. Should access tokens granted to this client have only access to the associated user?
  1. Add scope `ME`
4. Remove the old scopes (`GET`, `POST`, `PUT`, `PATCH`, `DELETE`)
5. Deploy OSIAM

**NOTE:** The new scopes `ADMIN` and `ME` have been supported since the last
release, to make it possible to just switch to them without upgrading OSIAM and
still have everything work as expected. This way you can schedule an upgrade at
your own convenience and minimize downtime.

**WARNING:** having a scope like `ADMIN` defined for your client can lead to
security issues if you allow untrusted entities to request access tokens with
whatever scopes they want.
