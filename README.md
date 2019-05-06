# NBA Fantasy App
this is a NBA fantasy app that will allow you to get access to all infomation about teams and players within the NBA and be able to create, join Fantasy Leagues and draft within them.

By Eddie Kochman, Jacob Curley, Ethan Chen

# Currently Work in Progress
We are still working on this, all features are not yet complete

# launch the EC2 Server
the username is ec2-user and the password should be included in the NBA folder in NBAkey.pem file

the address for the ec2 server:
http://ec2-54-215-176-11.us-west-1.compute.amazonaws.com
and enter "java -jar NBADraftProject-0.0.1.jar" into the terminal to run the server.

Access the http address from Google Chrome:
http://ec2-54-215-176-11.us-west-1.compute.amazonaws.com

# using the application

Register a user, this user can be logged in again once you have registered them.

The homepage will have buttons and most importantly, a global chatroom.

Upon Joining, there would be no leagues, and no teams created yet, so you will need to go to "Create A League" in your homepage.

Insert all the parameters needed to create a league, and upon creating a sucessful league, it should redirect you to your homepage again.

From there you can see your team from the "My Teams" button in the Homepage. To join the draft, click on "Go to Draft".
 
The draft will begin when the Maximum amount of users have joined the league, and they all have chosen to join the draft.

You can now start your draft, click on players you want to draft, turns are enabled.

There is a league specific chat client in the draft window, and also a console log that shows the events of the draft. These are League Specific.

upon each user drafting 14 players(5 for their starting lineup and 9 for subs), the draft will automatically end and insert all of your picks into your table within a SQL server. Thus Concluding the live draft.
