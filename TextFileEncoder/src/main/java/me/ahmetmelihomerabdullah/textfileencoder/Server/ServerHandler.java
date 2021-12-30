package me.ahmetmelihomerabdullah.textfileencoder.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerHandler extends Thread{
    Socket socket;
    DataInputStream din;
    DataOutputStream dout;
    boolean serverRun = true;

    public ServerHandler(Socket socket){
        super("Server ConnectionThread");
        this.socket = socket;
    }

    public void sendToClient(String text){
        try{
            dout.writeUTF(text);
            dout.flush();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void sendToAllClient(String text){
        for(int i = 0; i< Server.connections.size(); i++){
            ServerHandler sc = Server.connections.get(i);
            sc.sendToClient(text);
        }
    }

    public void run(){
        try {
            din = new DataInputStream(socket.getInputStream());
            dout = new DataOutputStream(socket.getOutputStream());
            while(serverRun){
                while(din.available() == 0){
                    try{
                        Thread.sleep(1);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    String text = din.readUTF();
                    System.out.println(text);
                    sendToAllClient(text);
                }
            }
            din.close();
            dout.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
