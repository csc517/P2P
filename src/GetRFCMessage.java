import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GetRFCMessage implements Message {
	
	Utility.MSG_TYPE msg_type;
	String host;
	String os;
	String data;
	int rfcNumber;
	InputStream inputStream = null;
	BufferedReader br = null;

	public GetRFCMessage(Utility.MSG_TYPE msg_type) {
		this.msg_type = msg_type;
	}

	public GetRFCMessage(Utility.MSG_TYPE msg_type, InputStream inputStream) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
		this.br = br;
		this.msg_type = Utility.MSG_TYPE.GET;
		this.inputStream = inputStream;		
	}

	@Override
	public void readMessage() throws IOException {
		String[] str = br.readLine().split(" ");
		this.rfcNumber = Integer.valueOf(str[1]);
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
		
		System.out.print("writing buffer:"+ buf.toString());
		
		pw.print(buf);
		pw.flush();
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
	public BufferedReader getBufferedReader() {
		return this.br;
	}

}
