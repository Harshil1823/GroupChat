import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class GroupChatServer {
    private static Map<String, PrintWriter> clientWriters = new HashMap<>();

    public static void main(String args[]) {
        if (args.length != 1) {
            System.out.println("usage: java GroupChatServer <port>");
            System.exit(1);
        }

        int serverPort = Integer.parseInt(args[0]);

        try {
            ServerSocket serverSocket = new ServerSocket(serverPort);
            System.out.println("Waiting for clients...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket);

                // Create a PrintWriter for this client
                PrintWriter clientWriter = new PrintWriter(clientSocket.getOutputStream(), true);

                // Create a BufferedReader for reading from the client
                BufferedReader clientReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                // Get the client's name (assumes the client sends its name first)
                String clientName = clientReader.readLine();
                if (clientName == null || clientName.isEmpty()) {
                    System.out.println("Client didn't provide a name. Disconnecting.");
                    clientSocket.close();
                    continue;
                }

                // Store the client's name and writer
                clientWriters.put(clientName, clientWriter);

                // Start a new thread to handle this client
                Thread clientThread = new Thread(() -> handleClient(clientReader, clientName));
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void handleClient(BufferedReader clientReader, String clientName) {
        try {
            while (true) {
                String message = clientReader.readLine();
                if (message == null) {
                    System.out.println("*** " + clientName + " disconnected");
					// Broadcast a message to inform other clients
					for (PrintWriter writer : clientWriters.values()) {
						writer.println(clientName + " has left the chat.");
					}
                    clientWriters.remove(clientName);
                    break;
                }

                // Broadcast the message to all connected clients except the sender
				for (Map.Entry<String, PrintWriter> entry : clientWriters.entrySet()) {
					if (!entry.getKey().equals(clientName)) {
						entry.getValue().println(clientName + ": " + message);
					}
				}
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
