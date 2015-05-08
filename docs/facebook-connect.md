# Facebook Connect

OSIAM provides an api, compatible to the Facebook connect api. With this feature, it is possible to use the Facebook connect plugins in several web applications and link them against OSIAM instead of Facebook.

The Facebook connect mode was tested with Liferay 6.1 and its Facebook authentication plugin.
However, this does not mean that the federation does not work with other Facebook connector as well, but we didn't test it. 

If you have any experience with other Facebook connector and OSIAM please feel free to inform us.

## Requirements

You need a running instance of OSIAM and a running Liferay 6.1 System.

Your client needs also the scope "email" in OSIAM.

## Liferay Configuration

To let Liferay think that OSIAM is Facebook you need to go to:

    Control Panel -> Portal Settings -> Authentication -> Facebook

Application ID is your OSIAM client_id,

Application Secret is your OSIAM client secret,

Graph URL is http://<your:host:to:osiam>/osiam-server/

OAuth Authentication URL is http://<your:host:to:osiam>/osiam-server/oauth/authorize?response_type=code

OAuth Token URL is http://<your:host:to:osiam>/osiam-server/fb/oauth/access_token?grant_type=authorization_code

Redirect URL is your redirect URL (e.q. http://localhost:8181/c/login/Facebook_connect_oauth)

## Login

To login in OSIAM you need to press Facebook in the login page, you will then be redirected to 

http://localhost:8080/osiam-server/oauth/authorize?response_type=code

to enter your username and password. if you have entered your credentials you than be redirected to the Liferay server (e.q. http://localhost:8181/c/login/Facebook_connect_oauth) Liferay will than get your information by calling:

http://localhost:8080/osiam-server/me

and create a Liferay account with those information.
