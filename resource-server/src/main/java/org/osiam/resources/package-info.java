/**
 *  <p>org.osiam.resources is a group of groups related the resource-server functionality.</p>
 *
 *  <p>The resource-server of OSIAM is build on top of SCIM v2 (see http://tools.ietf.org/html/draft-ietf-scim-api-01,
 *  http://tools.ietf.org/html/draft-ietf-scim-core-schema-01 for further details on the standard).</p>
 *
 *  <p>It has also some additional functionalities which are:</p>
 *
 *  <p>- a small client management, to be able to handle more than one client within the OAuth2 flow,</p>
 *
 *  <p>- the functionality to be a fake facebook site, so that facebook-connector could be used with OSIAM.</p>
 *
 *
 *  <p>The api is build on http and contains:</p>
 *
 *  <p>/ - org.osiam.resources.controller.RootController - would be a resource independent search but it is currently
 *  disabled.</p>
 *
 *  <p>/Client - org.osiam.resources.controller.ClientManagementController - is a Controller to create, delete and get
 *  clients.</p>
 *
 *  <p>/Group - org.osiam.resources.controller.GroupController - is a Controller to create, replace, modify, get,
 *  delete and search groups.</p>
 *
 *  <p>/me - org.osiam.resources.controller.MeController - is a Facebook /me clone to get name, email and id of an user.</p>
 *
 *  <p>/User - org.osiam.resources.controller.UserController - is a Controller to create, replace, modify, get,</p>
 *  delete and search user.
 *
 * <p>/ServiceProviderConfig - org.osiam.resources.controller.ServiceProviderConfigController is a controller to get
 * information about the running OSIAM instance.</p>
 *
 */
package org.osiam.resources;