/**
 * <p>org.osiam.security is a group of groups with the focus on security.
 *
 * In OSIAM authentication and authorization are done by Spring-Security, there are some modification.
 *
 * OSIAM does use the OAuth2 authorization-code flow protocol delivered by spring-security-oauth2 and has a own facebook connect like implementation
 * to be able to federate an existing OSIAM user to an other system by calling /me with an email scope.
 *
 * Spring Username Password authentication does use the SHA-512 algorithm the hash the passwords of an user and the
 * internal id for salting it.</p>
 *
 * <p>To be able to get and authorization code you need to redirect an user of your client to:</p>
 *
 * <p>/oauth/authorize?client_id=CLIENT_ID&response_type=code</p>
 *
 * <p>and the excahnge the delivered auth-code to an access_token you need to call either:</p>
 *
 * <p>/osiam-server/oauth/token</p>
 *
 * <p>for the general json access_token or:</p>
 *
 * <p>/osiam-server/fb/oauth/access_token</p>
 *
 * <p>for the facebook like key value pairs.</p>
 *
 */
package org.osiam.security;