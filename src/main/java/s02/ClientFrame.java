package s02;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientFrame extends Frame {
    TextField field = new TextField();
    TextArea area = new TextArea();
    Client client = null;
    public ClientFrame(){
        this.setSize(400,600);
        this.setLocation(20,20);
        this.add(field,BorderLayout.SOUTH);
        this.add(area,BorderLayout.NORTH);
        field.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                client.send(field.getText());
                area.setText(area.getText()+field.getText());
                field.setText("");
            }
        });
        this.setVisible(true);
        connectToServer();
    }
    public static void main(String[] args) {
        ClientFrame frame = new ClientFrame();
    }

    public void connectToServer(){
        client = new Client();
        client.connect();
    }
}
