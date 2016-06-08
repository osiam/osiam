# HTTP API Documentation

## Table of contents

- [Basics](#basics)
- [OAuth2](#oauth2)
    - [Supported Grants](#supported-grants)
    - [Authorization Code Grant](#authorization-code-grant)
    - [Resource Owner Password Credentials Grant](#resource-owner-password-credentials-grant)
    - [Client Credentials Grant](#client-credentials-grant)
- [Scopes](#scopes)
    - [Supported scopes](#supported-scopes)
- [Client Management](#client-management)
- [SCIM](#scim)
    - [OSIAM Implementation Specifics](#osiam-implementation-specifics)
    - [OSIAM's Known Limitations](#osiams-known-limitations)
    - [Unsupported Interfaces](#unsupported-interfaces)
    - [Service provider configuration](#service-provider-configuration)
    - [Search](#search)
        - [Search filtering including logical operators](#search-filtering-including-logical-operators)
        - [Limiting the output to predefined attributes](#limiting-the-output-to-predefined-attributes)
        - [Sorting](#sorting)
        - [Paging](#paging)
    - [User management](#user-management)
        - [Get a single User](#get-a-single-user)
        - [Get the User of the current accessToken](#get-the-user-of-the-current-accesstoken)
        - [Create a new User](#create-a-new-user)
        - [Replace an existing User](#replace-an-existing-user)
        - [Update a User](#update-a-user)
        - [Get the Authenticated User](#get-the-authenticated-user)
        - [Delete a User](#delete-a-user)
    - [Group management](#group-management)
        - [Get a single Group](#get-a-single-group)
        - [Create a new Group](#create-a-new-group)
        - [Replace an existing Group](#replace-an-existing-group)
        - [Update a Group](#update-a-group)
        - [Delete a Group](#delete-a-group)

## Basics

OSIAM uses the [OAuth 2.0](http://tools.ietf.org/html/rfc6749) standard the
provide a secure API. The standard supports different ways of getting access to
a secured API. They are called "grants". OSIAM support three different grants,
while the concept of OSIAM is based on the grant providing the best security.
It is important to understand the concept of the so called 'Authorization Code
Grant'.

There are a lot of websites out there explaining that topic in detail, the
following sections are an OSIAM related summary.

OSIAM has two types of interfaces:
* The authx interface - also called technical interface, implementing the OAuth
2.0 standard: Used for authentication (authn) and authorization (authz)
* The resource interface - also called functional interface, implementing the
SCIMv2 standard: Used for management of the data (identities) within OSIAM

OSIAM basically communicates with two parties:
* The client: Is an application that wants to work with the data stored in
OSIAM
* The user: Is the one who is using the client.

OSIAM needs to know both parties to provide its services.
* The client has to be configured in OSIAM including a secret phrase, so OSIAM
can verify that it is an authentic client is sending requests.
* The user needs to be stored in the OSIAM database as well, including an
authentication parameter (e.g. a password) and the so called 'scopes'. The
scopes define what actions an user can perform on the OSIAM resource interface.

In order for a client to access the resource interface of OSIAM the client
needs to be authorized. This is done by the user. In the standard flow the user
gets to see a [login screen]
(../src/main/deploy/osiam/templates/web/login.html)
provided by OSIAM to authenticate themselves (e.g. using a password). If
successful, the client can make use of OSIAM's resource interface based on the
scopes defined on the user's record.

So in short words: OSIAM needs to know at least about a single user and one
client to provide its services. That is the reason why an OSIAM installation
comes with the [default user and a default client]
(https://github.com/osiam/osiam/blob/master/docs/detailed-reference-installation.md#default-setup).

The resource interface - also called functional interface - implements the
SCIMv2 standard. It's used for management of the user data (identities) within
OSIAM.

The user needs to be stored in the OSIAM database, including an authentication
parameter (e.g. a password).

**NOTE:** The URLs used in this document are valid for a standalone deployment only.
If you deploy OSIAM into a Servlet container, i.e. Tomcat, you have to append the context root after the hostname, e.g.:
`http://localhost:8080/ServiceProviderConfig` becomes `http://localhost:8080/osiam/ServiceProviderConfig`.
The context root is probably `osiam` by default.

## OAuth2

### Supported Grants

An authorization grant is a credential representing the authorization to access
protected resources. This credential is called "access token". Once a client
has the access token it has to use the token every time it accesses a protected
resource until the token gets invalid.
More details can be found in the
[OAuth 2.0 specification](http://tools.ietf.org/html/rfc6749#section-1.3).

OSIAM supports three OAuth 2.0 grant types:
* [authorization code](#authorization-code-grant)
* [resource owner password credentials](#resource-owner-password-credentials-grant)
* [client credentials](#client-credentials-grant)

### Authorization Code Grant
This is the most secure grant and is recommended for every production use case.

With this grant the user needs to authenticate himself, the user has to
authorize the client to access the protected resources and last but not least
the registered client needs to authenticate himself.

OSIAM implements some additional features for the client authorization that are
[configurable per client](#client-management):
* Authorization behaviour: OSIAM can skip the step of the user authorization by
performing an internal implicit authorization or the a authorization once given
by the user can be store as valid for a configurable period of time, instead of
asking the user for authorization on every login.

#### How to get the access token?

##### Authorization Request
First the "authorization code" is required. This happens with the so called
[authorization request](http://tools.ietf.org/html/rfc6749#section-4.1.1).
In short words this is what happens:

The client that wants to get the authorization code redirects the user's
browser to the authorization server providing several parameters shown in the
example request below:

    http://OSIAM_HOST:8080/oauth/authorize?client_id=<CLIENT_ID>&response_type=code&redirect_uri=<CLIENT_REDIRECT_URI>&scope=<SCOPES>

This request an all consequent examples are based on an encrypted connection,
if you do not have SSL enabled on your application server, the protocol prefix
is only 'http://' and the port is likely to be '8080'.

The additional parameters in the example shortly explained:
* OSIAM_HOST - is the hostname or IP address of the machine OSIAM is running on.
* CLIENT_ID - is the identifier of the client the client was registered with
OSIAM's client management interface.
* CLIENT_REDIRECT_URI - the redirect URI of the client. This URI must begin
with the value of the redirect URI stored in OSIAM's database for that
client, or it can be identical to it. So you can provide additional parameters
for the redirect URI in the authorization request.
* SCOPES - the [scopes](#scopes) the client wants to be authorized for when
using the access token.

##### Interactive Authorization
Now the user sees a page to authenticate himself (e.g. using a username and a
password). After successful authentication and when the client is configured in
the OSIAM database that way, the user will also be asked to authorize the client.

##### Authorization Response
The server now sends an
[authorization response](http://tools.ietf.org/html/rfc6749#section-4.1.2) that
includes the authorization code and redirects the user back to the client. If
something goes wrong an
[error response](http://tools.ietf.org/html/rfc6749#section-4.1.2.1) is send.

##### Access Token Request
With the authentication code the client can now talk directly to the server to
request the access token. This is done via the
[access token request](http://tools.ietf.org/html/rfc6749#section-4.1.3) using
[HTTP basic authentication](http://tools.ietf.org/html/rfc2617). An example
access token request below:

```sh
curl -H "Authorization: Basic <BASE64_CLIENT_CREDENTIALS>" -X POST -d "code=<AUTH_CODE>&grant_type=authorization_code&redirect_uri=<CLIENT_PROVIDED_URI>" http://OSIAM_HOST:8080/oauth/token
```

The parameters (beside OSIAM_HOST are):
* BASE64_CLIENT_CREDENTIALS - required for the HTTP basic authentication, it
consists of the CLIENT_ID and the client's SECRET
* AUTH_CODE - is the previously received authentication code.
* CLIENT_PROVIDED_URI - The URI of the client that must match the one stored
for the client in OSIAM's database

##### Access Token Response
The [access token response](http://tools.ietf.org/html/rfc6749#section-4.1.4)
includes the access token the client needs to use OSIAM's resource interface on
the authorized scopes.

### Resource Owner Password Credentials Grant
This grant provides the possibility to get an access token without user
interaction. But needs client and user credentials.
Authorization request/response from the authorization code grant is not
required. is necessary, only the
[Access Token Request](http://tools.ietf.org/html/rfc6749#section-4.3.2) and
[HTTP Basic Authentication](http://tools.ietf.org/html/rfc2617) is used.

```sh
curl -H "Authorization: Basic <BASE64_CLIENT_CREDENTIALS>" -X POST -d "grant_type=password&username=<USERNAME>&password=<PASSWORD>&scope=<SCOPES>" http://OSIAM_HOST:8080/oauth/token
```

The parameters are similar to the access token request from the authorization
code grant, but the user credentials are provided using the parameter
* USERNAME and
* PASSWORD

An example based on [OSIAM's default setup]
(https://github.com/osiam/osiam/blob/master/docs/detailed-reference-installation.md#default-setup):

```sh
curl -H "Authorization: Basic ZXhhbXBsZS1jbGllbnQ6c2VjcmV0" -X POST -d "grant_type=password&username=admin&password=koala&scope=ADMIN" http://localhost:8080/oauth/token
```

### Client Credentials Grant
This grant provides a possibility to get an access token without user
interaction and needs only client credentials, no authorization request is
required, only the [Access Token Request]
(http://tools.ietf.org/html/rfc6749#section-4.4.2) and
[HTTP Basic Authentication](http://tools.ietf.org/html/rfc2617) is used.

```sh
curl -H "Authorization: Basic <BASE64_CLIENT_CREDENTIALS>" -X POST -d "grant_type=client_credentials&scope=<SCOPES>"
http://OSIAM_HOST:8080/oauth/token
```

An example based on [OSIAM's default setup]
(https://github.com/osiam/osiam/blob/master/docs/detailed-reference-installation.md#default-setup)

```sh
curl -H "Authorization: Basic ZXhhbXBsZS1jbGllbnQ6c2VjcmV0" -X POST -d "grant_type=client_credentials&scope=ADMIN" http://localhost:8080/oauth/token
```

## Scopes

Scopes are used to define access rights, see [OAuth2 Spec]
(http://tools.ietf.org/html/draft-ietf-oauth-v2-31#section-1.4) for further
details.

### Supported scopes

OSIAM knows about the following scopes:

* ADMIN - allows full access to any resource
* ME - allows read and write access to the data of the user associated with the
  access token

## Client Management

The client management is a osiam defined endpoint to manage the clients needed
for the oauth flow.

URI:
    http://OSIAM_HOST:8080/Client

The client role in oauth is described as follows:

An application making protected resource requests on behalf of the resource
owner and with its authorization.  The term "client" does not imply any
particular implementation characteristics (e.g., whether the application
executes on a server, a desktop, or other devices).

[OAuth2 Roles](http://tools.ietf.org/html/rfc6749#section-1.1)

### Client configuration

A client in Osiam consists of the following configurable values:

* accessTokenValiditySeconds = is the validity in seconds of an access token
* refreshTokenValiditySeconds = is the validity in seconds of an refresh token
* redirectUri = is the uri for user agent redirection as described in
[OAuth2 RFC](http://tools.ietf.org/html/rfc6749#section-3.1.2)
* clientSecret = the clients secret is part of the client credentials and will
be generated
* scope = the access token scopes which are allowed for the client
* grants = the allowed grants for the client. Default is authorization_grant
and refresh_token
* implicit = the value indicates whether the client authorization to access
protected resources is done with or without asking the user
* validityInSeconds = is the validity in seconds for the client authorization
to access protected resources if implicit is not wanted. The user will be asked
again to authorize the client to access they data, after this time is expired.
OSIAM holds this information in the user's session.

### Get a single client

To get a single client you need to send a GET request to the url

    http://OSIAM_HOST:8080/Client/$ID

the response will be a osiam client in json format.

e.g.:

    curl -i -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" -X GET http://localhost:8080/Client/$ID

### Create a client

To create a client you need to send the client input as json via post to the
url

    http://OSIAM_HOST:8080/Client

the response will be the created client in json format.

e.g.:

    curl -i -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" -X POST http://localhost:8080/Client -d '{"id": "client_id", "accessTokenValiditySeconds": "1337", "refreshTokenValiditySeconds": "1337", "redirectUri": "http://OSIAM_HOST:5000/stuff", "scope": ["ADMIN", "ME"], "validityInSeconds": "1337", "implicit": "false", "grants": ["authorization_code", "client_credentials", "password", "refresh-token"]}'

### Replace an client

To replace a client you need to send the client input as json via put to the url

    http://OSIAM_HOST:8080/Client/$ID

the response will be the replaced client in json format.

e.g.:

```sh
curl -i -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" -X PUT http://localhost:8080/Client/$ID
-d '{"id": "client_id", "accessTokenValiditySeconds": "1337", "refreshTokenValiditySeconds": "1337", "redirectUri": "http://localhost:5000/stuff", "scope": ["ADMIN", "ME"], "validityInSeconds": "1337", "implicit": "false", "grants": ["authorization_code", "client_credentials", "password", "refresh-token"]}'
```

### Delete an client

To delete a client you need to call the url via delete

    http://OSIAM_HOST:8080/Client/$ID

the response will be status.

e.g.:

    curl -i -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" -X DELETE http://OSIAM_HOST:8080/Client/$ID

## SCIM

All SCIM endpoints are secured with OAuth 2.0, so you will at least have to
send an access token in order get the expected response:

```sh
curl -H "Authorization: Bearer YOUR_ACCESS_TOKEN" http://OSIAM_HOST:8080/Users/ID
```

The parameters are:
* YOUR_ACCESS_TOKEN - the access token that was provided through the OAuth 2.0
  grant
* ID - The ID of the user record you want to retrieve

You can retrieve an access token if you by following [these instructions]
(https://github.com/osiam/osiam/blob/master/docs/detailed-reference-installation.md#customized-setup).

Most endpoints can only be accessed with a proper authorization. The access
tokens are bound to a client or a user and need specific scopes, which are
[defined per client in OSIAM](#supported-scopes).

### OSIAM Implementation Specifics
* The default maxResults of a filter is 100
* xmlDataFormat is not supported

### OSIAM's Known Limitations
* [etag](http://tools.ietf.org/html/draft-ietf-scim-api-02#section-3.11) for resource
versioning is not supported yet, but it is already in OSIAMs backlog

### Unsupported Interfaces
* SCIM bulk actions: Is not yet implemented, but it is on the roadmap
* Cross ressource types search from the server's root: We do not understand the
use case for this functionality at the moment, please let us know if you have a
reasonable use case.

### Service provider configuration

The following URI provides the service provider configuration of the addressed
server:

```http
http://OSIAM_HOST:8080/ServiceProviderConfig
```

### Search
OSIAM supports search for both SCIM resource types, user and group.
* Users: 
```
http://OSIAM_HOST:8080/Users
```
* Groups
```
http://OSIAM_HOST:8080/Groups
```

#### Parser grammar for search

```h
grammar LogicalOperatorRules;

parse
    : expression
    ;

expression
    : expression OR expression    #orExp
    | expression AND expression   #andExp
    | NOT '(' expression ')'      #notExp
    | '(' expression ')'          #braceExp
    | FIELD PRESENT               #simplePresentExp
    | FIELD OPERATOR VALUE        #simpleExp
    ;

OR
    : 'or'
    | 'Or'
    | 'oR'
    | 'OR'
    ;

AND
    : 'and'
    | 'And'
    | 'aNd'
    | 'anD'
    | 'ANd'
    | 'aND'
    | 'AND'
    ;

NOT
    : 'not'
    | 'Not'
    | 'nOt'
    | 'noT'
    | 'NOt'
    | 'nOT'
    | 'NOT'
    ;

PRESENT
    : 'pr'
    ;

OPERATOR
    : 'sw'
    | 'co'
    | 'eq'
    | 'gt'
    | 'ge'
    | 'lt'
    | 'le'
    ;

FIELD
    : ([a-z] | [A-Z] | [0-9] | '.' | ':' | '_' | '-')+ 
    ;

ESCAPED_QUOTE
    : '\\"'
    ;

VALUE
    : '"'(ESCAPED_QUOTE | ~'"')*'"'
    ;

EXCLUDE
    : [\b | \t | \r | \n]+ -> skip
    ;
```

#### Search filtering including logical operators

The following filter options are supported:
* eq = equals
* co = contains
* sw = starts with
* pr = present
* gt = greater than
* ge = greater equals
* lt = less than
* le = less equals

**The value must be provided in double quotes. To provide a quote as part of
the search value, the input must contain \" for each desired quote.**
 
Here are some examples:

```http
http://OSIAM_HOST:8080/Users?access_token=YOUR_ACCESSTOKEN&filter=userName%20eq%20"TheUserName"

http://OSIAM_HOST:8080/Users?access_token=YOUR_ACCESSTOKEN&filter=userName%20co%20"someValue"

http://OSIAM_HOST:8080/Users?access_token=YOUR_ACCESSTOKEN&filter=userName%20sw%20"someValue"

http://OSIAM_HOST:8080/Users?access_token=YOUR_ACCESSTOKEN&filter=displayName%20pr

http://OSIAM_HOST:8080/Users?access_token=YOUR_ACCESSTOKEN&filter=meta.created%20gt%20"2013-05-23T13:12:45.672#;4302:00"

http://OSIAM_HOST:8080/Users?access_token=YOUR_ACCESSTOKEN&filter=meta.created%20ge%20<an existing time>

http://OSIAM_HOST:8080/Users?access_token=YOUR_ACCESSTOKEN&filter=meta.created%20lt%20"2013-05-23T13:12:45.672#;4302:00"

http://OSIAM_HOST:8080/Users?access_token=YOUR_ACCESSTOKEN&filter=meta.created%20le%20<an existing time>
```

Additionally "AND" and "OR" as logical operators are supported, including
grouping with parentheses.

```http
http://OSIAM_HOST:8080/Users?access_token=YOUR_ACCESSTOKEN&filter=userName%20eq%20"TheUserName"%20and%20meta.created%20lt%20"2013-05-23T13:12:45.672#;4302:00"

http://OSIAM_HOST:8080/Users?access_token=YOUR_ACCESSTOKEN&filter=userName%20eq%20"TheUserName"%20or%20userName%20eq%20"TheUserName1"

http://OSIAM_HOST:8080/Users?access_token=YOUR_ACCESSTOKEN&filter=(userName%20eq%20"TheUserName"%20or%20userName%20eq%20"TheUserName1")%20and%20meta.created%20gt%20"2013-05-23T13:12:45.672#;4302:00"
```

Also the "NOT" operator is supported. The parentheses are required and not
optional. The "NOT" can also include filters already combined with "AND" and "OR".

```http
http://OSIAM_HOST:8080/Users?access_token=YOUR_ACCESSTOKEN&filter=active%20eq%20"true"%20and%20not%20(groups.display%20eq%20"TheGroupName")
```

#### Limiting the output to predefined attributes

It is possible to search and limit the output to a the given list of
attributes. To define more than one separate them with comma using the
`attributes` parameter.

```http
http://OSIAM_HOST:8080/Users?access_token=YOUR_ACCESSTOKEN&attributes=userName,displayName,meta.created
```

#### Sorting

To sort the results ascending or descending by a given attribute use the
following parameters:
* sortOrder - ascending and descending. Default is ascending
* sortBy - the attribute so sort by. For example "userName". The default is
"id"

```http
http://OSIAM_HOST:8080/Users?access_token=YOUR_ACCESSTOKEN&sortBy=meta.created&sortOrder=ascending

http://OSIAM_HOST:8080/Users?access_token=YOUR_ACCESSTOKEN&sortBy=meta.created&sortOrder=descending
```

#### Paging

The paging is done via two parameters that limit the output shown per page and
define the starting point using the following parameters:
* count - will limit the items per page to the given value. Default is 100
* startIndex - will define the start index of the search. Default is 0

To paginate through the results increase the startIndex to the next desired
position.

```http
http://OSIAM_HOST:8080/Users?access_token=$YOUR_ACCESSTOKEN&count=5&startIndex=0

http://OSIAM_HOST:8080/Users?access_token=$YOUR_ACCESSTOKEN&count=5&startIndex=5
```

### User management

This section describes the handling of user with OSIAM.

#### Retrieving a single User

Retrieving a single user is done by sending a GET request to the `/Users` URL providing the users id

```http
http://OSIAM_HOST:8080/Users/ID
```

The response contains the SCIM compliant record of the user from the OSIAM
database.

```sh
curl -i -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" -X GET http://localhost:8080/Users/$ID
```
See [SCIMv2 specification]
(http://tools.ietf.org/html/draft-ietf-scim-api-02#section-3.2.1) for further
details.

##### Filtering the output with predefined attributes

It is possible to filter the returned resource by a list of
attributes. To define more than one separate them with comma using the
`attributes` parameter.

```sh
curl -i -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" -X GET http://localhost:8080/Users/$ID?attributes=userName,meta.created
```

#### Create a new User

To create a new user you need to send the user input as JSON via POST to the
URL

```http
http://OSIAM_HOST:8080/Users
```

the response will be the created user.

e.g.:

```sh
curl -i -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" -X POST http://localhost:8080/Users -d '{"schemas":["urn:ietf:params:scim:schemas:core:2.0:User"],"externalId":"external_id","userName":"arthur","password":"dent"}'
```

See [scim 2 rest spec]
(http://tools.ietf.org/html/draft-ietf-scim-api-02#section-3.1) for further
details.

##### Filtering the output with predefined attributes

It is possible to filter the returned resource by a list of
attributes. To define more than one separate them with comma using the
`attributes` parameter.

```sh
curl -i -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" -X POST http://localhost:8080/Users?attributes=userName,meta.created -d '{"schemas":["urn:ietf:params:scim:schemas:core:2.0:User"],"externalId":"external_id","userName":"arthur","password":"dent"}'
```

#### Replace an existing User
To replace an existing user you need to send the user input as json via put to
the url

```http
http://OSIAM_HOST:8080/Users/$ID
```

the response will be the replaced user in json format.

e.g.:
```sh
curl -i -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" -X PUT http://localhost:8080/Users/$ID -d '{"schemas":["urn:ietf:params:scim:schemas:core:2.0:User"], "externalId":"new_external_id","userName":"arthur","password":"dent"}'
```

See [scim 2 rest spec]
(http://tools.ietf.org/html/draft-ietf-scim-api-02#section-3.3.1) for further
details.

##### Filtering the output with predefined attributes

It is possible to filter the returned resource by a list of
attributes. To define more than one separate them with comma using the
`attributes` parameter.

```sh
curl -i -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" -X PUT http://localhost:8080/Users?attributes=userName,meta.created -d '{"schemas":["urn:ietf:params:scim:schemas:core:2.0:User"],"externalId":"ne_externalId","userName":"arthur","password":"dent"}'
```

#### Update a User
To update an existing user you need to send the fields you which to update or
delete as json via patch to the url

```http
http://OSIAM_HOST:8080/Users/$ID
```

the response is the updated user in JSON format.

e.g.:
```sh
curl -i -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" -X PATCH http://localhost:8080/Users/$ID -d '{"schemas":["urn:ietf:params:scim:schemas:core:2.0:User"], "externalId":"new_external_id"}'
```
See [scim 2 rest spec]
(http://tools.ietf.org/html/draft-ietf-scim-api-02#section-3.3.2) for further
details.

#### Delete a User
To delete an existing user you need to call the url via delete

```http
http://OSIAM_HOST:8080/Users/$ID
```

the response will be the http status.

e.g.:

```sh
curl -i -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" -X DELETE http://localhost:8080/Users/$ID
```

### Get the Authenticated User
To get the user authenticated by a given access token it is possible to to query the `/Me` url:

```sh
curl -i -H "Accept: application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" -X GET http://localhost:8080/Me
```

The response is the json representation of the user currently authenticated with the provided access token.

##### Filtering the output with predefined attributes

It is possible to filter the returned resource by a list of
attributes. To define more than one separate them with comma using the
`attributes` parameter.

```sh
curl -i -H "Accept: application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" -X GET http://localhost:8080/Me?attributes=displayName,meta.created
```


### Group management

This section describes the handling of user in the osiam context.

#### Get a single Group

To get a single group you need to send a GET request to the url

```http
http://OSIAM_HOST:8080/Groups/$ID
```

the response will be a osiam group in json format.

e.g.:

```sh
curl -i -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" -X GET http://localhost:8080/Groups/$ID
```

See [scim 2 rest spec]
(http://tools.ietf.org/html/draft-ietf-scim-api-02#section-3.2.1) for further
details.

##### Filtering the output with predefined attributes

It is possible to filter the returned resource by a list of
attributes. To define more than one separate them with comma using the
`attributes` parameter.

```sh
curl -i -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" -X GET http://localhost:8080/Groups/$ID?attributes=displayName,meta.created
```

#### Create a new Group
To create a new group you need to send the group input as json via post to the
url

```http
http://OSIAM_HOST:8080/Groups
```

the response will be the created group in json format.

e.g.:

```sh
curl -i -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" -X POST http://localhost:8080/Groups -d '{"schemas":["urn:ietf:params:scim:schemas:core:2.0:Group"],"displayName":"adminsGroup"}'
```

See [scim 2 rest spec]
(http://tools.ietf.org/html/draft-ietf-scim-api-02#section-3.1) for further
details.

##### Filtering the output with predefined attributes

It is possible to filter the returned resource by a list of
attributes. To define more than one separate them with comma using the
`attributes` parameter.

```sh
curl -i -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" -X POST http://localhost:8080/Groups?attributes=deisplayName,meta.created -d '{"schemas":["urn:ietf:params:scim:schemas:core:2.0:Group"],"displayName":"adminsGroup"}'
```

#### Replace an existing Group
To replace a group you need to send the group input as json via put to the url

```http
http://OSIAM_HOST:8080/Groups/$ID
```

the response will be the replaced group in json format.

e.g.:

```sh
curl -i -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" -X PUT http://localhost:8080/Groups/$ID -d '{"schemas":["urn:ietf:params:scim:schemas:core:2.0:Group"], "displayName":"Group1"}'
```
See [scim 2 rest spec]
(http://tools.ietf.org/html/draft-ietf-scim-api-02#section-3.3.1) for further
details.

##### Filtering the output with predefined attributes

It is possible to filter the returned resource by a list of
attributes. To define more than one separate them with comma using the
`attributes` parameter.

```sh
curl -i -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" -X PUT http://localhost:8080/Groups?attributes=deisplayName,meta.created -d '{"schemas":["urn:ietf:params:scim:schemas:core:2.0:Group"],"displayName":"newDisplayName"}'
```
#### Update a Group
To update a group you need to send the fields you which to update oder delete
as json via patch to the url

```http
http://OSIAM_HOST:8080/Groups/$ID
```

the response will be the updated group in json format.

e.g.:

```sh
curl -i -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" -X PATCH http://localhost:8080/Groups/$ID -d '{"schemas":["urn:ietf:params:scim:schemas:core:2.0:Group"], "displayName":"adminsGroup"}'
```
See [scim 2 rest spec]
(http://tools.ietf.org/html/draft-ietf-scim-api-02#section-3.3.2) for further
details.

#### Delete a Group
To delete a group you need to call the url via delete

```http
http://OSIAM_HOST:8080/Groups/$ID
```

the response will be status.

e.g.:

```sh
curl -i -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" -X DELETE http://localhost:8080/Groups/$ID
```
