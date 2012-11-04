import java.net.Socket;



public interface Message {
	String VERSION = "P2P-CI/1.0";
	String DELIMITER = " ";
	String EOL = "\r\n";
	
	void setHost(String host);
	void setPort(int port);
	void setOS(String os);
	void setContentType(String content_type);
	void setContentLength(int length);
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
	
	void send(Socket connectSocket);

}
