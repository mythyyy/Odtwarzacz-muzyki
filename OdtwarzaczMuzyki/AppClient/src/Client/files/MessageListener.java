package Client.files;

import Client.WritableGUI;
import Client.view.ClientViewController;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Wycinek klasy klienta obslugujacej transmisje Serwer -> Klient
 */

public class MessageListener extends Thread{

	private ConcurrentLinkedDeque<byte[]> deque = null;
	private AtomicInteger dequeCount = null;
	private AtomicBoolean doneFlag = null;
	private AudioPlayerThread audioThread = null;




	private Socket socket;
	private DataInputStream in;
	private DataOutputStream out;

	boolean firstSetupFlag = false;



	WritableGUI gui;
	String ip;

	public MessageListener(ClientViewController gui, String ip){
	    this.gui = gui;
	    this.ip = ip;
    }

	public Socket getSocket()
	{
		return socket;
	}
	
	public DataOutputStream getOutputStream()
	{
		return out;
	}

	public void stopPlayer() throws IOException {
		if(audioThread.isAlive())
			audioThread.interrupt();
		socket.close();
	}

	public void run() {
		/*
		 * Polaczenie z serwerem
		 */
		try {
			socket = new Socket(ip, Protocol.PORT);
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
			/*
			 * Inicjalizacja obiektow
			 */
			deque = new ConcurrentLinkedDeque<byte[]>();
			dequeCount = new AtomicInteger(0);
			doneFlag = new AtomicBoolean(false);
			audioThread = new AudioPlayerThread(deque, dequeCount, doneFlag);
			/*
			 * Rozpocz�cie w�tku audio
			 */
			audioThread.start();
			/*
			 * Deklaracja zmiennych pomocniczych
			 */
			int type;
			int len;
			byte[] data;
			ByteArrayOutputStream interBuffer = new ByteArrayOutputStream();
			while (true) {
				/*
				 * Odczyt metadanych
				 */
				type = in.readInt();
				switch (type) {
					case Protocol.SERVER_WELCOME:
						System.out.println("Wersja protokolu: " + in.readInt());
						gui.write(in.readUTF());
						out.writeInt(Protocol.CLIENT_WELCOME);
						break;
					case Protocol.SERVER_DATA_TEXT:
						gui.write(in.readUTF());
						break;
					case Protocol.SERVER_DATA_AUDIO_NEW_FORMAT:
						System.out.println("Playing: " + in.readUTF());
					case Protocol.SERVER_DATA_AUDIO_CONTINUE:
						len = in.readInt();
						// System.out.println(len);
						if (len > 4096 || len < 0) {
							len = 4096;
						}
						for (int i = 0; i < len; i++) {
							if (interBuffer.size() < 32768 * 2) {
								interBuffer.write(in.readByte());
							} else {
								deque.add(interBuffer.toByteArray());
								interBuffer.reset();
								interBuffer.write(in.readByte());
								// System.out.println("Writing to queue " + deque.size());
							}
						}
						break;
					default:
						System.out.println("typ " + type);
						break;
				}
			}
		}
		catch (IOException e) {
			return;
		}
		finally {
			try {
				in.close();
				out.close();
				socket.close();
			}
			catch(IOException e)
			{

			}
		}
	}



}
