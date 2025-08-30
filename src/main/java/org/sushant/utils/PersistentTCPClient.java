package org.sushant.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class PersistentTCPClient {
    private final String host;
    private final int port;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    private final ReentrantLock lock = new ReentrantLock();

    public PersistentTCPClient(String host, int port) throws Exception {
        this.host = host;
        this.port = port;
        connect();
    }

    private void connect() throws IOException {
        socket = new Socket(host, port);
        socket.setKeepAlive(true);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    private void close() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException ignored) {}
    }

    private void reconnect() throws IOException {
        close();
        connect();
    }

    public String sendCommand(String command) throws IOException {
        synchronized (lock) {
            try {
                if (socket.isClosed() || !socket.isConnected()) {
                    reconnect();
                }
                out.println(command);
                return in.readLine();
            } catch (SocketException se) {
                log.warn("SocketException, trying to reconnect: {}", se.getMessage());
                reconnect();
                out.println(command);
                return in.readLine();
            }
        }
    }

    public void set(String key, String value) throws IOException {
        sendCommand("SET " + key + " " + value);
    }

    public String get(String key) throws IOException {
        return sendCommand("GET " + key);
    }
}
