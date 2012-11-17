import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;



public interface Message {
	static String VERSION = "P2P-CI/1.0";
	static String DELIMITER = " ";
	static String EOL = "\r\n";
	static int INT_LEN = 4;
	
	void setHost(String host);
	void setPort(int port);
	void setOS(String os);
	void setContentType(String content_type);
	void setContentLength(int length);
	void setRFCNumber(int rfcNumber);
	
	void setTitle(String title);
	void setData(String data);
	void setBufferedReader(BufferedReader br);
	
	int getPort();
	int getContentLength();
	int getRFCNumber();
	String getContentType();
	String getHost();
	String getOS();
	String getTitle();
	String getData();
	
	BufferedReader getBufferedReader();
	
	Utility.MSG_TYPE getType();
	
	void send(Socket socket) throws IOException;
	void readMessage() throws IOException;
	

}
