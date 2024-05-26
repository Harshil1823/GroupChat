First and foremost compile the code

so run the following command.

javac GroupChatClient.java GroupChatServer.java

Make sure to run the following commands on it's seperate terminal.

First let's start with GroupChatServer. So, run the following command.
java GroupChatServer 50000


Then you have to have two different clients at minimum in order for it to work.
So, to test and connect the clients to servers run the following command.
java GroupChatClient localhost 50000



Now, whichever clients want to connect they can connect using the upper command.To end the connection from client side press Ctrl + D. And to end the server's connection enter Option + C. 
