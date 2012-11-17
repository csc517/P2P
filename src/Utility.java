import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import com.sun.corba.se.impl.ior.ByteBuffer;


public class Utility {
	static int serverPort = 12345;
	
	public enum RESPONSE_TYPE {
		OK, BAD_REQ, NOT_FOUND, VERSION_UNSUPPORTED;
		
		private static HashMap<RESPONSE_TYPE, Status> map;
		static {
			map = new HashMap<RESPONSE_TYPE, Status>();
			map.put(RESPONSE_TYPE.OK, new Status(200, "OK"));
			map.put(RESPONSE_TYPE.BAD_REQ, new Status(400, "Bad Request"));
			map.put(RESPONSE_TYPE.NOT_FOUND, new Status(404, "Not Found"));
			map.put(RESPONSE_TYPE.VERSION_UNSUPPORTED, new Status(505, "P2P-CI Version Not Supported"));
		}
		
		public static String getResponseString(RESPONSE_TYPE responseType) {
			Status status = map.get(responseType);
			return(status.getStatusCode() + " " + status.getMessage());
			
			
		}
		
	}
	
	public enum MSG_TYPE {
		GET, ADD, LOOKUP, LIST, RESPONSE;

		private static HashMap<Integer, MSG_TYPE> map;
		static {
			map = new HashMap<Integer, MSG_TYPE>();
			System.out.println("creating map...");
			for (MSG_TYPE type: MSG_TYPE.values()) {
				System.out.println("Adding " + type.ordinal() + ":"+type);
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
		default: 
			return null;
		}
	}

	public static Message parseMessage(Socket incomingSocket) throws IOException {
		InputStream inputStream = incomingSocket.getInputStream();
		
		System.out.println("Trying to read msg type...");

		int msg_type_int = readInteger(inputStream);
		
		System.out.println("MSG_TYPE: "+ msg_type_int);

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
	private static int readInteger(InputStream inputStream) throws IOException {
		byte[] buf = new byte[Message.INT_LEN];	//read an integer to determine the type of message

		readBytes(inputStream, buf, Message.INT_LEN);

		int final_read_int = 0;
		int read_int = 0;

		final_read_int = final_read_int | buf[0];

		read_int = buf[1];
		read_int = read_int << 8;
		final_read_int = final_read_int | read_int;
		
		read_int = buf[2];
		read_int = read_int << 16;
		final_read_int = final_read_int | read_int;

		read_int = buf[3];
		read_int = read_int << 24;
		final_read_int = final_read_int | read_int;

		/*DataInputStream dis = new DataInputStream(inputStream);
		return dis.readInt();*/

		return final_read_int;
	}
	
	public static void writeInteger(Socket socket, int writeInt) throws IOException {
		System.out.println("Writing integer: "+writeInt);
		ByteBuffer integerBuffer = new ByteBuffer(Message.INT_LEN);
		integerBuffer.append(writeInt);
		for(int i=integerBuffer.size()-1;i>=0;--i) {
			System.out.print(integerBuffer.toArray()[i] + " ");
		}
		System.out.println();
		socket.getOutputStream().write(integerBuffer.toArray());
		socket.getOutputStream().flush();
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
		System.out.println("Reading number of fields...");
		
		BufferedReader br = msg.getBufferedReader();
		
		int num_fields = Integer.valueOf(br.readLine());
		System.out.println("Number of fields: "+num_fields);
		
		for(int i=0;i<num_fields;++i) {
			String str = br.readLine();
			setField(msg, str);
		}

	}

	public static void send_file (Socket socket, int rfcNumber) throws FileNotFoundException, IOException {
		
		//append default path to file name
		File rfcFile = new File(new Integer(rfcNumber).toString());
		
		if(!rfcFile.exists()) {
			return;
		}
		  
		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);                    
        int len = (int)rfcFile.length();    
        
        out.println(Integer.toString(rfcNumber));
        out.println(Integer.toString(len));
        out.flush();
  
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(rfcFile));  
        BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
        
        byte[] byteArray = new byte[1000];  
        int i=0;
        System.out.println("From file to socket...");
        while ((i = bis.read(byteArray)) != len){  
            bos.write(byteArray, 0, i);  
        }
        bos.flush();
        System.out.println("From file to socket...DONE");
	}

	public static String recv_file(Socket socket) throws IOException {
		
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));      
        
        int rfcNumber = Integer.valueOf(in.readLine());
        int length = Integer.parseInt(in.readLine());
        
		File newRFCFile = new File(new Integer(rfcNumber).toString());
		newRFCFile.createNewFile();
        
        BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());  
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(newRFCFile));  
        
        int IN=0;   
        byte[] receivedData = new byte[1000];
        System.out.println("From socket to file...");
        while ((IN = bis.read(receivedData)) != length){  
            bos.write(receivedData,0,IN);  
        }
		bos.flush();
		System.out.println("From socket to file...DONE");
		
		BufferedReader lineReader = new BufferedReader(new FileReader(newRFCFile));
		return lineReader.readLine().split(" ")[2];
	}

	public static ArrayList<String> textFiles(String directory) {
		  ArrayList<String> textFiles = new ArrayList<String>();		  
		  if(null != directory) {
			  File dir = new File(directory);
			  for (File file : dir.listFiles()) {
				  if (file.getName().endsWith((".txt"))) {
					  textFiles.add(file.getName());
				  }
			  }
		  }
		  return textFiles;
	}
	
}
