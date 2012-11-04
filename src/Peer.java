import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

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
			
			Message msg = Utility.createMessage(Utility.MSG_TYPE.ADD, "P2P-CI/1.0");
			
			msg.setPort(serverSocket.getLocalPort());
			msg.setHost(serverSocket.getInetAddress().getHostName());
			msg.setTitle("RFC");
			msg.setData("123");
			
			msg.send(connectSocket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
