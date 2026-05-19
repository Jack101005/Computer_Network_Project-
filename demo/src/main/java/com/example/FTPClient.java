package com.example;

import java.io.*;
import java.net.*;
import java.util.*;

public class FTPClient {

    private Socket loginSocket;
    private BufferedReader Network_in;
    private BufferedWriter Network_out;
    private String host;
    private int port;

    public void connect(String host, int port) throws IOException {
        this.host = host;
        this.port = port;
        loginSocket = new Socket(host, port);
        Network_in = new BufferedReader(new InputStreamReader(loginSocket.getInputStream()));
        Network_out = new BufferedWriter(new OutputStreamWriter(loginSocket.getOutputStream()));
        readResponse();
    }

    public void sendCommand(String data) throws IOException {
        Network_out.write(data + "\r\n");
        Network_out.flush();
    }

    public FTPResponse readResponse() throws IOException {
        String signal = Network_in.readLine();
        if (signal == null) {
            return new FTPResponse(-1, "No response at all");
        }
        StringBuilder sb = new StringBuilder(signal);
    if (signal.length() >= 4 && signal.charAt(3) == '-') {
        String code = signal.substring(0, 3);
        while (true) {
            String next = Network_in.readLine();
            if (next == null) break;
            sb.append("\n").append(next);
            if (next.startsWith(code + " ")) break;
        }
    }
    
    int code = Integer.parseInt(signal.substring(0, 3));
    return new FTPResponse(code, sb.toString());
}

    public FTPResponse login(String username, String password) throws IOException {
        sendCommand("USER " + username);
        FTPResponse read = readResponse();
        if (read.code == 331) {
            sendCommand("PASS " + password);
            read = readResponse();
        }
        return read;
    }

    public FTPResponse pwd() throws IOException {
        sendCommand("PWD");
        return readResponse();
    }

    public FTPResponse cwd(String path) throws IOException {
        sendCommand("CWD " + path);
        return readResponse();
    }

    public FTPResponse mkd(String path) throws IOException {
        sendCommand("MKD " + path);
        return readResponse();
    }

    public FTPResponse rmd(String path) throws IOException {
        sendCommand("RMD " + path);
        return readResponse();
    }

    public FTPResponse dele(String path) throws IOException {
        sendCommand("DELE " + path);
        return readResponse();
    }

    public void disconnect() throws IOException {
        sendCommand("QUIT");
        loginSocket.close();
    }

    private Socket openPassive() throws IOException {
        sendCommand("PASV");
        FTPResponse read = readResponse();
        String message = read.message;
        int start = message.indexOf('(');
        int end = message.indexOf(')');
        String[] parts = message.substring(start + 1, end).split(",");

        String ip = parts[0] + "." + parts[1] + "." + parts[2] + "." + parts[3];
        int dataPort = Integer.parseInt(parts[4]) * 256 + Integer.parseInt(parts[5]);

        return new Socket(ip, dataPort);
    }

    public List<String> list() throws IOException {
        Socket dataSocket = openPassive();
        sendCommand("LIST");
        FTPResponse read = readResponse();

        List<String> entries = new ArrayList<>();
        BufferedReader dataReader = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));
        String line;
        while ((line = dataReader.readLine()) != null) {
            entries.add(line);
        }

        dataSocket.close();
        readResponse();
        return entries;
    }

    public void download(String remoteFile, String localPath) throws IOException {
        sendCommand("TYPE I");
        readResponse();

        Socket dataSocket = openPassive();
        sendCommand("RETR " + remoteFile);
        FTPResponse read = readResponse();

        InputStream in = dataSocket.getInputStream();
        FileOutputStream out = new FileOutputStream(localPath);
        byte[] buf = new byte[4096];
        int n;
        while ((n = in.read(buf)) != -1) {
            out.write(buf, 0, n);
        }
        in.close();
        out.close();

        dataSocket.close();
        readResponse();
    }

    public void upload(String localpath, String remoteFile) throws IOException{
    sendCommand("TYPE I");
    readResponse();
    
    Socket dataSocket = openPassive();
    sendCommand("STOR " + remoteFile);
    FTPResponse read = readResponse();


    FileInputStream in = new FileInputStream(localpath);
    OutputStream out = dataSocket.getOutputStream();

    byte[] buf = new byte[4096];
    int n;
    while ((n = in.read(buf)) != -1) {
        out.write(buf, 0, n);
    }
    in.close();
    out.close();

    dataSocket.close();
    readResponse();

}
}