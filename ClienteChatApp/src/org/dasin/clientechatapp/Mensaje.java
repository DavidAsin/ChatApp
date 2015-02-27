package org.dasin.clientechatapp;

import javax.swing.*;

/**
 * Created by David on 28/01/2015.
 */
public class Mensaje {
    private JLabel lbMensaje;
    private JPanel panel1;

    public Mensaje(String mensaje){
        lbMensaje.setText(mensaje);
    }

    public JLabel getLbMensaje() {
        return lbMensaje;
    }

    public JPanel getPanel1() {
        return panel1;
    }
}
