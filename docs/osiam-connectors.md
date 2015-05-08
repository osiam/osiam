# OSIAM Connectors

Connectors are important for OSIAM and the adoption of OSIAM. They make it easier to work with the OSIAM API especially in languages where REST and JSON are not an integral part of the language's concept.

## OSIAM maintained connectors

The OSIAM core developers are maintaining some connectors themselves. Currently there are:

* [Java connector](https://github.com/osiam/connector4java)
* [Python connector](https://github.com/osiam/connector4python)

## Tested connectors

As we are implementing open standards. Connectors that implement these standards as well may also be used to talk to the OSIAM API. For OAuth 2.0 there are a lot of implementations out there, we do not know about other SCIMv2 connectors yet. If you know, let us know :)
We will test external connectors in the future ourselves to maintain an interoperability list and to make OSIAM interoperable to almost every connector that has a proper realization of the standards. In the meantime please let share your experience with external connectors e.g. [through our forum](https://groups.google.com/forum/?fromgroups#!forum/osiam).

## Externally built OSIAM connectors

You may think about building your own connector for OSIAM and making it publicly available using an open source licences - we would recommend the MIT licence of course.
In this case you can find some steps that show the functional sequence of building the connector and that proved helpful in the past:

* OAuth 2.0 login/logout
* SCIM Data queries
* SCIM Data manipulation
* Client management
* Integration into platform specific security frameworks (e.g. for Java this could/would be Spring Security or JAAS)
* HTML for the frontend

If you want to contribute your connector to the OSIAM community, let us get in contact. We are happy to get you an repository under http://github.com/osiam.
