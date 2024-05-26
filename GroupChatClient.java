/*
 * Implementation of a two way messaging client in Java
 */

// Package for socket related stuff
import java.net.*;

// Package for I/O related stuff
import java.io.*;


/*
 * This class does all of two way messaging client's job
 * It simultaneously watches both keyboard and socket for input
 *
 * It consists of 2 threads: parent thread (code inside main method)
 * and child thread (code inside run method)
 *
 * Parent thread spawns a child thread and then
 * reads from the socket and writes to the screen
 *
 * Child thread reads from the keyboard and writes to socket
 *
 * Since a thread is being created with this class object,
 * this class declaration includes "implements Runnable"
 */
public class GroupChatClient implements Runnable {
    private BufferedReader fromUserReader;
    private PrintWriter toSockWriter;

    public GroupChatClient(BufferedReader reader, PrintWriter writer) {
        fromUserReader = reader;
        toSockWriter = writer;
    }

    public void run() {
        try {
            while (true) {
                String line = fromUserReader.readLine();
                if (line == null) {
                    System.out.println("*** Client closing connection");
                    break;
                }
                toSockWriter.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


public static void main(String args[])
	{
		// Client needs server's contact information and user name
		if (args.length != 2) {
			System.out.println("usage: java TwoWayAsyncMesgClient <host> <port>");
			System.exit(1);
		}

		// Connect to the server at the given host and port
		Socket sock = null;
        try {
            sock = new Socket(args[0], Integer.parseInt(args[1]));
            System.out.println("Connected to server at " + args[0] + ":" + args[1]);

            // Prompt the user for their name and send it to the server
            System.out.print("Enter your name: ");
            BufferedReader nameReader = new BufferedReader(new InputStreamReader(System.in));
            String userName = nameReader.readLine();
    
            // send user's name to the server
            PrintWriter toSockWriter = new PrintWriter(sock.getOutputStream(), true);
            toSockWriter.println(userName);

            // Create a thread to read user input and send it to the server
            BufferedReader fromUserReader = new BufferedReader(new InputStreamReader(System.in));
            Thread clientThread = new Thread(new GroupChatClient(fromUserReader, toSockWriter));
            clientThread.start();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        try {
            BufferedReader fromSockReader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            // keep reading whatever server sent
            while (true) {
                String line = fromSockReader.readLine();
                if (line == null) {
                    System.out.println("*** Server closed connection");
                    break;
                }
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}