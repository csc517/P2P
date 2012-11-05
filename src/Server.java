import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.io.*;



public class Server extends Thread {
	
	public static HashMap<Integer,ArrayList<PeerInfo>> map = new HashMap<Integer, ArrayList<PeerInfo>>();
	
	Socket clientSocket;

	public Server(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}
	
	public void run() {
		try {
			Message msg = Utility.parseMessage(clientSocket);
			switch(msg.getType()) {
			case ADD:
				synchronized (Server.map) {
					ArrayList<PeerInfo> list = Server.map.get(Integer.valueOf(msg.getData()));
					PeerInfo peerinfo = new PeerInfo(msg.getPort(),
							msg.getHost(), msg.getTitle());
					if (null == list) {
						ArrayList<PeerInfo> arr = new ArrayList<PeerInfo>();
						arr.add(peerinfo);
						Server.map.put(Integer.valueOf(msg.getData()), arr);
					} else {
						list.add(peerinfo);
					}
				}
				break;
			case LOOKUP:
				ArrayList<PeerInfo> list = Server.map.get(Integer.valueOf(msg.getData()));
				if(null != list) {
					Iterator<PeerInfo> itr = list.iterator();
					StringBuffer buf = new StringBuffer();				
					while(itr.hasNext())
					{
						buf.append("RFC" + 
								Message.DELIMITER +
								msg.getData() +
								Message.DELIMITER +
								itr.next().getRFCInfoString());
					}
					//buf.append(Message.EOL);
					ResponseRFCMessage response = new ResponseRFCMessage(ResponseRFCMessage.RESPONSE_TYPE.OK);
					response.setResponseContent(buf);
					
				}
				else {
					//send not found
					ResponseRFCMessage response = new ResponseRFCMessage(ResponseRFCMessage.RESPONSE_TYPE.NOT_FOUND);
				}
				break;
			case LIST:
				return new ListRFCMessage(msg_type, inputStream);
			//case  RESPONSE:
			//	return new ResponseRFCMessage(msg_type, inputStream);
			default: 
				return null;
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int port;
		String hostName;
		ServerSocket serverSocket = null;
		Socket clientSocket = null;
		port = 1234;
		try {
			serverSocket = new ServerSocket(port);

		}
		catch(IOException e) {
			System.out.println("Error listening on port");
			System.exit(-1);
		}	
		while(true)
		{
			try {
				clientSocket = serverSocket.accept();	
			} catch(IOException e) {
				System.out.println("error accepting the connection");
			}
			new Server(clientSocket).start();
			
		}

	}
	

	

}
