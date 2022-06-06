package tr.edu.ozyegin.chat.client;

import java.awt.*;

import javax.swing.JButton;

import java.awt.Color;

import tr.edu.ozyegin.chat.messages.LoginRequest;
import tr.edu.ozyegin.chat.messages.LoginResponse;
import tr.edu.ozyegin.chat.messages.MessageHistoryResponse;
import tr.edu.ozyegin.chat.messages.MessageResponse;
import tr.edu.ozyegin.chat.messages.PersonListRequest;
import tr.edu.ozyegin.chat.messages.PersonListResponse;

public class ChatClientGUI extends Frame implements ChatMessageListener {

    private MockChatClient client;

    private String username;
    private String password;

    private Frame frame;
    public static JButton connectButton;
    public static Label isConnectedLabel;
    private TextArea messageLog;
    private TextArea userList;

    private LoginScreen loginScreen;

    private int loginCounter = 0;

    ChatClientGUI() {

        this.username = "";
        this.password = "";

        this.client = new MockChatClient();

        String duplicateUsername = username;

        /* Create and set up the frame */
        this.frame = new Frame("OzU Chat");
        Button sendButton = new Button("SEND");
        TextField messageInput = new TextField();
        this.messageLog = new TextArea();
        Color c = new Color(96, 164, 236);
        Color messageList = new Color(189, 189, 189, 255);
        this.userList = new TextArea();
        this.isConnectedLabel = new Label("  X Disconnected");

        this.connectButton = new JButton("Connect");

        /* Material sizing and positioning */
        frame.setLayout(new BorderLayout());
        messageLog.setBounds(30, 150, 370, 510);
        messageLog.setEditable(false);
        messageLog.setSize(370, 510);
        messageInput.setBounds(30, 690, 370, 80);
        sendButton.setBounds(410, 690, 170, 80);
        connectButton.setBounds(250, 70, 190, 40);
        isConnectedLabel.setBounds(5, 30, 590, 100);
        userList.setBounds(410, 150, 170, 510);

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                System.exit(0);
            }
        });

        /* Frame settings */
        frame.add(messageInput);
        frame.add(sendButton);
        frame.add(connectButton);
        frame.add(messageLog);
        frame.add(userList);
        frame.add(isConnectedLabel);

        frame.setSize(600, 800);
        frame.setLayout(null);
        frame.setVisible(true);
        frame.setBackground(c);
        frame.setResizable(false);

        /* Label settings */
        isConnectedLabel.setAlignment(Label.LEFT);
        isConnectedLabel.setFont(new Font("Arial", Font.BOLD, 12));

        /* User List editable/uneditable */
        userList.setEditable(false);

        /* Text Editable/Uneditable */
        messageLog.setEditable(false);
        messageInput.setEditable(false);
        messageInput.setBackground(messageList);
        messageLog.setBackground(messageList);
        userList.setBackground(messageList);

        /* Button settings */
        connectButton.setBackground(Color.WHITE);

        /* Chatting enabled/disabled with connect button */

        connectButton.addActionListener(e -> {

            if (e.getSource() == connectButton) {

                if (connectButton.getLabel().equals("Connect")) {
                    if (this.loginCounter == 0) {

                        this.loginScreen = new LoginScreen();

                        this.username = this.loginScreen.getUsername();
                        this.password = this.loginScreen.getPassword();

                        this.loginCounter++;

                    }

                    if (this.loginScreen.getLoginStatus()) {
                        messageInput.setEditable(true);
                        sendButton.setEnabled(true);
                        connectButton.setLabel("Disconnect");
                        messageInput.setBackground(Color.WHITE);
                        messageLog.setBackground(Color.WHITE);
                        userList.setBackground(Color.WHITE);
                        //label.setText("  \u2713 Connected");
                    }

                }

                else {
                    messageInput.setEditable(false);
                    sendButton.setEnabled(false);
                    messageInput.setBackground(messageList);
                    messageLog.setBackground(messageList);
                    userList.setBackground(messageList);
                    connectButton.setLabel("Connect");
                }

            }
        });

        while (connectButton.getLabel().equals("Connect")) {
            userList.setText("");
            messageLog.setText("");
        }

        connectButton.addActionListener(e -> {
            if (connectButton.getLabel().equals("Disconnect")) {
                isConnectedLabel.setText("  X Not Connected");
                frame.repaint();
            } else {
                isConnectedLabel.setText("  \u2713 Connected");
                
            }
        });
                
        /* Send button action */
        sendButton.addActionListener(e -> {
            String message = messageInput.getText();
            if (message.length() > 0) {
                messageInput.setText("");
                messageInput.requestFocus();
                messageLog.append("You: " + message + "\n");
            }
        });
        /* Sending messages with enter key */
        messageInput.addActionListener(e -> {
            String message = messageInput.getText();
            if (message.length() > 0) {
                messageLog.append("You: " + message + "\n");
                messageInput.setText("");
            }
        });

        client.registerChatMessageListener(new ChatMessageListener() {

            private String username = duplicateUsername;

            @Override
            public void personListResponseReceived(PersonListResponse personListResponse) {
                for (String s : personListResponse.personList) {
                    try {
                        if (s.equals(username)) {
                            userList.append(" (You) " + s.substring(0, 1).toUpperCase() + s.substring(1) + "\n");
                        } else {
                            userList.append(" " + s.substring(0, 1).toUpperCase() + s.substring(1) + "\n");
                        }
                    } catch (Exception e) {
                        continue;
                    }

                }
            }

            @Override
            public void loginResponseReceived(LoginResponse loginResponse) {
                if (loginResponse.success) {
                    messageLog.append("Login Successful!" + "\n");
                }
            }

            @Override
            public void messageResponseReceived(MessageResponse messageResponse) {
                messageLog.append("[" + messageResponse.time + "] " + messageResponse.sender + " : "
                        + messageResponse.message + "\n");

            }

            @Override
            public void messageHistoryResponseReceived(MessageHistoryResponse messageHistoryResponse) {
                messageLog.append("PREVIOUS MESSAGES" + "\n");

                for (MessageResponse m : messageHistoryResponse.messages) {
                    messageResponseReceived(m);
                }

            }

        });

        client.connect("localhost", 7777);

        LoginRequest loginRequest = new LoginRequest();

        loginRequest.username = this.username;
        loginRequest.password = this.password;

        client.sendMessage(loginRequest);

        LoginResponse loginResponse = new LoginResponse();
        loginResponseReceived(loginResponse);

        PersonListRequest personListRequest = new PersonListRequest();
        client.sendMessage(personListRequest);

        personListResponseReceived(new PersonListResponse());

    }

    @Override
    public void personListResponseReceived(PersonListResponse personListResponse) {

        if (!loginScreen.getLoginStatus()) {
            for (String s : personListResponse.personList) {
                try {
                    if (s.equals(username)) {
                        userList.append(" (You) " + s.substring(0, 1).toUpperCase() + s.substring(1)
                                + "\n");
                    } else {
                        userList.append(" " + s.substring(0, 1).toUpperCase() + s.substring(1) +
                                "\n");
                    }
                } catch (Exception e) {
                    continue;
                }

            }
        }
        /**
         * Just in case something goes wrong with
         * personListResponseReceived
         */
        for (String m : userList.getText().split("\n")) {
            if (m.equals(username)) {

            } else {
                username = loginScreen.getUsername();
                userList.append(" " + username);
                break;
            }

        }

    }

    @Override
    public void loginResponseReceived(LoginResponse loginResponse) {
        if (loginResponse.success && !loginScreen.getLoginStatus()) {
            this.messageLog.append("Login Successful!" + "\n");
        }
    }

    @Override
    public void messageResponseReceived(MessageResponse messageResponse) {
        messageLog.append("[" + messageResponse.time + "] " + messageResponse.sender + " : "
                + messageResponse.message + "\n");

    }

    @Override
    public void messageHistoryResponseReceived(MessageHistoryResponse messageHistoryResponse) {
        messageLog.append("PREVIOUS MESSAGES" + "\n");

        for (MessageResponse m : messageHistoryResponse.messages) {
            messageResponseReceived(m);
        }

    }

}