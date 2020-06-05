package Server.music.Networking;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

public class TextWriter extends Thread {

    private LinkedBlockingQueue<String> chatQueue;
    private ArrayList<Socket> socketList;
    private Semaphore binarySem;

    public TextWriter(LinkedBlockingQueue<String> chatQueue, ArrayList<Socket> socketList, Semaphore binarySem) {
        // TODO Auto-generated constructor stub
        this.chatQueue = chatQueue;
        this.socketList = socketList;
        this.binarySem = binarySem;

        if (chatQueue == null || socketList == null || binarySem == null)
            throw new NullPointerException("Null pointer in class constructor");
    }

    @Override
    public void run() {
        System.out.println("tw started");
        String message;

        while (socketList.isEmpty()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                return;
            }
        }
        try {
            DataOutputStream out;
            while (true) {
                message = chatQueue.take();
                try {
                    binarySem.acquire();
                    Iterator<Socket> it = socketList.iterator();
                    while (it.hasNext()) {
                        Socket socket = it.next();
                        try {
                            out = new DataOutputStream(socket.getOutputStream());
                            out.writeInt(Protocol.SERVER_DATA_TEXT);
                            out.writeUTF(message);
                        } catch (SocketException e) {
                            it.remove();
                        }
                    }
                } catch (InterruptedException e) {
                    return;
                } finally {
                    binarySem.release();
                }
            }
        } catch (InterruptedException e) {
            return;
        } catch (IOException e) {
        }
    }
}
