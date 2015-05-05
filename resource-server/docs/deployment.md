# Deployment into the application server

To deploy the OSIAM resource-server in the previously installed Tomcat the
downloaded `.war` file needs to be renamed and moved into Tomcat's
webapp directory:

    $ sudo mv resource-server-<VERSION>.war /var/lib/tomcat7/webapps/resource-server.war
