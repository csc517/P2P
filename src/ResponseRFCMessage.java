import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import Utility.MSG_TYPE;


public class ResponseRFCMessage implements Message {
	
	Utility.MSG_TYPE msg_type;
	int contentLength;
	String contentType;
	Calendar date;


	Calendar lastModified;
	String data;
	RESPONSE_TYPE responseType;
	
	public class Status {
		int statusCode;
		String message;
			
		public Status(int statusCode, String message) {
			this.statusCode = statusCode;
			this.message = message;
		}
		
		public int getStatusCode() {
			return statusCode;
		}

		public void setStatusCode(int statusCode) {
			this.statusCode = statusCode;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

	}
	
	
	public ResponseRFCMessage(Utility.MSG_TYPE msg_type) {
		this.msg_type = msg_type;
	}
	
	
	public enum RESPONSE_TYPE {
		OK, BAD_REQ, NOT_FOUND, VERSION_UNSUPPORTED;
		
		private static HashMap<RESPONSE_TYPE, Status> map;
		static {
			map = new HashMap<RESPONSE_TYPE, Status>();
			RESPONSE_TYPE type;
			map.put(type.OK, new Status(200, "OK"));
			map.put(type.BAD_REQ, new Status(400, "Bad Request"));
			map.put(type.NOT_FOUND, new Status(404, "Not Found"));
			map.put(type.VERSION_UNSUPPORTED, new Status(505, "P2P-CI Version Not Supported"));
		}
		
		public static String getResponseString(RESPONSE_TYPE responseType) {
			Status status = map.get(responseType);
			return(status.getStatusCode() + " " + status.getMessage());
			
			
		}
		
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
	
	public void setResponse(RESPONSE_TYPE response) {
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
	
	public void sendServerResponse(Socket socket) {
		StringBuffer buf = new StringBuffer();
		
		buf.append(VERSION +
				   DELIMITER +
				   RESPONSE_TYPE.getResponseString(this.responseType) +
				   EOL);
		
		if(this.responseType == RESPONSE_TYPE.OK)
		{
			
		}
		try {
			PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
			pw.print(buf);
			pw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void sendPeerResponse(Socket socket) {
		StringBuffer buf = new StringBuffer();
		
		buf.append(VERSION +
				   DELIMITER +
				   RESPONSE_TYPE.getResponseString(this.responseType) +
				   EOL);
		
		if(this.responseType == RESPONSE_TYPE.OK)
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

}
