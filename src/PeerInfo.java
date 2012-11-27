
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((RFCTitle == null) ? 0 : RFCTitle.hashCode());
		result = prime * result
				+ ((hostname == null) ? 0 : hostname.hashCode());
		result = prime * result + port;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PeerInfo other = (PeerInfo) obj;
		if (RFCTitle == null) {
			if (other.RFCTitle != null)
				return false;
		} else if (!RFCTitle.equals(other.RFCTitle))
			return false;
		if (hostname == null) {
			if (other.hostname != null)
				return false;
		} else if (!hostname.equals(other.hostname))
			return false;
		if (port != other.port)
			return false;
		return true;
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
