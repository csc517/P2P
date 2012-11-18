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
	static String workingDir;

	public Peer(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

	public void run() {
		Socket incomingSocket = null;
		while(true) {
			try {
				incomingSocket = serverSocket.accept();
				System.out.println("Parsing msg..");
				Message msg = Utility.parseMessage(incomingSocket);
				msg.readMessage();

				switch(msg.getType()) {
				case GET:
					//send peer response
					//send the file to requesting host
					ResponseRFCMessage response = new ResponseRFCMessage(Utility.RESPONSE_TYPE.OK);
					
					StringBuffer buf = new StringBuffer();
					
					buf.append(0 + Message.EOL);
					
					response.setResponseContent(buf);
					response.sendServerResponse(incomingSocket);
					System.out.println("Sent response for GET...");
					
					System.out.println("Sending file...");
					Utility.send_file(incomingSocket, msg.getRFCNumber());
					
					incomingSocket.close();
					System.out.println("Sending file...DONE");
					break;
				case ADD:
				case LOOKUP:
				case LIST:
					break;
				case RESPONSE:	//response from server
					System.out.println("Response from server...");
					synchronized (Peer.waitObject) {
						Peer.waitObject.notify();
					}

				}
				System.out.println(msg);
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}

	}

	@SuppressWarnings("finally")
	public static Message acceptResponse(Socket incomingSocket) {
		Message msg = null;
		try {
			System.out.println("acceptResponseFromServer(): Parsing msg..");
			msg = Utility.parseMessage(incomingSocket);
			msg.readMessage();

			switch(msg.getType()) {
			case GET:
			case ADD:
			case LOOKUP:
			case LIST:
				break;
			case RESPONSE:	//response from server
				System.out.println("Response from server...");
			}
			System.out.println(msg);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			return msg;
		}
	}
	
	static ServerSocket getServerSocket() {
		int i = 12345;
		int tries = 0;
		ServerSocket serverSocket = null;
		while(tries != 100) {
			try {
				serverSocket = new ServerSocket(i);
			} catch (IOException e) {
				++i;
				++tries;
				continue;
			}
			break;
		}
		return serverSocket;
	}

	public static void main(String[] args) {
		try {
			Peer.workingDir = System.getProperty("user.dir");
			
			if(args.length >= 1) {
				if(args[0].charAt(1) == ':') {
					Peer.workingDir = args[0];
				} else {
					Peer.workingDir = Peer.workingDir + File.separator + args[0];
				}
			}
			
			File dirFile = new File(Peer.workingDir);
			if(!dirFile.exists()) {
				dirFile.mkdirs();
			} else {
				if(!dirFile.isDirectory()) {
					System.err.println(dirFile.getAbsolutePath() + " is not a directory");
					System.exit(-1);
				}
			}
			
			int serverPort = Utility.serverPort;
			String serverHost = "localhost";
			Socket connectSocket = new Socket(serverHost, serverPort);
			ServerSocket serverSocket = getServerSocket();
			if(serverSocket == null) {
				System.err.println("Could not open server socket");
				System.exit(-1);
			}
			new Peer(serverSocket).start();

			//after connecting to the server loop through the local files
			//and send add for each of them
			ArrayList<String> files = Utility.textFiles(null);
			Iterator<String> it = files.iterator();

			while(it.hasNext()) {

				File file = new File(it.next());
				BufferedReader fileReader = new BufferedReader(new FileReader(file));

				String firstLine = fileReader.readLine();
				String[] firstLineArr = firstLine.split(" ");

				int rfcNumber = Integer.parseInt(firstLineArr[1]);
				String rfcTitle = firstLineArr[2];

				Message msg = Utility.createMessage(Utility.MSG_TYPE.ADD);
				System.out.println("Server socket port: "+ serverSocket.getLocalPort());
				System.out.println("Server socket host: "+ serverSocket.getInetAddress().getHostName());
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
				System.out.println("Waiting for input...");
				String consoleLine = console.readLine();
				String [] consoleLinesArr = consoleLine.split(" ");

				if(consoleLinesArr[0].equals("LOOKUP")) {

					//LOOKUP RFC <RFC_NUMBER>
					int rfcNumber = Integer.parseInt(consoleLinesArr[2]);

					Message msg = Utility.createMessage(Utility.MSG_TYPE.LOOKUP);
					msg.setHost("");
					msg.setOS(System.getProperty("os.name") + " " +
							System.getProperty("os.version") + " " +
							System.getProperty("os.arch"));
					msg.setRFCNumber(rfcNumber);
					msg.send(connectSocket);
					
					acceptResponse(connectSocket);

				} else if(consoleLinesArr[0].equals("GET")) {

					//GET RFC <RFC_NUMBER> <host> <port>
					int rfcNumber = Integer.parseInt(consoleLinesArr[2]);
					String clientHost = consoleLinesArr[3];
					int clientPort = Integer.parseInt(consoleLinesArr[4]);
					
					Socket clientConnectSocket = new Socket(clientHost, clientPort);

					Message msg = Utility.createMessage(Utility.MSG_TYPE.GET);
					msg.setHost("");
					msg.setRFCNumber(rfcNumber);
					msg.send(clientConnectSocket);
					
					Message resMsg = acceptResponse(clientConnectSocket);
					
					String title = Utility.recv_file(clientConnectSocket, resMsg.getBufferedReader());
					
					clientConnectSocket.close();
					
					msg = Utility.createMessage(Utility.MSG_TYPE.ADD);
					msg.setPort(serverSocket.getLocalPort());
					msg.setHost(serverSocket.getInetAddress().getHostName());
					msg.setRFCNumber(rfcNumber);
					msg.setTitle(title);
					msg.send(connectSocket);

					acceptResponse(connectSocket);

				} else if (consoleLinesArr[0].equals("ADD")) {
					//[0] [1]    [2]        [3]
					//ADD RFC <RFC_NUMBER> <title>
					int rfcNumber = Integer.parseInt(consoleLinesArr[2]);
					String rfcTitle = consoleLinesArr[3];

					Message msg = Utility.createMessage(Utility.MSG_TYPE.ADD);
					msg.setPort(serverSocket.getLocalPort());
					msg.setHost(serverSocket.getInetAddress().getHostName());
					msg.setRFCNumber(rfcNumber);
					msg.setTitle(rfcTitle);
					msg.send(connectSocket);

					acceptResponse(connectSocket);

				} else if(consoleLinesArr[0].equalsIgnoreCase("LIST")) {

					//LIST RFC
					Message msg = Utility.createMessage(Utility.MSG_TYPE.LIST);
					msg = Utility.createMessage(Utility.MSG_TYPE.LIST);
					msg.setPort(serverSocket.getLocalPort());
					msg.setHost(serverSocket.getInetAddress().getHostName());
					msg.send(connectSocket);

				} else {
					System.out.println("Invalid command");
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
