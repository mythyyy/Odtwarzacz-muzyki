package Client.files;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class AudioPlayerThread extends Thread {

	private final int BUFFERSIZE = 32768 * 2;

	ConcurrentLinkedDeque<byte[]> deque;
	private AtomicInteger dequeCount = null;
	private AtomicBoolean doneFlag = null;

	public AudioPlayerThread(ConcurrentLinkedDeque<byte[]> deque, AtomicInteger dequeCount, AtomicBoolean doneFlag) {
		this.deque = deque;
		this.dequeCount = dequeCount;
		this.doneFlag = doneFlag;
	}

	private byte[] getDataBlock() {
		byte[] block = null;

		while (true) {
			if (deque.isEmpty() == false) {
				block = deque.poll();
				return block;
			} else {
				if (doneFlag.get()) {
					return null;
				} else {
					continue;
				}
			}
		}

	}

	@Override
	public void run() {
		System.out.println("Audio thread started");

		byte[] currentDataBlock = null;
		// AudioInputStream codedStream;

		currentDataBlock = getDataBlock();
		ByteArrayInputStream rawStream = new ByteArrayInputStream(currentDataBlock);
		try {
			AudioInputStream codedStream = AudioSystem.getAudioInputStream(rawStream);

			AudioFormat codedFormat = codedStream.getFormat();
			AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, codedFormat.getSampleRate(),16, codedFormat.getChannels(), codedFormat.getChannels() * 2, codedFormat.getSampleRate(), false);

			AudioInputStream decodedStream = AudioSystem.getAudioInputStream(decodedFormat, codedStream);

			DataLine.Info info = new DataLine.Info(SourceDataLine.class, decodedFormat);
			SourceDataLine audioLine = (SourceDataLine) AudioSystem.getLine(info);
			audioLine.open(decodedFormat);

			byte[] data = new byte[4096];

			if (audioLine != null) {
				audioLine.start();
				int nBytesRead = 0, nBytesWritten = 0;
				while (true) {

					nBytesRead = decodedStream.read(data, 0, data.length);
					if (nBytesRead != -1) {
						nBytesWritten = audioLine.write(data, 0, nBytesRead);
					}
					else {
						currentDataBlock = getDataBlock();
						if (currentDataBlock == null) {
							break;
						} else {
							decodedStream.close();
							codedStream.close();
							rawStream = new ByteArrayInputStream(currentDataBlock);
							codedStream = AudioSystem.getAudioInputStream(rawStream);
							decodedStream = AudioSystem.getAudioInputStream(decodedFormat, codedStream);
							continue;
						}
					}
				}

				// Stop
				audioLine.drain();
				audioLine.stop();
				audioLine.close();
				decodedStream.close();

			}

		} catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {

		}
	}

}
