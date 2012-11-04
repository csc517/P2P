import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;


public class LookupRFCMessage implements Message {
	
	Utility.MSG_TYPE msg_type;
	String host;
	int port;
	String title;
	String data;

	public LookupRFCMessage(Utility.MSG_TYPE msg_type) {
		this.msg_type = msg_type;
	}

	public LookupRFCMessage(Utility.MSG_TYPE msg_type, InputStream inputStream) throws IOException {
		Utility.read_fields(this, inputStream);
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
	public void setData(String data) {
		this.data = data;
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
	public String getData() {
		return this.data;
	}

	@Override
	public Utility.MSG_TYPE getType() {
		return this.msg_type;
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

		buf.append(3);	//write number of fields
		Utility.add_field(buf, "Host", this.getHost());
		Utility.add_field(buf, "Port", this.getPort());
		Utility.add_field(buf, "Title", this.getTitle());

		try {
			PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
			pw.print(buf);
			pw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
