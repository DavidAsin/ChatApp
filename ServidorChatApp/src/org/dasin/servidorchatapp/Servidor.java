package org.dasin.servidorchatapp;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by David on 27/01/2015.
 */
public class Servidor {

    private int puerto;
    private ServerSocket socket;
    private ArrayList<ConexionCliente> conexionClientes;
    private ArrayList<ConexionCliente> listaContactos;
    private ObjectInputStream in;

    public Servidor(int puerto) {
        this.puerto = puerto;
        conexionClientes = new ArrayList<ConexionCliente>();
        listaContactos = new ArrayList<ConexionCliente>();
        /*try {
            File f = new File("registros");
            if (f.exists()){
                in = new ObjectInputStream(new FileInputStream("registros"));
                ConexionCliente cc = null;
                try {
                    while (true){
                        cc = (ConexionCliente) in.readObject();
                        listaContactos.add(cc);
                    }


                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            for (ConexionCliente c: listaContactos){
                System.out.println(c.getNombre());
            }

        } catch (EOFException e){
            try {
                in.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
*/

    }

    public void anadirCliente(ConexionCliente conexionCliente) {
        conexionClientes.add(conexionCliente);
    }

    public void anadirContacto(ConexionCliente conexionCliente) {
        listaContactos.add(conexionCliente);
    }

    public void eliminarCliente(ConexionCliente conexionCliente) {
        conexionClientes.remove(conexionCliente);
    }

    public void enviarContactos(ConexionCliente c) {
            c.getOut().println(obtenerContactos());
    }

    public String obtenerContactos() {

        String nicks = "/nicks,";
        for (ConexionCliente conexionCliente : conexionClientes) {
            nicks += conexionCliente.getNombre() + ":" + conexionCliente.getEstado()+ ",";
        }
        return nicks;
    }

    public boolean estaConectado() {
        return !socket.isClosed();
    }

    public void conectar() throws IOException {
        socket = new ServerSocket(puerto);
    }

    public void desconectar() throws IOException {
        socket.close();
    }

    public Socket accept() throws IOException {
        return socket.accept();
    }

    public ArrayList<ConexionCliente> getConexionClientes() {
        return conexionClientes;
    }

    public void setConexionClientes(ArrayList<ConexionCliente> conexionClientes) {
        this.conexionClientes = conexionClientes;
    }

    public ArrayList<ConexionCliente> getListaContactos() {
        return listaContactos;
    }

    public void enviarMensajePrivado(String mensaje){
        String[] mensajes = mensaje.split(" ");
        if (mensajes[2].equalsIgnoreCase("general")){
            String linea = "/mensajegrupo ";
            for (int i = 1; i < mensajes.length; i++) {
                linea += mensajes[i] +" ";
            }
            System.out.println(linea);
            for (ConexionCliente cc: conexionClientes) {
                if (!linea.split(" ")[1].equalsIgnoreCase(cc.getNombre()) && !cc.getNombre().equalsIgnoreCase("general")){
                    cc.getOut().println(linea);
                }
            }
        }else{
            for (ConexionCliente cc: conexionClientes){
                if (mensajes[2].equalsIgnoreCase(cc.getNombre())){
                    cc.getOut().println(mensaje);
                    break;
                }
            }
        }

    }

}
