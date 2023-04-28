package assign3ChatClient;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.SystemColor;
import javax.swing.border.EtchedBorder;
import javax.swing.JTextField;
import java.awt.Font;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

public class ChatClientGUI {
	//		GUI components declaration																					//
	private JFrame frmFredbclientchat;																					//
	private static JPanel panelChatBackground;																			//
	private static JLabel lblMembersConnected, lblUserName, lblDisplayNickName, lblStatus;								//
	public JTextField textDefineUsername;																				//
	public JTextArea membersConnectedList, chatPanel, textWriter;														//
	private JButton connectButton, disconnectButton, sendButton;														//
	private JScrollPane scrollPane, scrollPane_1, scrollPane_2;															//
	//																													//
	//Global datatype declaration																						//
	private static final String serverAddress = "25.58.35.232"; //hamachi 													//	
	private static final int serverPORT = 2620;																			//
	private Socket socket;// socket that goes at the server																//
	private PrintWriter out;// print the data over and back over to the server											//
	private BufferedReader in; // read the data that comes from the server												//
	public Boolean isConnected = false; // define if the client is connected or not										//
	public String clientUsername; // client's username																	//
	List<String> clientListedNames = new ArrayList<>(); // create a List<String> to hold the names of connected clients	//
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new ChatClientGUI().frmFredbclientchat.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ChatClientGUI() {
		initialize();
	}

	//////////////////////////////////////////////
	// Start of GUI configuration and setting up//
	//////////////////////////////////////////////
	private void initialize() {
		frmFredbclientchat = new JFrame();
		frmFredbclientchat.setBackground(new Color(128, 0, 0));
		frmFredbclientchat.setForeground(new Color(0, 128, 0));
		frmFredbclientchat.setTitle("FredB_Client_Chat");
		frmFredbclientchat.setFont(new Font("Dubai", Font.BOLD, 12));
		frmFredbclientchat.setBounds(100, 100, 710, 542);
		frmFredbclientchat.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmFredbclientchat.getContentPane().setLayout(null);

		panelChatBackground = new JPanel();
		panelChatBackground.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panelChatBackground.setBackground(SystemColor.activeCaption);
		panelChatBackground.setBounds(0, 0, 694, 503);
		frmFredbclientchat.getContentPane().add(panelChatBackground);
		panelChatBackground.setLayout(null);

		lblMembersConnected = new JLabel("Members Connected");
		lblMembersConnected.setFont(new Font("SansSerif", Font.PLAIN, 16));
		lblMembersConnected.setBounds(524, 9, 154, 28);
		panelChatBackground.add(lblMembersConnected);

		lblUserName = new JLabel("Client Name:");
		lblUserName.setFont(new Font("SansSerif", Font.PLAIN, 18));
		lblUserName.setBounds(6, 10, 112, 26);
		panelChatBackground.add(lblUserName);

		sendButton = new JButton("Send Message");
		sendButton.setBackground(new Color(128, 128, 128));
		sendButton.setFont(new Font("Times New Roman", Font.BOLD, 14));
		sendButton.setForeground(new Color(0, 128, 128));
		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendMessage();

			}
		});
		sendButton.setBounds(515, 401, 154, 78);
		panelChatBackground.add(sendButton);

		textDefineUsername = new JTextField();
		textDefineUsername.setBounds(117, 11, 163, 26);
		panelChatBackground.add(textDefineUsername);
		textDefineUsername.setColumns(10);

		scrollPane_1 = new JScrollPane();
		scrollPane_1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane_1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane_1.setBounds(515, 72, 163, 317);
		panelChatBackground.add(scrollPane_1);

		membersConnectedList = new JTextArea();
		membersConnectedList.setForeground(SystemColor.controlText);
		membersConnectedList.setBackground(SystemColor.control);
		membersConnectedList.setEditable(false);
		scrollPane_1.setViewportView(membersConnectedList);

		scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(22, 68, 482, 317);
		panelChatBackground.add(scrollPane);

		chatPanel = new JTextArea();
		chatPanel.setText("To connectd to the server, 1st type your Client name on the field and press the Connect Button");
		chatPanel.setEditable(false);
		scrollPane.setViewportView(chatPanel);
		chatPanel.setLineWrap(true);

		scrollPane_2 = new JScrollPane();
		scrollPane_2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane_2.setBounds(22, 404, 482, 75);
		panelChatBackground.add(scrollPane_2);
		textWriter = new JTextArea();
		scrollPane_2.setViewportView(textWriter);

		connectButton = new JButton("Connect");
		connectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (isConnected == false) {
					
					// Get the username from the text field
					clientUsername = textDefineUsername.getText();
					if (clientUsername.isEmpty()) {
						lblStatus.setText("Status: Error - You must enter a username.");
					} else {
						connect(serverAddress, serverPORT);
					}
				}
			}
		});
		connectButton.setForeground(SystemColor.inactiveCaptionBorder);
		connectButton.setBackground(new Color(0, 102, 51));
		connectButton.setBounds(292, 15, 100, 21);
		panelChatBackground.add(connectButton);

		disconnectButton = new JButton("Disconnect");
		disconnectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				disconnect();
				isConnected = false;
				clientListedNames.remove(clientUsername);

			}
		});

		disconnectButton.setForeground(SystemColor.inactiveCaptionBorder);
		disconnectButton.setBackground(new Color(255, 102, 0));
		disconnectButton.setBounds(404, 15, 100, 21);
		panelChatBackground.add(disconnectButton);

		lblStatus = new JLabel("Status:");
		lblStatus.setHorizontalAlignment(SwingConstants.LEFT);
		lblStatus.setFont(new Font("SansSerif", Font.PLAIN, 13));
		lblStatus.setBounds(6, 38, 377, 26);
		panelChatBackground.add(lblStatus);

		lblDisplayNickName = new JLabel("Nickname:");
		lblDisplayNickName.setFont(new Font("SansSerif", Font.PLAIN, 14));
		lblDisplayNickName.setBounds(395, 39, 283, 26);
		panelChatBackground.add(lblDisplayNickName);

		sendButton.setEnabled(false);
		textWriter.setEnabled(false);
		disconnectButton.setEnabled(false);
		lblStatus.setText("Status: Not connected");
	} 
	////////////////////////////////////////////
	// End of GUI configuration and setting up//
	////////////////////////////////////////////
	
	
	public void startListeningThread() {
	    // Create a new thread to listen for incoming messages from the server
	    Thread incomingMessagesThread = new Thread(() -> {
	        try {
	            String line;
	            // read lines from the server while the connection is open
	            while ((line = in.readLine()) != null) {
	                // Process each incoming message from the server
	                processIncomingMessage(line);
	            }
	        } catch (IOException e) {
	            // If an error occurs, handle the lost connection and print the stack trace
	            handleLostConnection(); // Handle lost connection
	            e.printStackTrace();
	        }
	    });
	    // Start the thread
	    incomingMessagesThread.start();
	}
	
	public void processIncomingMessage(String line) {
	    // Split the incoming message into tokens
	    String[] tokens = line.split(" ", 2);
	    String command = tokens[0];
	    
	    // Process the command based on its different type
	    switch (command) {
	        case "MESSAGE":	            // Append the message to the chat panel
	            chatPanel.append(tokens[1] + "\n");
	            break;
	        case "NEWUSER":	            // Add the new user to the client list
	            addClientToList(tokens[1]);
	            break;
	        case "REMOVEUSER":	        // Remove the user from the client list
	            removeClientFromList(tokens[1]);
	            break;
	        case "CLIENTLIST":	 		// Update the client list
	            updateClientList(tokens[1]);
	            break;
	        default:					// Unknown command received
	            System.out.println("Unknown command: " + command);
	            break;
	    }
	}
	
	public void updateClientList(String clientList) {
		   	// Split the client list string into individual client names
	    String[] clientNames = clientList.split(",");
	    	// Clear the existing client list
	    clientListedNames.clear();
	    	// Add the received client names to the list
	    for (String name : clientNames) {
	        addClientToList(name);
	    }
	}
	
	public void handleLostConnection() {
			// Check if the client is still connected
	    if (isConnected) {
	        isConnected = false;
	        try {
	        	 // Close resources (socket, output stream, input stream)
	            socket.close();
	            out.close();
	            in.close();
	            
	        	// Update GUI to reflect disconnected state
	            chatPanel.setText("");
				membersConnectedList.setText("");
				lblDisplayNickName.setText("Nickname: ");
				lblStatus.setText("Disconnected from Server");
				disconnectButton.setEnabled(false);
				connectButton.setEnabled(true);
				sendButton.setEnabled(false);
				textWriter.setEnabled(false);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	}

	public void connect(String serverAddress, int serverPort) {
		try {
			// create a new socket and connect to the server
	        socket = new Socket(serverAddress, serverPort); 
	    	// create the output and input streams
	        out = new PrintWriter(socket.getOutputStream(), true);
	        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	        isConnected = true; //boolean as connected
	        // Start listening for incoming messages
			startListeningThread(); 
	        // send the username to the server after a successful connection
	        out.println("USERNAME " + clientUsername);	        
	        lblStatus.setText("Status: Connected");
			lblDisplayNickName.setText("Nickname: " + textDefineUsername.getText());

			// Update the GUI to indicate that the user is connected
			sendButton.setEnabled(true);
			textWriter.setEnabled(true);
			disconnectButton.setEnabled(true);
			connectButton.setEnabled(false);
			lblStatus.setText("Status: You are connected to the server.");			
			chatPanel.setText("");
			isConnected = true;
	    } catch (UnknownHostException e) {
	        chatPanel.setText("Unknown host: " + serverAddress);
	        e.printStackTrace();
	    } catch (IOException e) {
	    	chatPanel.setText("Could not connect to server: " + serverAddress + ":" + serverPort);
	        e.printStackTrace();
	    }
	}	

	public void disconnect() {
		if (isConnected) {
			try {
				// notify the server that this client is disconnecting 
				out.println("LOGOUT " + clientUsername);
				socket.close(); // Close resources (socket, output stream, input stream)
				out.close();
				in.close();
				isConnected = false;
				// update the GUI to show that the client is disconnected from the server
				chatPanel.setText("");
				membersConnectedList.setText("");
				lblDisplayNickName.setText("Nickname: ");
				lblStatus.setText("Disconnected from Server");
				disconnectButton.setEnabled(false);
				connectButton.setEnabled(true);
				sendButton.setEnabled(false);
				textWriter.setEnabled(false);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// Send a message to the server
	public void sendMessage() {
		 // Code to send a message to the server if the client is connected
		if (isConnected) {
			String message = textWriter.getText();
			out.println("MESSAGE " + message);
			textWriter.setText("");
		}
	}
	// Add a client to the client list
	public void addClientToList(String username) {
	    // Code to add a client to the client list and refresh the displayed list
		clientListedNames.add(username);
		refreshClientList();
	}
	// Remove a client from the client list
	public void removeClientFromList(String username) {
	    // Code to remove a client from the client list and refresh the displayed list
	    clientListedNames.remove(username);
	    refreshClientList();
	}
	// Refresh the client list display
	private void refreshClientList() {
	    // Code to rebuild and update the displayed list of connected clients
		   StringBuilder sb = new StringBuilder();
		    for (String name : clientListedNames) {
		        sb.append(name + "\n");
		    }
		    membersConnectedList.setText(sb.toString());
	}
}