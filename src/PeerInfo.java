
public class PeerInfo {

	int port;
	String hostname;
	String RFCTitle;
	PeerInfo next;
	
	public PeerInfo(int port, String hostname, String rFCTitle) {
		this.port = port;
		this.hostname = hostname;
		RFCTitle = rFCTitle;
	}

	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public String getRFCTitle() {
		return RFCTitle;
	}
	public void setRFCTitle(String rFCTitle) {
		RFCTitle = rFCTitle;
	}
	
	public StringBuffer getRFCInfoString() {
		StringBuffer buf = new StringBuffer();
		buf.append(this.getRFCTitle() +
				   Message.DELIMITER +
				   this.getHostname() +
				   Message.DELIMITER + 
				   this.getPort() +
				   Message.EOL);
		return buf;
	}

	
}
