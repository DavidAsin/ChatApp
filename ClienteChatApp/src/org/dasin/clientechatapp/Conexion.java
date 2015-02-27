package org.dasin.clientechatapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by David on 27/01/2015.
 */
public class Conexion {
    private String hostname;
    private int puerto;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private BufferedReader teclado;


    public Conexion() {

        hostname = "localhost";
        puerto = 4444;

        try {
            socket = new Socket(hostname, puerto);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        } catch (IOException e) {
            e.printStackTrace();
        }

        teclado = new BufferedReader(new InputStreamReader(System.in));

    }

    public PrintWriter getOut() {
        return out;
    }

    public BufferedReader getIn() {
        return in;
    }

    public Socket getSocket() {
        return socket;
    }
}
