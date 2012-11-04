import java.net.Socket;


public class Utility {
	public enum MSG_TYPE {
		GET, ADD, LOOKUP, LIST, RESPONSE
	}

	public enum HDR_TYPE {
		HOST, PORT, TITLE, OS, DATE, LAST_MODIFIED, CONTENT_LENGTH, CONTENT_TYPE 
	}
	
	public static Message createMessage(Utility.MSG_TYPE msg_type, String version) {
		switch(msg_type) {
			case GET:
				//return new GetRFCMessage(msg_type, version);
			case ADD:
				return new AddRFCMessage(msg_type, version);
			case LOOKUP:
				//return new LookupRFCMessage(msg_type, version);
			case LIST:
				//return new ListRFCMessage(msg_type, version);
			case  RESPONSE:
				//return new ResponseRFCMessage(msg_type, version);
			default: return null;
		}
	}

	public static Message parseMessage(Socket incomingSocket) {
		return null;
	}
	

}
