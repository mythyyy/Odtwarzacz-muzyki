package Server.music.Networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class ServerThreadHandler extends Thread {
    private Socket socket;
    private LinkedBlockingQueue<String> chatQueue;
    private ArrayList<Socket> socketList;

    public ServerThreadHandler(Socket socket, LinkedBlockingQueue<String> chatQueue, ArrayList<Socket> socketList) {
        this.socket = socket;
        this.chatQueue = chatQueue;
        this.socketList = socketList;
        if (socket == null || chatQueue == null || socketList == null)
            throw new NullPointerException("Null pointer in class constructor");
    }

    @Override
    public void run() {
        int index;
        System.out.println("STH started: Connected to: " + (socket.getInetAddress()).toString());
        try (
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());) {
            out.writeInt(Protocol.SERVER_WELCOME);
            out.writeInt(Protocol.PROTOCOL_VERSION);
            out.writeUTF(Protocol.MOTD);

            if (in.readInt() == Protocol.CLIENT_WELCOME) {
                socketList.add(socket);
                while (true) {
                    index = in.readInt();
                    switch (index) {
                        case Protocol.CLIENT_DATA_TEXT:
                            System.out.println("Odebral wiadomosc");
                            String message = new String((LocalTime.now()).toString() + " " + in.readUTF());
                            chatQueue.put(message);
                            break;
                        case Protocol.CLIENT_DISCONNECTED:
                            socketList.remove(socket);
                            throw new IOException();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } catch (InterruptedException e) {
            return;
        } finally {
            if (socket.isClosed() == false)
                try {
                    socket.close();
                } catch (IOException e) {
                    return;
                }
        }
    }
}
