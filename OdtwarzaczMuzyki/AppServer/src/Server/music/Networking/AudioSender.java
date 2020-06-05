package Server.music.Networking;

import Server.music.Song;
import Server.view.ServerViewController;
import javafx.collections.ObservableList;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Semaphore;

public class AudioSender extends Thread {

    private ObservableList<Song> playlist;
    private ArrayList<Socket> socketList;
    private Semaphore binarySem;
    public boolean next = false;
    boolean pause = true;

    public AudioSender(ObservableList<Song> playlist, ArrayList<Socket> socketList, Semaphore binarySem) {
        this.socketList = socketList;
        this.playlist = playlist;
        this.binarySem = binarySem;
        if (playlist == null || socketList == null || binarySem == null)
            throw new NullPointerException("Null pointer in class constructor");
    }

    public void changePause() {
        System.out.println("3");
        if (pause == false)
            pause = true;
        if (pause == true)
            pause = false;
        System.out.println(pause);
    }

    @Override
    public void run() {
        Song currentSong;
        int songIndex = -1;
        int currByteRate;
        if (playlist.isEmpty()) {
            return;
        }
        while (socketList.isEmpty()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                return;
            }
        }
        while (true) {
            if (this.isInterrupted())
                return;
            songIndex += 1;
            if (playlist.size() - 1 < songIndex)
                songIndex = 0;
            currentSong = playlist.get(songIndex);
            if (currentSong == null)
                break;
            currentSong.setIsPlaying("sending");
            currByteRate = (currentSong.getBitrate() / 8);
            long timeStart = System.currentTimeMillis();
            long timeSent = 0;
            try (FileInputStream in = new FileInputStream(currentSong.getPath());) {
                byte[] data = new byte[4096];
                int len;
                DataOutputStream out;
                Iterator<Socket> it = socketList.iterator();
                if ((len = in.read(data)) != -1) {
                    try {
                        binarySem.acquire();
                        while (it.hasNext()) {
                            Socket socket = it.next();
                            try {
                                out = new DataOutputStream(socket.getOutputStream());
                                out.writeInt(Protocol.SERVER_DATA_AUDIO_NEW_FORMAT);
                                out.writeUTF(currentSong.getDisplayName());
                                out.writeInt(len);
                                if (len < 4096) {
                                    for (int i = 0; i < len; i++) {
                                        out.writeByte(data[i]);
                                    }
                                } else {
                                    out.write(data);
                                }
                            } catch (SocketException e) {
                                it.remove();
                            }
                        }
                        timeSent += (len * 1000) / currByteRate;
                    } catch (InterruptedException e) {
                        return;
                    } finally {
                        binarySem.release();
                    }
                } else {
                    in.close();
                    currentSong.setIsPlaying("");
                    break;
                }

                while (true) {
                    if ((timeSent - (System.currentTimeMillis() - timeStart)) > Protocol.DELAY) {
                        try {
                            Thread.sleep(5);
                        } catch (InterruptedException e) {
                            return;
                        }
                        continue;
                    }
                    if ((len = in.read(data)) != -1) {
                        try {
                            binarySem.acquire();
                            it = socketList.iterator();
                            while (it.hasNext()) {
                                Socket socket = it.next();
                                try {
                                    out = new DataOutputStream(socket.getOutputStream());
                                    out.writeInt(Protocol.SERVER_DATA_AUDIO_CONTINUE);
                                    out.writeInt(len);
                                    if (len < 4096) {
                                        for (int i = 0; i < len; i++) {
                                            out.writeByte(data[i]);
                                        }
                                    } else {
                                        out.write(data);
                                    }
                                } catch (SocketException e) {
                                    it.remove();
                                }
                            }
                            timeSent += (len * 1000) / currByteRate;
                        } catch (InterruptedException e) {
                            return;
                        } finally {
                            binarySem.release();
                        }
                    } else {
                        in.close();
                        currentSong.setIsPlaying("");
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
