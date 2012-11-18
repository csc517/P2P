
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Calendar;

public class ResponseRFCMessage implements Message {
	
	int contentLength;
	String contentType;
	Calendar date;
	StringBuffer responseContent;
	Calendar lastModified;
	String data;
	
	BufferedReader br = null;
	InputStream inputStream = null;
	
	Utility.MSG_TYPE msg_type;	//message type - message is a response
	Utility.RESPONSE_TYPE responseType;	//type of response
	Utility.MSG_TYPE request_type;	//request type for which this a response

	
	public ResponseRFCMessage(Utility.RESPONSE_TYPE response_type) {
		this.msg_type = Utility.MSG_TYPE.RESPONSE;
		this.responseType = response_type;
	}
		
	public ResponseRFCMessage(Utility.MSG_TYPE msg_type, InputStream inputStream) {
		this.msg_type = msg_type;
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
		this.inputStream = inputStream;
		this.br = br;
	}
	
	public void readMessage()  throws IOException {
		System.out.println("reading line from response...");
		String str = br.readLine();
		this.responseType = Utility.RESPONSE_TYPE.getType(Integer.valueOf(str.split(" ")[1]));
		System.out.println(str);
//		Utility.read_fields(this, inputStream);
		int num_fields = Integer.valueOf(br.readLine());
		System.out.println("Number of fields: "+num_fields);
		
		for(int i=0;i<num_fields;++i) {
			String field = br.readLine();
			System.out.println(field);
		}
	}
	
	public StringBuffer getResponseContent() {
		return responseContent;
	}

	public void setResponseContent(StringBuffer responseContent) {
		this.responseContent = responseContent;
	}	
	public Calendar getLastModified() {
		return lastModified;
	}

	public void setLastModified(Calendar lastModified) {
		this.lastModified = lastModified;
	}

	public Calendar getDate() {
		return date;
	}

	public void setDate() {
		this.date = Calendar.getInstance();
	}
	
	@Override
	public void setOS(String os) {	
	}
	
	
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	public void setContentLength(int contentLength) {
		this.contentLength = contentLength;
	}
	
	@Override
	public void setData(String data) {
		this.data = data;
	}
	
	public void setResponse(Utility.RESPONSE_TYPE response) {
		this.responseType = response;
	}
	
	@Override
	public int getContentLength() {
		return 0;
	}

	@Override
	public String getOS() {
		return null;
	}
	
	@Override
	public String getContentType() {
		return null;
	}
	
	public void sendServerResponse(Socket socket) throws IOException {
		StringBuffer buf = new StringBuffer();
		
		PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
		
		Utility.writeInteger(socket, this.msg_type.ordinal());
		
		buf.append(	VERSION +
					DELIMITER +
					this.responseType.ordinal() +
					DELIMITER +
					Utility.RESPONSE_TYPE.getResponseString(this.responseType) +
					EOL);
		
		buf.append(this.getResponseContent());		

		System.out.print("response buf: "+ buf);
		
		pw.print(buf);
		pw.flush();		
	}
	
	public void sendPeerResponse(Socket socket) {
		StringBuffer buf = new StringBuffer();
		
		buf.append(this.msg_type +
					DELIMITER +
					VERSION +
					DELIMITER +
					Utility.RESPONSE_TYPE.getResponseString(this.responseType) +
					EOL);
		
		if(this.responseType == Utility.RESPONSE_TYPE.OK)
		{
			buf.append(5);
			Utility.add_field(buf, "Date", this.getDate());
			Utility.add_field(buf, "OS", this.getOS());
			Utility.add_field(buf, "Last Modified", this.getLastModified());
			Utility.add_field(buf, "Content Length", this.getContentLength());
			Utility.add_field(buf, "Content Type", this.getContentType());
		}
		try {
			PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
			pw.print(buf);
			pw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void setHost(String host) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPort(int port) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTitle(String title) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getPort() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getHost() {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		
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
		this.br = br;
	}

	@Override
	public BufferedReader getBufferedReader() {
		return br;
	}

	public Utility.RESPONSE_TYPE getResponseType() {
		return this.responseType;
	}

}
