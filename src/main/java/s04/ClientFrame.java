package s04;

import java.awt.*;
import java.awt.event.*;

public class ClientFrame extends Frame {

    public static volatile ClientFrame instance = null;
    TextField field = new TextField();
    private static TextArea area = new TextArea();
    Client client = null;

    public static ClientFrame getInstance(){
        if(instance == null){
            synchronized (ClientFrame.class){
                if(instance == null){
                    instance = new ClientFrame();
                }
            }
        }
        return instance;
    }

    private ClientFrame(){
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
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        this.setVisible(true);
        connectToServer();
    }

    public void updateArea(String msg){
        System.out.println( "show in textarea: "+msg);
        area.setText(area.getText()+System.getProperty("line.seperator")+msg);
    }


    public static void main(String[] args) {
        ClientFrame.getInstance();
    }

    public void connectToServer(){
        client = new Client(this);
        client.connect();
    }
}
