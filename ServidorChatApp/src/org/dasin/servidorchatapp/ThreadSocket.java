package org.dasin.servidorchatapp;

import java.io.*;
import java.util.Calendar;

/**
 * Created by David on 27/01/2015.
 */
public class ThreadSocket {


    public static void main(String args[]) {

        Servidor servidor = new Servidor(4444);
        ConexionCliente conexionCliente = null;

        try {
            servidor.conectar();

            while (servidor.estaConectado()) {
                conexionCliente = new ConexionCliente(servidor.accept(), servidor);
                System.out.println("Nuevo cliente conectado");

                //ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("registros"));
                //out.writeObject(conexionCliente);
                boolean encontrado = false;
                for (ConexionCliente cc : servidor.getConexionClientes()){
                    System.out.println(cc.getSocket().getInetAddress().getHostAddress());

                    if (conexionCliente.getSocket().getInetAddress().getHostAddress().equalsIgnoreCase(cc.getSocket().getInetAddress().getHostAddress())){
                        encontrado = true;
                    }
                }
                if (!encontrado){
                    servidor.anadirCliente(conexionCliente);
                    conexionCliente.start();
                }

            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
