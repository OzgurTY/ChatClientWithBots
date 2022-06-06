package tr.edu.ozyegin.chat.client;

import java.awt.event.*;
import java.awt.*;
import tr.edu.ozyegin.chat.messages.*;

public class LoginScreen extends Frame implements ActionListener {

    private String username, password;

    private int countDoClick = 0;

    private Frame frame;
    private boolean loginStatus;

    TextField usernameText, passwordText;
    Button loginButton, cancelButton;

    public int count = 0;

    LoginScreen() {

        this.loginStatus = false;

        this.username = "";
        this.password = "";

        this.frame = new Frame("Connect");

        loginButton = new Button("Connect");
        cancelButton = new Button("Cancel");

        usernameText = new TextField();
        passwordText = new TextField();

        Label usernameLabel = new Label("   Username:");
        Label passwordLabel = new Label("   Password:");

        Color c = new Color(96, 164, 236);

        /* Material sizing and positioning */
        frame.setLayout(new BorderLayout());
        usernameLabel.setBounds(10, 80, 100, 30);
        usernameText.setBounds(120, 80, 240, 30);
        passwordLabel.setBounds(10, 130, 100, 30);
        passwordText.setBounds(120, 130, 240, 30);
        loginButton.setBounds(70, 180, 100, 30);
        cancelButton.setBounds(220, 180, 100, 30);

        /* Password Field settings */
        passwordText.setEchoChar('*');

        /* Label settings */
        usernameLabel.setAlignment(Label.LEFT);
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 12));
        passwordLabel.setAlignment(Label.LEFT);
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 12));
        usernameLabel.setBackground(c);
        passwordLabel.setBackground(c);

        /* Frame settings */
        frame.add(usernameLabel);
        frame.add(usernameText);
        frame.add(passwordLabel);
        frame.add(passwordText);
        frame.add(loginButton);
        frame.add(cancelButton);

        frame.setSize(400, 250);
        frame.setResizable(false);

        frame.setLayout(null);

        frame.setVisible(true);

        frame.setBackground(c);

        loginButton.addActionListener(this);
        cancelButton.addActionListener(this);

        loginButton.addActionListener(e -> {

            setUsername(usernameText.getText());
            setPassword(passwordText.getText());

            MockChatClient client = new MockChatClient();

            setLoginStatus(true);

            // this.count++;

            frame.setVisible(false);

        });

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                System.exit(0);
            }
        });

        cancelButton.addActionListener(arg0 -> {
            System.exit(0);
        });

        loginButton.addActionListener(e -> {
            this.username = usernameText.getText();
            this.password = passwordText.getText();

            setLoginStatus(true);

            frame.setVisible(false);

            if (this.countDoClick == 0) {
                ChatClientGUI.connectButton.doClick();
                ChatClientGUI.isConnectedLabel.setText("  \u2713 Connected");
                countDoClick++;
            }

        });

    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            this.username = usernameText.getText();
            this.password = passwordText.getText();

            LoginRequest loginRequest = new LoginRequest();
            loginRequest.username = this.username;
            loginRequest.password = this.password;
            MockChatClient client = new MockChatClient();

            setLoginStatus(true);
            frame.setVisible(false);
        } else if (e.getSource() == cancelButton) {
            System.exit(0);
        }

    }

    public void setLoginStatus(boolean isLoginSuccessful) {
        this.loginStatus = isLoginSuccessful;
    }

    public boolean getLoginStatus() {
        return this.loginStatus;
    }

    public String getPassword() {
        return this.password;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String usernamee) {
        this.username = usernamee;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static void main(String[] args) {
        new LoginScreen();
    }
}