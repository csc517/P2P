import java.net.*;
import java.util.HashMap;
import java.io.*;

public class Server extends Thread {
	
	static HashMap map;
	
	Socket clientSocket;

	public Server(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}
	
	public void run() {
		
	}

	/**
	 * @param args
	 */
	
/*	public class ConnectionHandler extends Thread {
		public ConnectionHandler(Socket socket) {
			// TODO Auto-generated constructor stub
			Socket clientSocket = socket;
		}
		public void run() {
			
		}
	}*/
	
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
