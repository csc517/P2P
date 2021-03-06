
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
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;


public class Utility {
	
	static int serverPort = 12345;
	
	public enum RESPONSE_TYPE {
		OK, BAD_REQ, NOT_FOUND, VERSION_UNSUPPORTED;
		
		private static HashMap<RESPONSE_TYPE, Status> statusMap;
		static {
			statusMap = new HashMap<RESPONSE_TYPE, Status>();
			statusMap.put(RESPONSE_TYPE.OK, new Status(200, "OK"));
			statusMap.put(RESPONSE_TYPE.BAD_REQ, new Status(400, "Bad Request"));
			statusMap.put(RESPONSE_TYPE.NOT_FOUND, new Status(404, "Not Found"));
			statusMap.put(RESPONSE_TYPE.VERSION_UNSUPPORTED, new Status(505, "P2P-CI Version Not Supported"));
		}
		
		public static String getResponseString(RESPONSE_TYPE responseType) {
			Status status = statusMap.get(responseType);
			return(status.getStatusCode() + " " + status.getMessage());			
		}
		
		private static HashMap<Integer, RESPONSE_TYPE> map;
		static {
			map = new HashMap<Integer, RESPONSE_TYPE>();
			System.out.println("creating RESPONSE_TYPE map...");
			for (RESPONSE_TYPE type: RESPONSE_TYPE.values()) {
				System.out.println("Adding " + type.ordinal() + ":"+type);
				map.put(type.ordinal(), type);
			}
		}

		public static RESPONSE_TYPE getType(int ordinal) {
			return map.get(ordinal);
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
		BufferedReader br = new BufferedReader(new InputStreamReader(incomingSocket.getInputStream()));
		
		//System.out.println("Trying to read msg type...");
		String s = br.readLine();
		System.out.println("line read : " + s);
		int msg_type_int = Integer.valueOf(s); //readInteger(inputStream);
		
		//System.out.println("MSG_TYPE: "+ msg_type_int);

		MSG_TYPE msg_type = MSG_TYPE.getType(msg_type_int);

		switch(msg_type) {
		case GET:
			return new GetRFCMessage(msg_type, inputStream, br);
		case ADD:
			return new AddRFCMessage(msg_type, inputStream, br);
		case LOOKUP:
			return new LookupRFCMessage(msg_type, inputStream, br);
		case LIST:
			return new ListRFCMessage(msg_type, inputStream, br);
		case  RESPONSE:
			return new ResponseRFCMessage(msg_type, inputStream, br);
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
		ByteBuffer integerBuffer = ByteBuffer.allocate(Message.INT_LEN);
		integerBuffer.putInt(writeInt);
		for(int i=integerBuffer.array().length-1;i>=0;--i) {
			System.out.print(integerBuffer.array()[i] + " ");
		}
		System.out.println();
		socket.getOutputStream().write(integerBuffer.array());
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
		File rfcFile = new File(getRFCFilename(rfcNumber));
		
		if(!rfcFile.exists()) {
			return;
		}
		  
		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);                    
        int fileLength = (int)rfcFile.length();
        
        out.println(Integer.toString(rfcNumber));
        out.println(Integer.toString(fileLength));
        ServerSocket fileServerSocket = Peer.getServerSocket();
        out.println(fileServerSocket.getLocalPort());
        out.println(fileServerSocket.getInetAddress().getHostName());
        out.flush();
        
        Socket fileSocket = fileServerSocket.accept();
   
        BufferedOutputStream bos = new BufferedOutputStream(fileSocket.getOutputStream());
 
        InputStream inputStream = new FileInputStream(rfcFile);
        
        System.out.println("From file to socket...");
        
        byte[] buf = new byte[1000];
        int len = 0;
		int total_len = 0;

		while(total_len != fileLength) {
			len = readBytes(inputStream, buf, 1000);
			if(len <= 0) {	//end of stream reached
				break;
			}
			total_len += len;
			bos.write(buf, 0, len);
		}

        bos.flush();
        
        fileSocket.close();
        fileServerSocket.close();
        
        System.out.println("From file to socket...DONE");
	}

	public static String recv_file(Socket socket, BufferedReader in) throws IOException {      
        
        int rfcNumber = Integer.valueOf(in.readLine());
        int fileLength = Integer.parseInt(in.readLine());
        
        int fileTransferPort = Integer.parseInt(in.readLine());
        String fileTransferHost = in.readLine();
        
		File newRFCFile = new File(getRFCFilename(rfcNumber));
		newRFCFile.createNewFile();
        
        Socket fileTransferSocket = new Socket(fileTransferHost, fileTransferPort); 
  
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(newRFCFile));  
        
        InputStream inputStream = fileTransferSocket.getInputStream();

        System.out.println("From socket to file...");
        
        byte[] buf = new byte[1000];
        int len = 0;
		int total_len = 0;

		while(total_len != fileLength) {
			len = readBytes(inputStream, buf, 1000);
			if(len <= 0) {	//end of stream reached
				break;
			}
			total_len += len;
			bos.write(buf, 0, len);
		}

        bos.flush();
        
		System.out.println("From socket to file...DONE");
		
		fileTransferSocket.close();
		
		BufferedReader lineReader = new BufferedReader(new FileReader(newRFCFile));
		return lineReader.readLine().split(" ")[2];
	}

	public static ArrayList<String> textFiles(File dir) {
		  ArrayList<String> textFiles = new ArrayList<String>();		  
		  if(null != dir) {
			  for (File file : dir.listFiles()) {
				  if (file.getName().endsWith((".txt"))) {
					  textFiles.add(file.getAbsolutePath());
				  }
			  }
		  }
		  return textFiles;
	}

	public static String getRFCFilename(int rfcNumber) {
		return Peer.workingDir + File.separator + new Integer(rfcNumber).toString() + ".txt";
	}
	
}
