package org.dasin.clientechatapp;

import javax.swing.*;

/**
 * Created by David on 28/01/2015.
 */
public class Chat {
    private JLabel lbNombreChat;
    private JPanel pConversacion;
    private JPanel panel1;

    public Chat(String nombre) {
        this.lbNombreChat.setText(nombre);
        pConversacion.setLayout(new BoxLayout(pConversacion, BoxLayout.Y_AXIS));
    }

    public JLabel getLbNombreChat() {
        return lbNombreChat;
    }

    public JPanel getpConversacion() {
        return pConversacion;
    }

    public JPanel getPanel1() {
        return panel1;
    }
}
