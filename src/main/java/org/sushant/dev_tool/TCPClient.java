package org.sushant.dev_tool;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

@Slf4j
public class TCPClient {
    public static void main(String[] args) {
        try (
                Socket socket = new Socket("localhost", 6349);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                Scanner scanner = new Scanner(System.in)
        ) {
            log.info("Connected to KV store. Type commands (SET key value / GET key):");

            while (true) {
                String userInput = scanner.nextLine();
                out.println(userInput);
                String response = in.readLine();
                System.out.println("-> " + response);
            }
        } catch (Exception e) {
            log.error("unable to create TCP client: {}", e.getMessage());
        }
    }
}
