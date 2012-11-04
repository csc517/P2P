import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class GetRFCMessage implements Message {
	
	Utility.MSG_TYPE msg_type;
	String host;
	String os;
	String data;

	public GetRFCMessage(Utility.MSG_TYPE msg_type) {
		this.msg_type = msg_type;
	}

	public GetRFCMessage(Utility.MSG_TYPE msg_type, InputStream inputStream) throws IOException {
		Utility.read_fields(this, inputStream);
	}

	@Override
	public void setHost(String host) {
		this.host = host;
	}

	@Override
	public void setPort(int port) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setOS(String os) {
		this.os = os;
	}

	@Override
	public void setContentType(String content_type) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setContentLength(int length) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTitle(String title) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setData(String data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getPort() {
		return -1;
	}

	@Override
	public int getContentLength() {
		// TODO Auto-generated method stub
		return -1;
	}

	@Override
	public String getContentType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getHost() {
		// TODO Auto-generated method stub
		return this.host;
	}

	@Override
	public String getOS() {
		// TODO Auto-generated method stub
		return this.os;
	}

	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Utility.MSG_TYPE getType() {
		// TODO Auto-generated method stub
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

		buf.append(2);	//write number of fields
		Utility.add_field(buf, "Host", this.getHost());
		Utility.add_field(buf, "OS", this.getOS());

		try {
			PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
			pw.print(buf);
			pw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
