import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;


public class AddRFCMessage implements Message {

	int port;
	String host;
	String title;
	String data;
	int rfcNumber;
	Utility.MSG_TYPE msg_type;
	BufferedReader br = null;
	InputStream inputStream = null;
	
	public AddRFCMessage(Utility.MSG_TYPE msg_type) {
		this.msg_type = msg_type;
	}

	public AddRFCMessage(Utility.MSG_TYPE msg_type, InputStream inputStream) {
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
		this.br = br;
		this.msg_type = Utility.MSG_TYPE.ADD;
		this.inputStream = inputStream;
	}
	
	public void readMessage() throws IOException {
		String[] str = br.readLine().split(" ");
		this.rfcNumber = Integer.valueOf(str[1]);
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
	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public int getPort() {
		return this.port;
	}

	@Override
	public String getHost() {
		return this.host;
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
	public String toString() {
		return "AddRFCMessage [port=" + port + ", host=" + host + ", title="
				+ title + ", data=" + data + ", rfcNumber=" + rfcNumber
				+ ", msg_type=" + msg_type + "]";
	}

	@Override
	public void send(Socket socket) throws IOException {
		StringBuffer buf = new StringBuffer();
		
		PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
		
		Utility.writeInteger(socket, this.msg_type.ordinal());

		buf.append( "RFC" +
					DELIMITER +
					this.rfcNumber +
					DELIMITER +
					VERSION +
					EOL);
		
		buf.append(3 + EOL);
		
//		Utility.writeInteger(socket, 3);
		
		Utility.add_field(buf, "Host", this.getHost());
		Utility.add_field(buf, "Port", this.getPort());
		Utility.add_field(buf, "Title", this.getTitle());
		
		System.out.println("writing buffer:"+buf.toString());
		
		pw.print(buf);
		pw.flush();						
	}
	
	@Override
	public String getOS() {
		return null;
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
	public int getContentLength() {
		return 0;
	}

	@Override
	public String getContentType() {
		return null;
	}

	@Override
	public void setRFCNumber(int rfcNumber) {
		this.rfcNumber = rfcNumber;		
	}

	@Override
	public int getRFCNumber() {
		return this.rfcNumber;
	}

	@Override
	public void setBufferedReader(BufferedReader br) {
		this.br = br;
	}
	
	@Override
	public  BufferedReader getBufferedReader() {
		return this.br;
	}
}
