package org.dasin.servidorchatapp;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.Calendar;

/**
 * Created by David on 27/01/2015.
 */
public class ConexionCliente extends Thread implements Serializable{

    private Socket socket;
    private Servidor servidor;
    private PrintWriter out;
    private BufferedReader in;
    private String nombre;
    private String contrasena;
    private String estado;
    private String tipo;

    public ConexionCliente(Socket socket, Servidor servidor) throws IOException {
        this.socket = socket;
        this.servidor = servidor;
        this.nombre = "";
        this.estado = "";
        this.tipo = "cliente";

        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public PrintWriter getOut(){
        return out;
    }

    @Override
    public void run() {
        System.out.println("Iniciando comunicación con "+ socket.getInetAddress().getHostAddress());
        try {
            String linea;
            while ((linea = in.readLine()) != null) {
                if (linea.startsWith("/escribiendo")){
                    estado = "Escribiendo...";
                }else {
                    estado = "En linea";
                }

                if (linea.startsWith("/conexion")){
                    String[] datos = linea.split(" ");
                    for (ConexionCliente cc: servidor.getListaContactos()){
                        if (cc.getNombre().equalsIgnoreCase(datos[1])){
                            String contasena;
                            if ((contasena = JOptionPane.showInputDialog("Contraseña:")).equalsIgnoreCase(String.valueOf(JOptionPane.CANCEL_OPTION))) {
                                return;
                            }
                            if (!cc.getContrasena().equalsIgnoreCase(contasena)){
                                out.println("/fallo");
                                servidor.eliminarCliente(this);
                                socket.close();
                                JOptionPane.showMessageDialog(null, "Contraseña no valida", "Error", JOptionPane.OK_OPTION);
                                return;
                            }
                        }
                    }
                    for (ConexionCliente cc: servidor.getConexionClientes()){
                        if (cc.getNombre().equalsIgnoreCase(datos[1])){
                            JOptionPane.showMessageDialog(null, "Ese nombre esta siendo utilizado", "Error", JOptionPane.OK_OPTION);
                            out.println("/fallo");
                            servidor.eliminarCliente(this);
                            socket.close();
                            return;
                        }
                    }
                   // servidor.anadirCliente(this);
                    nombre = datos[1];
                    PrintWriter out = new PrintWriter(new FileWriter("conexiones"));
                    out.println("Conexion de "+ getNombre()+" "+ Calendar.getInstance().getTime().toString());

                    out.close();
                }else if (linea.startsWith("/registro")){
                    for (ConexionCliente cc: servidor.getListaContactos()){
                        if (cc.getNombre().equalsIgnoreCase(linea.split(" ")[1])){
                            JOptionPane.showMessageDialog(null, "Ese nombre ya esta registrado", "Error", JOptionPane.OK_OPTION);
                            out.println("/fallo");
                            servidor.eliminarCliente(this);
                            socket.close();
                            return;
                        }
                    }
                    nombre = linea.split(" ")[1];
                    if (linea.split(" ").length > 2){
                        contrasena = linea.split(" ")[2];
                    }else {
                        contrasena = "";
                    }
                    servidor.anadirContacto(this);
                    JOptionPane.showMessageDialog(null, "Registro completado", "Registro", JOptionPane.OK_OPTION);
                }else if (linea.startsWith("/quit")){
                    servidor.eliminarCliente(this);
                    socket.close();
                    break;
                }else if (linea.equalsIgnoreCase("/enviamecontactos")){
                    servidor.enviarContactos(this);
                }else if (linea.startsWith("/privado")){
                    servidor.enviarMensajePrivado(linea);
                }else if(linea.startsWith("/grupo")){
                    try {
                        ConexionCliente cc = new ConexionCliente(socket, servidor);
                        String[] datos = linea.split(" ");
                        cc.setNombre(datos[1]);
                        cc.setEstado("grupo");
                        cc.setTipo("grupo");
                        servidor.anadirCliente(cc);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            servidor.eliminarCliente(this);
        }
        try {
            socket.close();
            servidor.eliminarCliente(this);
            System.out.println("Conexion cerrada con "+socket.getInetAddress().getHostAddress());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }



    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Socket getSocket() {
        return socket;
    }
}
