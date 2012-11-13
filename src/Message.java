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
	
	int getPort();
	int getContentLength();
	String getContentType();
	String getHost();
	String getOS();
	String getTitle();
	String getData();
	
	Utility.MSG_TYPE getType();
	
	void send(Socket socket);
	

}
