# echo-nest-ws
Webservice to be hosted on heroku for use to use a Nest Thermostat with the Amazon Echo

#Heroku
If you are unfamiliar with heroku, I recommend their getting started guide for help: https://devcenter.heroku.com/articles/getting-started-with-java#introduction

##Config Variables
When deploying to Heroku, the following config variables are required

-NEST_CLIENT_ID: Can be found in the Nest DeveloperLabs client page: https://developer.nest.com/clients
-NEST_CLIENT_SECRET: Can be found in the Nest DeveloperLabs client page: https://developer.nest.com/clients
-NEST_THERMOSTAT_ID: You'll need to view the data model for your thermostat, and copy it to this variable. See Appendix for more information.

#Building
Project is built using Maven.  It's intended to run on Heroku, if you want to run the webserver locally you'll have to read Heroku's documentation to run projects locally.

#Set Up
Once your webserver is running on Heroku, you can provide the following endpoint as a callback for Nest Labs to automatically store a authorization code for a given 'State ID'.
Anytime you want to use the same code, all you'll need to do is provide the same state ID.
In the Nest client settings, provide this URL for the authorization callback: https://\<app-name\>.herokuapp.com/nest/{stateId}/auth/callback

After the callback URL is configured, I manually performed the oAuth steps described by Nest.  Once authorized, Nest communicates the auth code back to the server, and stores it for a particular 'stateId'

At this point, you can start interacting with your Nest thermostat from a webapp.

#Use From Echo
I've also integrated with the echo, you can view the required files in the echo-nest project.  Once you have the AWS Lambda script running for echo, you'll need to provide the lambda with the URL to the webserver.


#Appendix
##Retrieval of thermostat ID
You'll need to retrieve the access code, either by viewing the logs of the webserver after setting up the callback or retrieve it manually.

1) https://home.nest.com/login/oauth2?client_id=[CLIENT ID] => receive authorization code/PIN

2) https://api.home.nest.com/oauth2/access_token?client_id=[CLIENT ID]&client_secret=[CLIENT SECRET]&grant_type=authorization_code&code=[CODE RECEIVED FROM STEP 1]

3) If done correctly, this will return a JSON object containing the code for your client.

4) You can then view your devices by going to https://developer-api.nest.com/devices/thermostats?auth=[AUTH CODE FROM STEP 2]

5) If you have more than one thermostat, pick one to use with the Echo, multiple Nest's are not supported.  It is the 'device_id' field


