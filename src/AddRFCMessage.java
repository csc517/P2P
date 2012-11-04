import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;


public class AddRFCMessage implements Message {

	int port;
	String host;
	String title;
	String data;
	String version;
	Utility.MSG_TYPE msg_type;
	
	public AddRFCMessage(Utility.MSG_TYPE msg_type, String version) {
		this.msg_type = msg_type;
		this.version = version;
	}

	@Override
	public void setHost(String host) {
		this.host = host;
	}

	@Override
	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public void setOS(String os) {	
	}

	@Override
	public void setContentType(String content_type) {
	}

	@Override
	public void setContentLength(int length) {
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public int getPort() {
		return this.port;
	}

	@Override
	public int getContentLength() {
		return 0;
	}

	@Override
	public String getContentType() {
		return null;
	}

	@Override
	public String getHost() {
		return this.host;
	}

	@Override
	public String getOS() {
		return null;
	}

	@Override
	public String getTitle() {
		return this.title;
	}

	@Override
	public Utility.MSG_TYPE getType() {
		return this.msg_type;
	}

	@Override
	public void setData(String data) {
		this.data = data;
	}

	@Override
	public String getData() {
		return this.data;
	}

	@Override
	public void send(Socket socket) {
		StringBuffer buf = new StringBuffer();
		
		buf.append( this.msg_type +
					DELIMITER +
					"RFC" +
					DELIMITER +
					this.data +
					DELIMITER +
					VERSION +
					EOL);
		
		add_field(buf, "Host", this.getHost());
		add_field(buf, "Port", this.getPort());
		add_field(buf, "Title", this.getTitle());
		
		try {
			PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
			pw.print(buf);
			pw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}				
	}

	private void add_field(StringBuffer buf, String field, Object value) {
		buf.append( field +
				    ":" +
					DELIMITER +
					value.toString() +
					EOL);
	}
}
