import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.io.*;



public class Server extends Thread {
	
	public static HashMap<Integer,ArrayList<PeerInfo>> map = 
			new HashMap<Integer, ArrayList<PeerInfo>>();
	
	Socket clientSocket;

	public Server(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}
	
	public void run() {
		while (true) {
			try {
				Message msg = Utility.parseMessage(clientSocket);
				msg.readMessage();

				switch (msg.getType()) {
				case ADD:
					synchronized (Server.map) {
						ArrayList<PeerInfo> list = Server.map.get(msg
								.getRFCNumber());

						PeerInfo peerinfo = new PeerInfo(msg.getPort(),
								msg.getHost(), msg.getTitle());
						System.out.println(peerinfo);

						if (null == list) {
							ArrayList<PeerInfo> arr = new ArrayList<PeerInfo>();
							arr.add(peerinfo);
							Server.map.put(msg.getRFCNumber(), arr);
						} else {
							list.add(peerinfo);
						}
					}

					ResponseRFCMessage response = new ResponseRFCMessage(Utility.RESPONSE_TYPE.OK);
					
					StringBuffer buf = new StringBuffer();
					
					buf.append(0 + Message.EOL);
					
					response.setResponseContent(buf);
					response.sendServerResponse(this.clientSocket);
					
					System.out.println("Sent response for ADD...");
					
					break;
				case LOOKUP:
					StringBuffer lookupResBuf = new StringBuffer();
					lookupResBuf.append(1 + Message.EOL);
					
					ResponseRFCMessage lookupResponse = null;
					
					ArrayList<PeerInfo> list = Server.map.get(msg.getRFCNumber());
					
					if (null != list) {
						Iterator<PeerInfo> itr = list.iterator();
						while (itr.hasNext()) {
							lookupResBuf.append("RFC" + Message.DELIMITER
									+ msg.getRFCNumber() + Message.DELIMITER
									+ itr.next().getRFCInfoString());
						}
						
//						lookupResBuf.append(Message.EOL);
						lookupResponse = new ResponseRFCMessage(Utility.RESPONSE_TYPE.OK);
						
					} else {	//send not found
						lookupResponse = new ResponseRFCMessage(Utility.RESPONSE_TYPE.NOT_FOUND);
					}
					
					lookupResponse.setResponseContent(lookupResBuf);
					lookupResponse.sendServerResponse(this.clientSocket);
					
					System.out.println("Sent response for LOOKUP...");
					break;
				case LIST:
					Set<Entry<Integer, ArrayList<PeerInfo>>> entries = Server.map
							.entrySet();
					Iterator<Entry<Integer, ArrayList<PeerInfo>>> entriesIterator = entries
							.iterator();
					StringBuffer listResBuf = new StringBuffer();
					while (entriesIterator.hasNext()) {

						Map.Entry<Integer, ArrayList<PeerInfo>> entry = entriesIterator
								.next();
						Iterator<PeerInfo> arrayListIterator = entry.getValue()
								.iterator();
						while (arrayListIterator.hasNext()) {
							listResBuf.append("RFC"
									+ Message.DELIMITER
									+ entry.getKey()
									+ Message.DELIMITER
									+ arrayListIterator.next()
											.getRFCInfoString());
						}
					}
					ResponseRFCMessage listResponse = new ResponseRFCMessage(
							Utility.RESPONSE_TYPE.OK);
					listResponse.setResponseContent(listResBuf);
					listResponse.sendServerResponse(this.clientSocket);

				default:
					return;
				}

			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}		
	}

	
	public static void main(String[] args) {
		int serverPort = Utility.serverPort;
		
		ServerSocket serverSocket = null;
		Socket clientSocket = null;
		
		try {
			serverSocket = new ServerSocket(serverPort);
		}
		catch(IOException e) {
			System.out.println("Error listening on port: " + serverPort);
			System.exit(-1);
		}	
		Utility.MSG_TYPE mt = Utility.MSG_TYPE.ADD; 
		while(true)	{
			try {
				System.out.println("Waiting for incoming connection...");
				clientSocket = serverSocket.accept();	
			} catch(IOException e) {
				System.out.println("error accepting the connection");
			}
			new Server(clientSocket).start();			
		}
	}
}
