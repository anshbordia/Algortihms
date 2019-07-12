package unimelb.bitbox;

import unimelb.bitbox.util.Configuration;
import unimelb.bitbox.util.Document;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

public class Messages {

	Document handshake_request() {
		Document doc = new Document();
		Document doc1 = new Document();
		doc1.append("host", /*InetAddress.getLocalHost().getHostAddress().toString()*/ Configuration.getConfigurationValue("advertisedName"));
		doc1.append("port", Integer.valueOf(Configuration.getConfigurationValue("port")));
		doc.append("command", "HANDSHAKE_REQUEST");
		doc.append("hostPort", doc1);

		//catch(UnknownHostException e) {}
		return doc;
	}
	Document handshake_response() {
		Document doc = new Document();
		try {
			Document doc1 = new Document();
			doc1.append("host", InetAddress.getLocalHost().getHostAddress().toString());
			doc1.append("port", Integer.valueOf(Configuration.getConfigurationValue("port")));
			doc.append("command", "HANDSHAKE_RESPONSE");
			doc.append("hostPort", doc1);
		}
		catch(UnknownHostException e) {}
		return doc;
	}
	Document invalid_protocol() {
		Document doc = new Document();
		doc.append("command", "INVALID_PROTOCOL");
		doc.append("message", "command field must be a string");
		return doc;
	}

	Document connection_refused(HashMap hostconnections) {
		Document doc = new Document();
		doc.append("command", "CONNECTION_REFUSED");
		doc.append("message", "connection limit reached");
		ArrayList <Document> docs = new ArrayList<>();

		hostconnections.forEach((k, v) -> {
			Document doc2 = new Document();
			doc2.append("host", k.toString());
			doc2.append("port", v.toString());
			docs.add(doc2);

		});
		doc.append("peers", docs);
//		doc.append("peers", hostconnections.toString());
		return doc;
	}

	// Covers Delete, Modify and Create requests.
	Document file_request(String command, String md5, Long timestamp, Long filesize, String pathname) {
		Document doc = new Document();
		Document doc1 = new Document();
		doc1.append("md5", md5);
		doc1.append("lastModified", timestamp);
		doc1.append("fileSize", filesize);
		doc.append("command", command);
		doc.append("fileDescriptor", doc1);
		doc.append("pathName", pathname);
		return doc;
	}
	// Covers Delete, Modify and Create responses.
	Document file_response(String command, String md5, Long timestamp,
						   Long filesize, String pathname, String message, Boolean value) {
		Document doc = new Document();
		Document doc1 = new Document();
		doc1.append("md5", md5);
		doc1.append("lastModified", timestamp);
		doc1.append("fileSize", filesize);
		doc.append("command", command);
		doc.append("fileDescriptor", doc1);
		doc.append("pathName", pathname);
		doc.append("message", message);
		doc.append("status", value);
		return doc;
	}
	//Directory Create and Delete Requests
	Document dir_request(String command, String pathname) {
		Document doc = new Document();
		doc.append("command", command);
		doc.append("pathName", pathname);
		return doc;
	}//Directory Create and Delete Response
	Document dir_response(String command, String pathname, String message, Boolean value) {
		Document doc = new Document();
		doc.append("command", command);
		doc.append("pathName", pathname);
		doc.append("message", message);
		doc.append("status", value);
		return doc;
	}
	Document byte_request(String md5, Long timestamp,
						  Long filesize, String pathname, Long position, Long length) {
		Document doc = new Document();
		Document doc1 = new Document();
		doc1.append("md5", md5);
		doc1.append("lastModified", timestamp);
		doc1.append("fileSize", filesize);
		doc.append("command", "FILE_BYTES_REQUEST");
		doc.append("fileDescriptor", doc1);
		doc.append("pathName", pathname);
		doc.append("position", position);
		doc.append("length", length);
		return doc;
	}
	Document byte_response(String md5, Long timestamp,
						   Long filesize, String pathname, Long position, Long length,
						   String content, String message, Boolean value) {
		Document doc = new Document();
		Document doc1 = new Document();
		doc1.append("md5", md5);
		doc1.append("lastModified", timestamp);
		doc1.append("fileSize", filesize);
		doc.append("command", "FILE_BYTES_RESPONSE");
		doc.append("fileDescriptor", doc1);
		doc.append("pathName", pathname);
		doc.append("position", position);
		doc.append("length", length);
		doc.append("content", content);
		doc.append("message", message);
		doc.append("status", value);
		return doc;
	}

	Document auth_req(String identity) {
		Document doc = new Document();
		doc.append("command", "AUTH_REQUEST");
		doc.append("identity", identity);
		return doc;
	}

	Document auth_resp(String aes128, Boolean status) {
		Document doc = new Document();
		doc.append("command", "AUTH_RESPONSE");
		doc.append("status", status);
		if(status) {
			doc.append("aes128", aes128);
			doc.append("message", "public key found");
		}
		else {
			doc.append("message", "public key not found");
		}
		return doc;
	}

	Document list_peers_req() {
		Document doc = new Document();
		doc.append("command", "LIST_PEERS_REQUEST");
		return doc;
	}

	Document list_peers_resp(HashMap hostconnections) {
		Document doc = new Document();
		doc.append("command", "LIST_PEERS_RESPONSE");
		ArrayList <Document> docs = new ArrayList<>();
		hostconnections.forEach((k, v) -> {
			Document doc2 = new Document();
			doc2.append("host", k.toString());
			doc2.append("port", v.toString());
			docs.add(doc2);

		});
		doc.append("peers", docs);
		return doc;
	}

	Document connect_peers_req(String host, Integer port) {
		Document doc = new Document();
		doc.append("command", "CONNECT_PEER_REQUEST");
		doc.append("host", host);
		doc.append("port", port);
		return doc;
	}

	Document connect_peers_resp(String host, Integer port, Boolean status) {
		Document doc = new Document();
		doc.append("command", "CONNECT_PEER_RESPONSE");
		doc.append("host", host);
		doc.append("port", port);
		doc.append("status", status);
		if(status) {
			doc.append("message", "connected to peer");
		}
		else {
			doc.append("message", "cannot connect to peer");
		}
		return doc;
	}

	Document disconnect_peers_req(String host, Integer port) {
		Document doc = new Document();
		doc.append("command", "DISCONNECT_PEER_REQUEST");
		doc.append("host", host);
		doc.append("port", port);
		return doc;
	}

	Document disconnect_peers_resp(String host, Integer port, Boolean status) {
		Document doc = new Document();
		doc.append("command", "DISCONNECT_PEER_RESPONSE");
		doc.append("host", host);
		doc.append("port", port);
		doc.append("status", status);
		if(status) {
			doc.append("message", "disconnected from peer");
		}
		else {
			doc.append("message", "cannot disconnect from peer");
		}
		return doc;
	}
	Document payload(String s) {
		Document doc = new Document();
		doc.append("payload", s);
		return doc;
	}


}
