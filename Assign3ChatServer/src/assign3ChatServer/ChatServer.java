package assign3ChatServer;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.stream.Collectors;

public class ChatServer {

    private int port;	// Server port
    private List<ClientHandler> clients; // List of connected clients

    // ChatServer Constructor
    public ChatServer(int port) {
        this.port = port;	
        clients = new ArrayList<>();
    }
    // Main method
    public static void main(String[] args) {
        int port = 2620;
        ChatServer server = new ChatServer(port);
        server.start();	// Start the chat server
    }
    // Start the server and listen for incoming connections
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Chat server is running on port " + port);

            while (true) {
            	// Accept incoming client connections
                Socket clientSocket = serverSocket.accept();
                // Create a new ClientHandler for the connected client
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandler.start();	// Start a new thread for each client
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Handle incoming commands
    private void processCommand(String command, ClientHandler sender) {
    	// Code to process the commands sent by clients
        String[] tokens = command.split(" ", 2);
        String cmd = tokens[0];

        switch (cmd) {
            case "USERNAME":
                String username = tokens[1];
                if (isUsernameAvailable(username)) {
                    sender.setUsername(username);
                    clients.add(sender);
                    broadcastMessage("NEWUSER " + username);
                    // Send updated client list to all clients
                    broadcastMessage("CLIENTLIST " + getClientListAsString());
                } else {
                    sender.sendMessage("ERROR Username already taken.");
                }
                break;
            case "MESSAGE":
                String message = tokens[1];
                broadcastMessage("MESSAGE " + sender.getUsername() + ": " + message);
                break;
            case "LOGOUT":
                clients.remove(sender);
                broadcastMessage("REMOVEUSER " + sender.getUsername());
                // Send updated client list to all clients
                broadcastMessage("CLIENTLIST " + getClientListAsString());
                break;
            default:
                System.out.println("Unknown command: " + cmd);
                break;
        }
    }
    // Get a comma-separated list of client usernames
    private String getClientListAsString() {
    	 // Code to convert the list of clients to a comma-separated string
        return clients.stream().map(ClientHandler::getUsername).collect(Collectors.joining(","));
    }
    // Broadcast a message to all connected clients
    public void broadcastMessage(String message) {
    	// Code to send a message to all connected clients
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }
    
    // Remove a client from the clients list
    public void removeClient(ClientHandler clientHandler) {
    	// Code to remove a client from the list and print a message
        clients.remove(clientHandler);
        System.out.println("Client " + clientHandler.username + " has disconnected.");
    }

    // Check if the provided username is available
    public boolean isUsernameAvailable(String username) {
    	// Code to check if the given username is available
        return clients.stream().noneMatch(client -> client.getUsername().equalsIgnoreCase(username));
    }

    // ClientHandler class to handle individual clients
    private class ClientHandler extends Thread {
    	//Attributes for client handling
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private String username;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }
        // Set the username for the client
        public void setUsername(String username) {
        	//Code for setting the username
            this.username = username;
        }
        // Get the username for the client
        public String getUsername() {
        	//Code for getting the username
            return username;
        }
        // Send a message to the client
        public void sendMessage(String message) {
        	//Code for sending a message to the client
            out.println(message);
        }

        @Override
        public void run() { // Handles client connection and processing commands
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String line;
                while ((line = in.readLine()) != null) {
                    processCommand(line, this);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                removeClient(this);
                closeResources();
            }
        }

        // Close resources (clientSocket, OutputStream, InputStreamReader)
        private void closeResources() {
            try {
                clientSocket.close();
                out.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}