import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class ListRFCMessage implements Message {
	
	Utility.MSG_TYPE msg_type;
	String data;
	String host;
	int port;
	int rfcNumber;
	String os;
	private BufferedReader br;
	private InputStream inputStream;
	

	public int getRfcNumber() {
		return rfcNumber;
	}

	public void setRfcNumber(int rfcNumber) {
		this.rfcNumber = rfcNumber;
	}

	public ListRFCMessage(Utility.MSG_TYPE msg_type) {
		this.msg_type = msg_type;
	}

	public ListRFCMessage(Utility.MSG_TYPE msg_type, InputStream inputStream, BufferedReader br) throws IOException {
		//Utility.read_fields(this, inputStream);
		this.br = br;
		this.msg_type = Utility.MSG_TYPE.LIST;
		this.inputStream = inputStream;
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
		this.os = os;
	}

	@Override
	public void setContentType(String content_type) {
		
	}

	@Override
	public void setContentLength(int length) {
		
	}

	@Override
	public void setTitle(String title) {
		
	}

	@Override
	public void setData(String data) {
		
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
		return this.os;
	}

	@Override
	public String getTitle() {
		return null;
	}

	@Override
	public String getData() {
		return null;
	}

	@Override
	public Utility.MSG_TYPE getType() {
		return this.msg_type;
	}

	@Override
	public void send(Socket socket) {
		StringBuffer buf = new StringBuffer();
	
		buf.append( this.msg_type.ordinal() + EOL +
				"RFC" +
				DELIMITER +
				VERSION +
				EOL);

		buf.append(1 + EOL);	//write number of fields
		//Utility.add_field(buf, "Host", this.getHost());
		//Utility.add_field(buf, "Port", this.getPort());
		Utility.add_field(buf, "OS", this.getOS());
		try {
			PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
			pw.print(buf);
			pw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void setRFCNumber(int rfcNumber) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getRFCNumber() {
		return 0;
	}

	@Override
	public void setBufferedReader(BufferedReader br) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public BufferedReader getBufferedReader() {
		// TODO Auto-generated method stub
		return this.br;
	}

	@Override
	public void readMessage() throws IOException {
		String[] str = br.readLine().split(" ");
		//this.rfcNumber = Integer.valueOf(str[1]);
		Utility.read_fields(this, inputStream);
		
	}

}
