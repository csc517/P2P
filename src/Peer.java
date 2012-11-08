import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;


public class Peer extends Thread {

	ServerSocket serverSocket = null;

	public Peer(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

	public void run() {
		Socket incomingSocket;
		while(true) {
			try {
				incomingSocket = serverSocket.accept();
				Message msg = Utility.parseMessage(incomingSocket);

				switch(msg.getType()) {
				case GET:
					//send peer response
					//send the file to requesting host
					Utility.send_file(incomingSocket, msg.getTitle());
					break;
				case ADD:
				case LOOKUP:
				case LIST:
					break;
				default:	//response from server

				}
				System.out.println(msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public static void main(String[] args) {
		try {
			ServerSocket serverSocket = new ServerSocket();
			new Peer(serverSocket).start();

			int serverPort = 7734;
			String serverHost = "localhost";

			Socket connectSocket = new Socket(serverHost, serverPort);
			//after connecting to the server loop through the local files
			//and send add for each of them
			ArrayList<String> files = Utility.textFiles();
			Iterator<String> it = files.iterator();
			while(it.hasNext()) {
				Message msg = Utility.createMessage(Utility.MSG_TYPE.ADD);
				msg.setPort(serverSocket.getLocalPort());
				msg.setHost(serverSocket.getInetAddress().getHostName());
				msg.setRFCnum("RFC" + it.next());
				msg.setTitle("RFC blah blah");
				msg.send(connectSocket);

				//receive response only if response is OK continue

			}

			Message msg = Utility.createMessage(Utility.MSG_TYPE.GET);
			msg.setHost("");
			msg.setOS(System.getProperty("os.name") + " " +
					System.getProperty("os.version") + " " +
					System.getProperty("os.arch"));
			msg.send(connectSocket);

			msg = Utility.createMessage(Utility.MSG_TYPE.LIST);
			msg.setPort(serverSocket.getLocalPort());
			msg.setHost(serverSocket.getInetAddress().getHostName());
			msg.send(connectSocket);			



		}
		catch (Exception e) {
			// TODO: handle exception
		}
	}
}
