package unimelb.bitbox;

import unimelb.bitbox.util.Configuration;
import java.util.logging.Logger;

public class Peer extends ServerMain
{
	private static Logger log = Logger.getLogger(Peer.class.getName());

	public static void main( String[] args ) throws NumberFormatException {

		System.setProperty("java.util.logging.SimpleFormatter.format",
				"[%1$tc] %2$s %4$s: %5$s%n");
		log.info("BitBox Peer starting...");
		Configuration.getConfiguration();
		String mode =  Configuration.getConfigurationValue("mode");
		System.out.println("Mode: "+mode);

		if (mode.equals("udp") || mode.equals("UDP")){
   			UDPPeer.main(null);
		}
		else if (mode.equals("tcp") || mode.equals("TCP")){
			TCPPeer.main(null);
		}
		else{
			System.out.println("Enter Correct Mode.. Exiting");
		}
	}
}

//Handshake Result:{"peers":[{"port":8115,"host":"10.12.78.222"},{"port":56053,"host":"220.240.154.137"},{"port":56249,"host":"220.240.154.137"},{"port":56250,"host":"220.240.154.137"}],"message":"connection limit reached","command":"CONNECTION_REFUSED"}