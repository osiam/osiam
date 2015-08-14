# HTTP API Documentation

## Table of contents

- [Basics](#basics)
- [Access](#access)
- [SCIM](#scim)
  - [OSIAM Implementation Specifics](#osiam-implementation-specifics)
  - [OSIAM's Known Limitations](#osiams-known-limitations)
  - [Unsupported Interfaces](#unsupported-interfaces)
  - [Service provider configuration](#service-provider-configuration)
  - [Search](#search)
    - [Search filtering including logical operators](#search-filtering-including-logical-operators)
    - [Limiting the output to predefined attributes] (#limiting-the-output-to-predefined-attributes)
    - [Sorting](#sorting)
    - [Paging](#paging)
  - [User management](#user-management)
    - [Get a single User] (#get-a-single-user)
    - [Get the User of the current accessToken](#get-the-user-of-the-current-accesstoken)
    - [Create a new User] (#create-a-new-user)
    - [Replace an existing User] (#replace-an-existing-user)
    - [Update a User](#update-a-user)
    - [Delete a User](#delete-a-user)
  - [Group management](#group-management)
    - [Get a single Group](#get-a-single-group)
    - [Create a new Group](#create-a-new-group)
    - [Replace an existing Group](#replace-an-existing-group)
    - [Update a Group](#update-a-group)
    - [Delete a Group](#delete-a-group)
- [Other](#other)
  - [Facebook's me](#facebooks-me)

## Basics

The resource interface - also called functional interface - implements the
SCIMv2 standard. It's used for management of the user data (identities) within
OSIAM.

The user needs to be stored in the OSIAM database, including an authentication
parameter (e.g. a password).

## Access

All SCIM endpoints are secured with OAuth 2.0, so you will at least have to
send an access token in order get the expected response:

```sh
curl -H "Authorization: Bearer YOUR_ACCESS_TOKEN" http://OSIAMHOST:8080/osiam-resource-server/Users/ID
```

The parameters are:
* YOUR_ACCESS_TOKEN - the access token that was provided through the OAuth 2.0
  grant
* ID - The ID of the user record you want to retrieve

You can retrieve an access token if you by following [these instructions]
(https://github.com/osiam/osiam/blob/master/docs/detailed-reference-installation.md#customized-setup).

Most endpoints can only be accessed with a proper authorization. The access
tokens are bound to a client or a user and need specific scopes, which are
[defined per client in the auth-server]
(https://github.com/osiam/auth-server/blob/master/docs/api_documentation.md#supported-scopes).

## SCIM

The resource-server is part of the [SCIM v2](http://www.simplecloud.info/)
implementation

### OSIAM Implementation Specifics
* The default maxResults of a filter is 100
* xmlDataFormat is not supported

### OSIAM's Known Limitations
* the [etag](http://tools.ietf.org/html/draft-ietf-scim-api-02#section-3.11) is
not supported yet, but it is already in OSIAMs backlog

### Unsupported Interfaces
* SCIM bulk actions: Is not yet implemented, but it is on the roadmap
* Cross ressource types search from the server's root: We do not understand the
use case for this functionality at the moment, please let us know if you have a
reasonable use case.

### Service provider configuration

The following URI provides the service provider configuration of the addressed
server:

```http
http://OSIAMHOST:8080/osiam-resource-server/ServiceProviderConfigs
```

### Search
OSIAM supports search on both SCIM resource types, user and group.
* Users: 
```
http://OSIAMHOST:8080/osiam-resource-server/Users
```
* Groups
```
http://OSIAMHOST:8080/osiam-resource-server/Groups
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
http://OSIAMHOST:8080/osiam-resource-server/Users?access_token=YOUR_ACCESSTOKEN&filter=userName%20eq%20"TheUserName"

http://OSIAMHOST:8080/osiam-resource-server/Users?access_token=YOUR_ACCESSTOKEN&filter=userName%20co%20"someValue"

http://OSIAMHOST:8080/osiam-resource-server/Users?access_token=YOUR_ACCESSTOKEN&filter=userName%20sw%20"someValue"

http://OSIAMHOST:8080/osiam-resource-server/Users?access_token=YOUR_ACCESSTOKEN&filter=displayName%20pr

http://OSIAMHOST:8080/osiam-resource-server/Users?access_token=YOUR_ACCESSTOKEN&filter=meta.created%20gt%20"2013-05-23T13:12:45.672#;4302:00"

http://OSIAMHOST:8080/osiam-resource-server/Users?access_token=YOUR_ACCESSTOKEN&filter=meta.created%20ge%20<an existing time>

http://OSIAMHOST:8080/osiam-resource-server/Users?access_token=YOUR_ACCESSTOKEN&filter=meta.created%20lt%20"2013-05-23T13:12:45.672#;4302:00"

http://OSIAMHOST:8080/osiam-resource-server/Users?access_token=YOUR_ACCESSTOKEN&filter=meta.created%20le%20<an existing time>
```

Additionally "AND" and "OR" as logical operators are supported, including
grouping with parentheses.

```http
http://OSIAMHOST:8080/osiam-resource-server/Users?access_token=YOUR_ACCESSTOKEN&filter=userName%20eq%20"TheUserName"%20and%20meta.created%20lt%20"2013-05-23T13:12:45.672#;4302:00"

http://OSIAMHOST:8080/osiam-resource-server/Users?access_token=YOUR_ACCESSTOKEN&filter=userName%20eq%20"TheUserName"%20or%20userName%20eq%20"TheUserName1"

http://OSIAMHOST:8080/osiam-resource-server/Users?access_token=YOUR_ACCESSTOKEN&filter=(userName%20eq%20"TheUserName"%20or%20userName%20eq%20"TheUserName1")%20and%20meta.created%20gt%20"2013-05-23T13:12:45.672#;4302:00"
```

Also the "NOT" operator is supported. The parentheses are required and not
optional. The "NOT" can also include filters already combined with "AND" and "OR".

```http
http://OSIAMHOST:8080/osiam-resource-server/Users?access_token=YOUR_ACCESSTOKEN&filter=active%20eq%20"true"%20and%20not%20(groups.display%20eq%20"TheGroupName")
```

#### Limiting the output to predefined attributes

It is possible to search and limit the output to a the given list of
attributes. To define more than one separate them with comma using the
`attributes` parameter.

```http
http://OSIAMHOST:8080/osiam-resource-server/Users?access_token=YOUR_ACCESSTOKEN&attributes=userName,displayName,meta.created
```

#### Sorting

To sort the results ascending or descending by a given attribute use the
following parameters:
* sortOrder - ascending and descending. Default is ascending
* sortBy - the attribute so sort by. For example "userName". The default is
"id"

```http
http://OSIAMHOST:8080/osiam-resource-server/Users?access_token=YOUR_ACCESSTOKEN&sortBy=meta.created&sortOrder=ascending

http://OSIAMHOST:8080/osiam-resource-server/Users?access_token=YOUR_ACCESSTOKEN&sortBy=meta.created&sortOrder=descending
```

#### Paging

The paging is done via two parameters that limit the output shown per page and
define the starting point using the following parameters:
* count - will limit the items per page to the given value. Default is 100
* startIndex - will define the start index of the search. Default is 0

To paginate through the results increase the startIndex to the next desired
position.

```http
http://OSIAMHOST:8080/osiam-resource-server/Users?access_token=$YOUR_ACCESSTOKEN&count=5&startIndex=0

http://OSIAMHOST:8080/osiam-resource-server/Users?access_token=$YOUR_ACCESSTOKEN&count=5&startIndex=5
```

### User management

This section will describe the handling of user with OSIAM.

#### Get a single User

To get a single user you need to send a GET request to the URL providing the
user's ID

```http
http://OSIAMHOST:8080/osiam-resource-server/Users/ID
```

The response contains the SCIM compliant record of the user from the OSIAM
database.

```sh
curl -i -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" -X GET http://localhost:8080/osiam-resource-server/Users/$ID
```
See [SCIMv2 specification]
(http://tools.ietf.org/html/draft-ietf-scim-api-02#section-3.2.1) for further
details.

#### Get the User of the current accessToken

To know the user of the actual accessToken OSIAM implemented an /me interface.
For more detail information please look [here](#facebooks-me).

#### Create a new User

To create a new user you need to send the user input as JSON via POST to the
URL

```http
http://OSIAMHOST:8080/osiam-resource-server/Users
```

the response will be the created user.

e.g.:

```sh
curl -i -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" -X POST http://localhost:8080/osiam-resource-server/Users -d '{"schemas":["urn:ietf:params:scim:schemas:core:2.0:User"],"externalId":"external_id","userName":"arthur","password":"dent"}'
```

See [scim 2 rest spec]
(http://tools.ietf.org/html/draft-ietf-scim-api-02#section-3.1) for further
details.

#### Replace an existing User
To replace an existing user you need to send the user input as json via put to
the url

```http
http://OSIAMHOST:8080/osiam-resource-server/Users/$ID
```

the response will be the replaced user in json format.

e.g.:
```sh
curl -i -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" -X PUT http://localhost:8080/osiam-resource-server/Users/$ID -d '{"schemas":["urn:ietf:params:scim:schemas:core:2.0:User"], "externalId":"new_external_id","userName":"arthur","password":"dent"}'
```

See [scim 2 rest spec]
(http://tools.ietf.org/html/draft-ietf-scim-api-02#section-3.3.1) for further
details.

#### Update a User
To update an existing user you need to send the fields you which to update or
delete as json via patch to the url

```http
http://OSIAMHOST:8080/osiam-resource-server/Users/$ID
```

the response will be the updated user in json format.

e.g.:
```sh
curl -i -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" -X PATCH http://localhost:8080/osiam-resource-server/Users/$ID -d '{"schemas":["urn:ietf:params:scim:schemas:core:2.0:User"], "externalId":"new_external_id"}'
```
See [scim 2 rest spec]
(http://tools.ietf.org/html/draft-ietf-scim-api-02#section-3.3.2) for further
details.

#### Delete a User
To delete an existing user you need to call the url via delete

```http
http://OSIAMHOST:8080/osiam-resource-server/Users/$ID
```

the response will be the http status.

e.g.:

```sh
curl -i -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" -X DELETE http://localhost:8080/osiam-resource-server/Users/$ID
```

### Group management

This section will describe the handling of user in the osiam context.

#### Get a single Group

To get a single group you need to send a GET request to the url

```http
http://OSIAMHOST:8080/osiam-resource-server/Groups/$ID
```

the response will be a osiam group in json format.

e.g.:

```sh
curl -i -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" -X GET http://localhost:8080/osiam-resource-server/Groups/$ID
```

See [scim 2 rest spec]
(http://tools.ietf.org/html/draft-ietf-scim-api-02#section-3.2.1) for further
details.

#### Create a new Group
To create a new group you need to send the group input as json via post to the
url

```http
http://OSIAMHOST:8080/osiam-resource-server/Groups
```

the response will be the created group in json format.

e.g.:

```sh
curl -i -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" -X POST http://localhost:8080/osiam-resource-server/Groups -d '{"schemas":["urn:ietf:params:scim:schemas:core:2.0:Group"],"displayName":"adminsGroup"}'
```

See [scim 2 rest spec]
(http://tools.ietf.org/html/draft-ietf-scim-api-02#section-3.1) for further
details.

#### Replace an existing Group
To replace a group you need to send the group input as json via put to the url

```http
http://OSIAMHOST:8080/osiam-resource-server/Groups/$ID
```

the response will be the replaced group in json format.

e.g.:

```sh
curl -i -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" -X PUT http://localhost:8080/osiam-resource-server/Groups/$ID -d '{"schemas":["urn:ietf:params:scim:schemas:core:2.0:Group"], "displayName":"Group1"}'
```
See [scim 2 rest spec]
(http://tools.ietf.org/html/draft-ietf-scim-api-02#section-3.3.1) for further
details.

#### Update a Group
To update a group you need to send the fields you which to update oder delete
as json via patch to the url

```http
http://OSIAMHOST:8080/osiam-resource-server/Groups/$ID
```

the response will be the updated group in json format.

e.g.:

```sh
curl -i -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" -X PATCH http://localhost:8080/osiam-resource-server/Groups/$ID -d '{"schemas":["urn:ietf:params:scim:schemas:core:2.0:Group"], "displayName":"adminsGroup"}'
```
See [scim 2 rest spec]
(http://tools.ietf.org/html/draft-ietf-scim-api-02#section-3.3.2) for further
details.

#### Delete a Group
To delete a group you need to call the url via delete

```http
http://OSIAMHOST:8080/osiam-resource-server/Groups/$ID
```

the response will be status.

e.g.:

```sh
curl -i -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" -X DELETE http://localhost:8080/osiam-resource-server/Groups/$ID
```

## Other

### Facebooks me

To get information about who granted the access_token you can call:

```http
http://OSIAMHOST:8080/osiam-resource-server/me
```

e.g.:
```sh
curl -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" http://localhost:8080/osiam-resource-server/me
```

The response is like the result of facebooks /me:

```json
{
    "id":"cef9452e-00a9-4cec-a086-d171374ffbef",
    "name":"Mar Issa",
    "first_name":"Issa",
    "last_name":"Mar",
    "link":"not supported.",
    "userName":"admin",
    "gender":"female",
    "email":"mari@ssa.ma",
    "timezone":2,
    "locale":null,
    "verified":true,
    "updated_time":"2011-10-10T00:00:00.000+02:00"
}
```
