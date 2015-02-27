package org.dasin.clientechatapp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Dialogo extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField tfNombre;
    private JTextField tfContrasena;

    private ChatApp ca;


    public Dialogo(ChatApp ca) {
        this.ca = ca;
        setContentPane(contentPane);
        setModal(true);
        setSize(new Dimension(200, 150));
        setLocationRelativeTo(null);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        ca.setConexion(new Conexion());
        ca.getlEstado().setText("Conectado");
        ca.setUsuario(tfNombre.getText());
        ca.setConectado(true);
        ca.escucharMensajes();
        ca.getConexion().getOut().println("/registro "+tfNombre.getText()+" "+tfContrasena.getText());
        ca.refrescarListaContactos();
        ca.getTimer().start();
        dispose();
    }

    private void onCancel() {
        dispose();
    }

}
