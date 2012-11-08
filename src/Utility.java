import java.awt.List;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
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

	private static void setField(Message msg, String str) {
		String[] strs = str.split(":");
		strs[1] = strs[1].trim();

		if(strs[0].equals("Host")) {
			msg.setHost(strs[1]);
		} else if(strs[0].equals("Port")) {
			msg.setPort( Integer.valueOf(strs[1]) );
		} else if(strs[0].equals("Title")) {
			msg.setTitle(strs[1]);
		} else if(strs[0].equals("Date")) {
			//			msg.set(strs[1]);
		} else if(strs[0].equals("OS")) {
			msg.setOS(strs[1]);
		} else if(strs[0].equals("Last Modified")) {
			//			msg.setTitle(strs[1]);
		} else if(strs[0].equals("Content Length")) {
			msg.setContentLength(Integer.valueOf(strs[1]));
		} else if(strs[0].equals("Content Type")) {
			msg.setContentType(strs[1]);
		}
	}

	public static void read_fields(Message msg, InputStream inputStream) throws IOException {
		int num_fields = readInterger(inputStream);

		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

		for(int i=0;i<num_fields;++i) {
			String str = br.readLine();
			setField(msg, str);
		}

	}

	public static void send_file(Socket socket, String filename) {
		//append default path to file name
		File myFile = new File(filename);
		byte[] mybytearray = new byte[(int) myFile.length()];
		try {
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(myFile));
			bis.read(mybytearray, 0, mybytearray.length);
			OutputStream os = socket.getOutputStream();
			os.write(mybytearray, 0, mybytearray.length);
			os.flush();
		}
		catch(IOException e) {
			System.out.println("error");
			System.exit(-1);
		}



	}

	public static ArrayList<String> textFiles() {
		  //change the below line to add custom dir path
		  String directory = null;
		  ArrayList<String> textFiles = new ArrayList<String>();
		  File dir = new File(directory);
		  for (File file : dir.listFiles()) {
		    if (file.getName().endsWith((".txt"))) {
		      textFiles.add(file.getName());
		    }
		  }
		  return textFiles;
	}
	
	
}
