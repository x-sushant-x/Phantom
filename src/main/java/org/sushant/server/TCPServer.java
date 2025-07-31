package org.sushant.server;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sushant.store.KVStore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
@AllArgsConstructor
public class TCPServer {
    private int PORT;
    private KVStore store;


    @SuppressWarnings("InfiniteLoopStatements")
    public void startServer() throws IOException {
        try(ServerSocket socket = new ServerSocket(PORT)) {
            log.info("TCP Server Started on Port: {}", PORT);

            while (true) {
                Socket conn = socket.accept();
                new Thread(() -> handleConnection(conn)).start();
            }
        }
    }

    private void handleConnection(Socket conn) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
             PrintWriter out = new PrintWriter(conn.getOutputStream(), true)) {

            String input;

            while ((input = in.readLine()) != null) {
                String[] tokens = input.split(" ", 3);
                if(tokens.length == 0) return;

                switch (tokens[0].toUpperCase()) {
                    case "SET":
                        if (tokens.length == 3) {
                            store.set(tokens[1], tokens[2]);
                            out.println("OK");
                        } else {
                            out.println("ERROR: Usage SET key value");
                        }
                        break;

                    case "GET":
                        if (tokens.length == 2) {
                            String value = store.get(tokens[1]);
                            out.println(value != null ? value : "NULL");
                        } else {
                            out.println("ERROR: Usage GET key");
                        }
                        break;

                    case "DEL":
                        if (tokens.length == 2) {
                            store.delete(tokens[1]);
                        } else {
                            out.println("ERROR: Usage DEL key");
                        }
                        break;

                    default:
                        out.println("ERROR: Unknown command");
                }
            }

        } catch (IOException e) {
            log.error("Connection error: {}", e.getMessage());
        }
    }
}
