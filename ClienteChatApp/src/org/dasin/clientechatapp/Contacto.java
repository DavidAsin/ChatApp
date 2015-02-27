package org.dasin.clientechatapp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by David on 28/01/2015.
 */
public class Contacto {
    private JPanel panel1;
    private JLabel lbContacto;
    private JLabel lbEstadoContacto;
    private JCheckBox cbIgnorar;
    private ChatApp ca;

    public Contacto(String contacto, final ChatApp ca){
        lbContacto.setText(contacto);
        this.ca = ca;
        panel1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ca.getBtEnviar().setEnabled(true);
                ca.getTfMensaje().setEditable(true);
                CardLayout c = (CardLayout) ca.getpChats().getLayout();
                boolean existe = false;
                for (Chat chat: ca.getListaChats()){
                    if (chat.getLbNombreChat().getText().equalsIgnoreCase(lbContacto.getText())){
                        existe = true;
                        c.show(ca.getpChats(), lbContacto.getText());
                        break;
                    }
                }

                if (!existe){
                    Chat chat = new Chat(lbContacto.getText());
                    ca.getListaChats().add(chat);
                    ca.getpChats().add(chat.getPanel1(), lbContacto.getText());
                    c.show(ca.getpChats(), lbContacto.getText());
                }
                ca.setConversacion(lbContacto.getText());
                ca.getpChats().updateUI();
            }
        });
        cbIgnorar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (cbIgnorar.isSelected()){
                    ca.getListaIgnorados().add(lbContacto.getText());
                }else{
                    ca.getListaIgnorados().remove(lbContacto.getText());
                }
            }
        });
    }

    public JPanel getPanel1() {
        return panel1;
    }

    public JLabel getLbEstadoContacto() {
        return lbEstadoContacto;
    }

    public JCheckBox getCbIgnorar() {
        return cbIgnorar;
    }
}
