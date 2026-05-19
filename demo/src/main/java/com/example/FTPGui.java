package com.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class FTPGui extends JFrame {

    private FTPClient client = new FTPClient();

    private JTextField hostField = new JTextField("test.rebex.net", 15);
    private JTextField portField = new JTextField("21", 5);
    private JTextField userField = new JTextField("demo", 10);
    private JPasswordField passField = new JPasswordField("password", 10);
    private JTextArea outputArea = new JTextArea();
    private JTextField inputField = new JTextField(20);
    private JButton connectBtn = new JButton("Connect");

    public FTPGui() {
        setTitle("FTP Client - VGU");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(new JLabel("Host:"));
        topPanel.add(hostField);
        topPanel.add(new JLabel("Port:"));
        topPanel.add(portField);
        topPanel.add(new JLabel("User:"));
        topPanel.add(userField);
        topPanel.add(new JLabel("Pass:"));
        topPanel.add(passField);
        topPanel.add(connectBtn);
        add(topPanel, BorderLayout.NORTH);

        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        outputArea.setBackground(Color.BLACK);
        outputArea.setForeground(Color.GREEN);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel btnPanel = new JPanel(new FlowLayout());

        String[] commands = {"ls", "pwd", "cd", "get", "put", "delete", "mkdir", "rmdir", "quit"};
        for (String cmd : commands) {
            JButton btn = new JButton(cmd);
            btn.addActionListener(e -> handleCommand(cmd));
            btnPanel.add(btn);
        }

        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(new JLabel("Argument:"));
        inputPanel.add(inputField);

        bottomPanel.add(btnPanel, BorderLayout.CENTER);
        bottomPanel.add(inputPanel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);

        connectBtn.addActionListener(e -> {
            try {
                String host = hostField.getText().trim();
                int port = Integer.parseInt(portField.getText().trim());
                String user = userField.getText().trim();
                String pass = new String(passField.getPassword());
                client.connect(host, port);
                log("Connected to " + host);
                FTPResponse r = client.login(user, pass);
                log("Login: " + r);
            } catch (Exception ex) {
                log("Error: " + ex.getMessage());
            }
        });

        setVisible(true);
    }

    private void handleCommand(String cmd) {
        String arg = inputField.getText().trim();
        try {
            switch (cmd) {
                case "ls" -> {
                    List<String> files = client.list();
                    files.forEach(this::log);
                }
                case "pwd" -> log(client.pwd().toString());
                case "cd" -> log(client.cwd(arg).toString());
                case "get" -> {
                    client.download(arg, System.getProperty("user.home") + "/Downloads/" + arg);
                    log("Downloaded: " + arg);
                }
                case "put" -> {
                    client.upload(System.getProperty("user.home") + "/Downloads/" + arg, arg);
                    log("Uploaded: " + arg);
                }
                case "delete" -> log(client.dele(arg).toString());
                case "mkdir" -> log(client.mkd(arg).toString());
                case "rmdir" -> log(client.rmd(arg).toString());
                case "quit" -> {
                    client.disconnect();
                    log("Disconnected.");
                }
            }
        } catch (Exception ex) {
            log("Error: " + ex.getMessage());
        }
    }

    private void log(String msg) {
        outputArea.append(msg + "\n");
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FTPGui::new);
    }
}