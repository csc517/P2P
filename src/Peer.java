import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;


public class Peer extends Thread {

	ServerSocket serverSocket = null;
	
	static Object waitObject = new Object();

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
					synchronized (Peer.waitObject) {
						Peer.waitObject.notify();
					}

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
				
				File file = new File(it.next());
				BufferedReader fileReader = new BufferedReader(new FileReader(file));
				
				String firstLine = fileReader.readLine();
				String[] firstLineArr = firstLine.split(" ");
				
				int rfcNumber = Integer.parseInt(firstLineArr[1]);
				String rfcTitle = firstLineArr[2];
				
				Message msg = Utility.createMessage(Utility.MSG_TYPE.ADD);
				msg.setPort(serverSocket.getLocalPort());
				msg.setHost(serverSocket.getInetAddress().getHostName());
				msg.setRFCNumber(rfcNumber);
				msg.setTitle(rfcTitle);
				msg.send(connectSocket);
				
				synchronized (Peer.waitObject) {
					//wait for the response from the server
					Peer.waitObject.wait();
				}

			}
			
			Console console = System.console();
			while(true) {
				String consoleLine = console.readLine();
				String [] consoleLinesArr = consoleLine.split(" ");
				
				if(consoleLinesArr[0].equals("GET")) {
					
					//GET RFC <RFC_NUMBER>
					int rfcNumber = Integer.parseInt(consoleLinesArr[2]);
					
					Message msg = Utility.createMessage(Utility.MSG_TYPE.GET);
					msg.setHost("");
					msg.setOS(System.getProperty("os.name") + " " +
							System.getProperty("os.version") + " " +
							System.getProperty("os.arch"));
					msg.setRFCNumber(rfcNumber);
					msg.send(connectSocket);
				
				} else if (consoleLinesArr[0].equals("ADD")) {
				
					//ADD RFC <RFC_NUMBER> <title>
					int rfcNumber = Integer.parseInt(consoleLinesArr[2]);
					String rfcTitle = consoleLinesArr[3];
					
					Message msg = Utility.createMessage(Utility.MSG_TYPE.ADD);
					msg.setPort(serverSocket.getLocalPort());
					msg.setHost(serverSocket.getInetAddress().getHostName());
					msg.setRFCNumber(rfcNumber);
					msg.setTitle(rfcTitle);
					msg.send(connectSocket);
					
					synchronized (Peer.waitObject) {
						//wait for the response from the server
						Peer.waitObject.wait();
					}
				
				} else if(consoleLinesArr[0].equalsIgnoreCase("LIST")) {
				
					//LIST RFC
					Message msg = Utility.createMessage(Utility.MSG_TYPE.LIST);
					msg = Utility.createMessage(Utility.MSG_TYPE.LIST);
					msg.setPort(serverSocket.getLocalPort());
					msg.setHost(serverSocket.getInetAddress().getHostName());
					msg.send(connectSocket);
					
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
