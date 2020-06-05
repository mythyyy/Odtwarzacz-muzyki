package Server.music;

import javafx.beans.property.*;
import org.apache.commons.io.FilenameUtils;
import org.tritonus.share.sampled.TAudioFormat;
import org.tritonus.share.sampled.file.TAudioFileFormat;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.Map;


public class Song {
	private final StringProperty isPlaying;
	private final StringProperty path;
	private final StringProperty displayName;
	private final LongProperty duration;
	private final StringProperty durationString;
	private final IntegerProperty bitrate;


	public Song(String path) throws UnsupportedAudioFileException, IOException {
		if ((FilenameUtils.getExtension(path)).equalsIgnoreCase("mp3") != true)
			throw new UnsupportedAudioFileException("File extension not mp3");

		this.path = new SimpleStringProperty(path);
		this.isPlaying = new SimpleStringProperty(" ");

		File file = new File(path);
		String author = new String();
		String title = new String();
		long duration = 0;

		AudioFileFormat baseFileFormat = AudioSystem.getAudioFileFormat(file);
		AudioFormat baseFormat =baseFileFormat.getFormat();

		if (baseFileFormat instanceof TAudioFileFormat) {
			Map properties = ((TAudioFileFormat) baseFileFormat).properties();

			author = (String) properties.get("author");
			title = (String) properties.get("title");
			duration = (long) properties.get("duration");
		}


		if (baseFormat instanceof TAudioFormat) {
			Map properties = ((TAudioFormat) baseFormat).properties();
			this.bitrate = new SimpleIntegerProperty((Integer) properties.get("bitrate"));
		} else {
			this.bitrate = new SimpleIntegerProperty(0);
			throw new UnsupportedAudioFileException("Cannot read bitrate");
		}

		if (author != null || title != null) {
			if (author.isEmpty() == false || title.isEmpty() == false) {
				this.displayName = new SimpleStringProperty(author + " - " + title);
			} else {
				this.displayName = new SimpleStringProperty(path);
			}
		} else {
			this.displayName = new SimpleStringProperty(path);
		}

		if (duration > 0) {
			this.duration = new SimpleLongProperty(duration);
			Long minutes, seconds;
			minutes = (duration / 1000000) / 60;
			seconds = (duration / 1000000) % 60;
			this.durationString = new SimpleStringProperty(minutes.toString() + ":" + seconds.toString());
		} else {
			this.duration = new SimpleLongProperty(0);
			this.durationString = new SimpleStringProperty("?:??");
		}

	}

	public final StringProperty pathProperty() {
		return this.path;
	}

	public final String getPath() {
		return this.pathProperty().get();
	}

	public final void setPath(final String path) {
		this.pathProperty().set(path);
	}

	public final StringProperty displayNameProperty() {
		return this.displayName;
	}

	public final String getDisplayName() {
		return this.displayNameProperty().get();
	}

	public final void setDisplayName(final String displayName) {
		this.displayNameProperty().set(displayName);
	}

	public final LongProperty durationProperty() {
		return this.duration;
	}

	public final long getDuration() {
		return this.durationProperty().get();
	}

	public final void setDuration(final long duration) {
		this.durationProperty().set(duration);
	}

	public final StringProperty durationStringProperty() {
		return this.durationString;
	}

	public final String getDurationString() {
		return this.durationStringProperty().get();
	}

	public final void setDurationString(final String durationString) {
		this.durationStringProperty().set(durationString);
	}

	public final IntegerProperty bitrateProperty() {
		return this.bitrate;
	}

	public final int getBitrate() {
		return this.bitrateProperty().get();
	}

	public final void setBitrate(final int bitrate) {
		this.bitrateProperty().set(bitrate);
	}

	public final StringProperty isPlayingProperty() {
		return this.isPlaying;
	}

	public final String getIsPlaying() {
		return this.isPlayingProperty().get();
	}

	public final void setIsPlaying(final String isPlaying) {
		this.isPlayingProperty().set(isPlaying);
	}

}
