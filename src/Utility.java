import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.HashMap;


public class Utility {
	public enum MSG_TYPE {
		GET, ADD, LOOKUP, LIST, RESPONSE;
		
		private static HashMap<Integer, MSG_TYPE> map;
		static {
			map = new HashMap<Integer, MSG_TYPE>();
			for (MSG_TYPE type: MSG_TYPE.values()) {
				map.put(type.ordinal(), type);
			}
		}
		
		public static MSG_TYPE getType(int ordinal) {
			return map.get(ordinal);
		}
	}

	public enum HDR_TYPE {
		HOST, PORT, TITLE, OS, DATE, LAST_MODIFIED, CONTENT_LENGTH, CONTENT_TYPE 
	}
	
	public static Message createMessage(Utility.MSG_TYPE msg_type) {
		switch(msg_type) {
			case GET:
				return new GetRFCMessage(msg_type);
			case ADD:
				return new AddRFCMessage(msg_type);
			case LOOKUP:
				return new LookupRFCMessage(msg_type);
			case LIST:
				return new ListRFCMessage(msg_type);
			case  RESPONSE:
				return new ResponseRFCMessage(msg_type);
			default: 
				return null;
		}
	}

	public static Message parseMessage(Socket incomingSocket) throws IOException {
		InputStream inputStream = incomingSocket.getInputStream();
		
		int msg_type_int = readInterger(inputStream);
		
		MSG_TYPE msg_type = MSG_TYPE.getType(msg_type_int);
		
		switch(msg_type) {
			case GET:
				return new GetRFCMessage(msg_type, inputStream);
			case ADD:
				return new AddRFCMessage(msg_type, inputStream);
			case LOOKUP:
				return new LookupRFCMessage(msg_type, inputStream);
			case LIST:
				return new ListRFCMessage(msg_type, inputStream);
			case  RESPONSE:
				return new ResponseRFCMessage(msg_type, inputStream);
			default: 
				return null;

		}
	}

	/**
	 * read up to size bytes and return a byte array
	 * @param inputStream
	 * @param buf
	 * @param size
	 * @return length of bytes read until end of stream or size specified is reached
	 * @throws IOException
	 */
	private static int readBytes(InputStream inputStream, byte[] buf, int size) throws IOException {
		int len = 0;
		int total_len = 0;

		while(total_len != size) {
			len = inputStream.read(buf, total_len, size-total_len);
			if(len < 0) {	//end of stream reached
				break;
			}
			total_len += len;
		}
		
		return total_len;
	}
	
	/**
	 * read an integer from the input stream
	 * @param inputStream
	 * @return int read from input stream
	 * @throws IOException
	 */
	private static int readInterger(InputStream inputStream) throws IOException {
		byte[] buf = new byte[Message.INT_LEN];	//read an integer to determine the type of message
		
		readBytes(inputStream, buf, Message.INT_LEN);
		
		//convert byte array to integer
		int read_int = 0;
		
		read_int = read_int | buf[0];
		read_int = read_int << 8;
		
		read_int = read_int | buf[1];
		read_int = read_int << 8;
		
		read_int = read_int | buf[2];
		read_int = read_int << 8;
		
		read_int = read_int | buf[3];
		
		/*DataInputStream dis = new DataInputStream(inputStream);
		return dis.readInt();*/
		
		return read_int;
	}

	public static void add_field(StringBuffer buf, String field, Object value) {
		buf.append( field +
				    ":" +
				    Message.DELIMITER +
					value.toString() +
					Message.EOL);
	}
	

}
