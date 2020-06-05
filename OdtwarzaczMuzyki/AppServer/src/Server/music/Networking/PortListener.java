package Server.music.Networking;

import Server.music.Song;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

public class PortListener extends Thread {
    private ObservableList<Song> playlist;
    private LinkedBlockingQueue<String> chatQueue;
    private ArrayList<Socket> socketList;
    private ArrayList<ServerThreadHandler> handlerList;
    private Semaphore binarySem;
    private ServerSocket serverSocket;
    private AudioSender audioSender;

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public PortListener(ObservableList<Song> playlist) {
        handlerList = new ArrayList<ServerThreadHandler>();
        this.playlist = playlist;
        chatQueue = new LinkedBlockingQueue<String>();
        socketList = new ArrayList<Socket>();
        if (playlist == null)
            throw new NullPointerException("Null pointer in class constructor");
    }

    public void changeMusic(int state){
        System.out.println("2");
        if (state == 0){
            audioSender.changePause();
        }
    }

    @Override
    public void run() {
        System.out.println("PL started");
        binarySem = new Semaphore(1);
        try {
            TextWriter textWriter = new TextWriter(chatQueue, socketList, binarySem);
            audioSender = new AudioSender(playlist, socketList, binarySem);
            textWriter.start();
            audioSender.start();
            try {
                serverSocket = new ServerSocket(Protocol.PORT);
                ServerThreadHandler handler;
                while (true) {
                    if (this.isInterrupted()) {
                        textWriter.interrupt();
                        audioSender.interrupt();
                        break;
                    }
                    handler = new ServerThreadHandler(serverSocket.accept(), chatQueue, socketList);
                    handlerList.add(handler);
                    handler.start();
                }
            } catch (IOException e) {
                textWriter.interrupt();
                audioSender.interrupt();
                Iterator<ServerThreadHandler> it = handlerList.iterator();
                while (it.hasNext()) {
                    (it.next()).interrupt();
                }
                return;
            } finally {
                try {
                    if (serverSocket != null)
                        if (!serverSocket.isClosed())
                            serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
