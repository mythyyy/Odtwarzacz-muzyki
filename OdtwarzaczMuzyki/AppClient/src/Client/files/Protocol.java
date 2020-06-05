package Client.files;


public final class Protocol
{
	public final static int PROTOCOL_VERSION = 1662019;

	public final static int SERVER_WELCOME = 1;
	public final static int CLIENT_WELCOME = 2;
	public final static int CLIENT_DISCONNECTED = 0;
	
	public final static int SERVER_DATA_TEXT = 3;
	public final static int CLIENT_DATA_TEXT = 4;

	public final static int SERVER_DATA_AUDIO_NEW_FORMAT = 5;
	public final static int SERVER_DATA_AUDIO_CONTINUE = 6;
	
	public final static int PORT = 7312;
	
	public final static int DELAY = 3000;
	public final static String MOTD = new String("Polaczyles sie z serwerem projektu czatu i radia MP3!");
	
	
	
	private Protocol(){
	    
	    throw new AssertionError();
	  }

	
}