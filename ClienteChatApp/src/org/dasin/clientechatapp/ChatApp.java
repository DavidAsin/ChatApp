package org.dasin.clientechatapp;

import javax.swing.*;
import javax.swing.plaf.BorderUIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.util.ArrayList;

/**
 * Created by David on 27/01/2015.
 */
public class ChatApp {
    private static JFrame frame;
    private JPanel panel1;
    private JTextField tfMensaje;
    private JPanel pChats;
    private JButton btEnviar;
    private JLabel lEstado;
    private JPanel pListaContactos;

    private Conexion conexion;
    private Timer timer;
    private boolean conectado;

    private String usuario;
    private String contrasena;

    private String conversacion;

    private ArrayList<Chat> listaChats;
    private ArrayList<String> listaIgnorados;

    private ChatApp esta;

    private JMenuItem conectar;
    private JMenuItem desconectar;
    private JMenuItem grupo;


    public ChatApp(){
        listaChats = new ArrayList<Chat>();
        listaIgnorados = new ArrayList<String>();
        esta = this;
        pListaContactos.setLayout(new BoxLayout(pListaContactos, BoxLayout.Y_AXIS));
        pChats.setLayout(new CardLayout());



        timer = new Timer(700, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refrescarListaContactos();
                if (!conexion.getSocket().isClosed()){
                    lEstado.setText("Conectado");
                }else{
                    lEstado.setText("Desconectado");
                }
            }
        });

        tfMensaje.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    enviarMensaje();
            }
        });


        tfMensaje.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                conexion.getOut().println("/escribiendo");
            }
        });


    }

    public void escucharMensajes() {

            Thread hiloRecibir = new Thread(new Runnable() {
                public void run() {
                    while (conectado) {
                        try {
                            if (conexion.getSocket().isClosed()) {
                                conectado = false;
                                break;
                            }
                            String mensaje = conexion.getIn().readLine();
                            if (mensaje == null)
                                continue;

                            String[] aux = null;
                            if (mensaje.equalsIgnoreCase("/fallo")){
                                lEstado.setText("Desconectado");
                                conexion.getSocket().close();
                                conectar.setEnabled(true);
                                desconectar.setEnabled(false);
                            }else if (mensaje.startsWith("/nicks")){
                                pChats.setVisible(true);
                                pListaContactos.removeAll();
                                aux = mensaje.split(",");
                                if (aux.length > 2){
                                    for (int i = 1; i < aux.length; i++){
                                        if (!aux[i].split(":")[0].equalsIgnoreCase(usuario)){
                                            String[] datos = aux[i].split(":");
                                            Contacto c = new Contacto(datos[0], getEsta());

                                            c.getLbEstadoContacto().setText(datos[1]);
                                            for (String s: listaIgnorados){
                                                if (s.equalsIgnoreCase(datos[0])){
                                                    c.getCbIgnorar().setSelected(true);
                                                    break;
                                                }
                                            }
                                            pListaContactos.add(c.getPanel1());
                                        }
                                    }
                                }else{
                                    pChats.setVisible(false);
                                    getBtEnviar().setEnabled(false);
                                    getTfMensaje().setEditable(false);
                                    Contacto c = new Contacto("No hay contactos", getEsta());
                                    c.getLbEstadoContacto().setText("");
                                    c.getCbIgnorar().setVisible(false);
                                    pListaContactos.add(c.getPanel1());
                                }
                                pListaContactos.updateUI();
                            }else if (mensaje.startsWith("/privado")) {
                                boolean ignorado = false;
                                for (String s: listaIgnorados){
                                    if (s.equalsIgnoreCase(mensaje.split(" ")[1])){
                                        ignorado = true;
                                        break;
                                    }
                                }

                                if (!ignorado){
                                    boolean existe = false;
                                    for (Chat chat: listaChats){
                                        if (chat.getLbNombreChat().getText().equalsIgnoreCase(mensaje.split(" ")[1])){
                                            aux = mensaje.split(" ");
                                            String m = "";
                                            for (int i = 3; i < aux.length; i++) {
                                                m += aux[i] + " ";
                                            }
                                            Mensaje men = new Mensaje(m);
                                            men.getLbMensaje().setBorder(BorderFactory.createTitledBorder(men.getPanel1().getBorder(), aux[1]));
                                            chat.getpConversacion().add(men.getPanel1());
                                            chat.getpConversacion().updateUI();
                                            existe = true;
                                            break;
                                        }
                                    }

                                    if (!existe){
                                        Chat chat = new Chat(mensaje.split(" ")[1]);
                                        listaChats.add(chat);
                                        pChats.add(chat.getPanel1(), mensaje.split(" ")[1]);
                                        aux = mensaje.split(" ");
                                        String m = "";
                                        for (int i = 3; i < aux.length; i++) {
                                            m += aux[i] + " ";
                                        }
                                        chat.getpConversacion().add(new Mensaje(m).getPanel1());
                                        chat.getpConversacion().updateUI();
                                    }
                                }
                            }else if (mensaje.startsWith("/mensajegrupo")){
                                for (Chat chat: listaChats){
                                    if (chat.getLbNombreChat().getText().equalsIgnoreCase(mensaje.split(" ")[2])){
                                        aux = mensaje.split(" ");
                                        String m = "";
                                        for (int i = 3; i < aux.length; i++) {
                                            m += aux[i] + " ";
                                        }
                                        Mensaje men = new Mensaje(m);
                                        men.getLbMensaje().setBorder(BorderFactory.createTitledBorder(men.getPanel1().getBorder(), aux[1]));
                                        chat.getpConversacion().add(men.getPanel1());
                                        chat.getpConversacion().updateUI();
                                        //existe = true;
                                        break;
                                    }
                                }
                            }
                        } catch (SocketException se) {
                            desconectar();
                        } catch (IOException ioe) {
                            ioe.printStackTrace();
                        }
                    }
                }
            });
            hiloRecibir.start();
    }

    private void enviarMensaje() {
        String mensaje = "/privado "+usuario+" "+conversacion+" "+tfMensaje.getText();
        conexion.getOut().println(mensaje);

        for (Chat chat: listaChats){
            if (chat.getLbNombreChat().getText().equalsIgnoreCase(mensaje.split(" ")[2])){
                Mensaje m = new Mensaje(tfMensaje.getText());
                m.getLbMensaje().setHorizontalAlignment(SwingConstants.RIGHT);
                m.getPanel1().setBackground(new Color(220, 248, 198));
                chat.getpConversacion().add(m.getPanel1());
                chat.getpConversacion().updateUI();
                break;
            }
        }
        tfMensaje.setText("");
    }

    public JMenuBar getMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        menuBar.add(menu);
        conectar = new JMenuItem("Conectar");
        desconectar = new JMenuItem("Desconectar");
        final JMenuItem registrarse = new JMenuItem("Registrarse");
        grupo = new JMenuItem("Crear grupo");
        desconectar.setEnabled(false);
        conectar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                conectar();
                conectar.setEnabled(false);
                desconectar.setEnabled(true);
                registrarse.setEnabled(false);
            }
        });
        menu.add(conectar);
        desconectar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                desconectar();
                conectar.setEnabled(true);
                desconectar.setEnabled(false);
                registrarse.setEnabled(true);
            }
        });
        menu.add(desconectar);
        registrarse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registro();
                conectar.setEnabled(false);
                desconectar.setEnabled(true);
                registrarse.setEnabled(false);
            }
        });
        menu.add(registrarse);
        JMenuItem salir = new JMenuItem("Salir");
        salir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                salir();
            }
        });
        menu.add(salir);
        grupo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                crearGrupo();
            }
        });
        menu.add(grupo);

        return menuBar;

    }

    private void crearGrupo() {
        conexion.getOut().println("/grupo " + "General");

    }

    private void registro() {
        Dialogo dialogo = new Dialogo(getEsta());
        dialogo.setVisible(true);
    }


    private void conectar() {
        String nombre;
        if ((nombre = JOptionPane.showInputDialog("Nombre:")).equalsIgnoreCase(String.valueOf(JOptionPane.CANCEL_OPTION))){
            return;
        }
        conexion = new Conexion();
        getlEstado().setText("Conectado");
        setUsuario(nombre);
        conexion.getOut().println("/conexion " + nombre);
        conectado = true;
        refrescarListaContactos();
        escucharMensajes();
        getTimer().start();
        /*
        Dialogo dialogo = new Dialogo(this);
        dialogo.setVisible(true);
        conectado = true;
        refrescarListaContactos();
        escucharMensajes();*/
    }

    public synchronized void refrescarListaContactos() {
        conexion.getOut().println("/enviamecontactos");

    }

    private void desconectar() {
        conexion.getOut().println("/quit " + usuario);
        lEstado.setText("Desconectado");
        timer.stop();

        pChats.removeAll();
        conectado = false;
        try {
            conexion.getSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        pListaContactos.removeAll();
        pChats.removeAll();
    }

    private void salir() {
        System.exit(0);
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public Conexion getConexion() {
        return conexion;
    }

    public void setConexion(Conexion conexion) {
        this.conexion = conexion;
    }

    public JLabel getlEstado() {
        return lEstado;
    }

    public void setlEstado(JLabel lEstado) {
        this.lEstado = lEstado;
    }

    public Timer getTimer() {
        return timer;
    }

    public String getConversacion() {
        return conversacion;
    }

    public void setConversacion(String conversacion) {
        this.conversacion = conversacion;
    }

    public JPanel getpChats() {
        return pChats;
    }

    public void setpChats(JPanel pChats) {
        this.pChats = pChats;
    }

    public ArrayList<Chat> getListaChats() {
        return listaChats;
    }

    public void setListaChats(ArrayList<Chat> listaChats) {
        this.listaChats = listaChats;
    }

    public ChatApp getEsta() {
        return esta;
    }

    public boolean isConectado() {
        return conectado;
    }

    public void setConectado(boolean conectado) {
        this.conectado = conectado;
    }

    public ArrayList<String> getListaIgnorados() {
        return listaIgnorados;
    }

    public JTextField getTfMensaje() {
        return tfMensaje;
    }

    public JButton getBtEnviar() {
        return btEnviar;
    }

    public static void main(String[] args) {
        frame = new JFrame("ChatApp");
        ChatApp chatApp = new ChatApp();
        frame.setJMenuBar(chatApp.getMenuBar());
        frame.setContentPane(chatApp.panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
