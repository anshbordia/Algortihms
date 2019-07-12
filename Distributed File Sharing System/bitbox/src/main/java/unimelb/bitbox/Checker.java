package unimelb.bitbox;

import unimelb.bitbox.util.Configuration;
import unimelb.bitbox.util.Document;
import unimelb.bitbox.util.FileSystemManager;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

//Class that handles requests and sends responses wherever necessary
public class Checker {
	FileSystemManager fs = null;
	Messages m = new Messages();
	Document d = new Document();
	Document d2 = new Document();
	String s;
	String secretKey = "abcdefghijklmnopqrstuvwxyz";
	HashMap<String, Long> hostConnections = new HashMap<>();

	public Checker(FileSystemManager fileSystemManager){
		this.fs = fileSystemManager;
	}

	public Document check_command(String a, FileSystemManager fsm, HashMap hostConn) {
		this.d = Document.parse(a);
		this.fs = fsm;
		this.hostConnections = hostConn;
		d2 = m.invalid_protocol();
		s = d.getString("command");

		if (s==null)
		{
			d2 = m.invalid_protocol();
		}
		else if (s.equals("DIRECTORY_DELETE_REQUEST")) {
			d2 = deletedirectory(d);
		}
		else if(s.equals("DIRECTORY_CREATE_REQUEST")) {
			d2 = createdirectory(d);
		}
		else if(s.equals("FILE_DELETE_REQUEST")) {
			d2 = deletefile(d);
		}
		else if(s.equals("HANDSHAKE_REQUEST")) {
			d2 = handshake_success();
		}
		else if(s.equals("FILE_MODIFY_REQUEST")) {
			d2 = filemodifyreq(d);
		}
		else if(s.equals("FILE_CREATE_REQUEST")) {
			d2 = createfile(d);
		}
		else if(s.equals("DIRECTORY_DELETE_RESPONSE")) {
			return null;
		}
		else if(s.equals("DIRECTORY_CREATE_RESPONSE")) {
			return null;
		}
		else if(s.equals("FILE_DELETE_RESPONSE")) {
			return null;
		}
		else if(s.equals("FILE_MODIFY_RESPONSE")) {
			return null;
		}
		else if(s.equals("FILE_CREATE_RESPONSE")) {
			return null;
		}
		else if(s.equals("FILE_BYTES_REQUEST")) {
			d2 = filebytesreq(d);
		}
		else if(s.equals("FILE_BYTES_RESPONSE")) {
			d2 = filebytesresponse(d);
		}
		else if(s.equals("HANDSHAKE_RESPONSE")) {
			return null;
		}
		else if(s.equals("INVALID_PROTOCOL")) {
			return null;
		}
		else if(s.equals("AUTH_REQUEST")) {
			d2 = authreq(d);
		}
		else if(s.equals("LIST_PEERS_REQUEST")) {
			d2 = listpeers(d);
		}
		else if(s.equals("CONNECT_PEER_REQUEST")) {
			d2 = connectreq(d);
		}
		/*else if(s.equals("DISCONNECT_PEER_REQUEST")) {
			d2 = disconnectreq(d);
		}*/
		else {
			d2 = m.invalid_protocol();
		}

		return d2;
	}

	public Document handshake_success() {
		return m.handshake_response();
	}

	public Document deletedirectory(Document d) {
		Boolean outcome= fs.deleteDirectory(d.getString("pathName"));
		if(outcome) {
			return(m.dir_response("DIRECTORY_DELETE_RESPONSE", d.getString("pathName")
					, "Directory Deleted", outcome));
		}
		else {
			return(m.dir_response("DIRECTORY_DELETE_RESPONSE", d.getString("pathName")
					, "Unable to procees directory delete", outcome));
		}
	}

	public Document createdirectory(Document d) {
		if (fs.isSafePathName(d.getString("pathName")) && !(fs.dirNameExists(d.getString("pathName")))) {
			Boolean outcome= fs.makeDirectory(d.getString("pathName"));
			if(outcome) {
				return(m.dir_response("DIRECTORY_CREATE_RESPONSE", d.getString("pathName")
						, "Directory Created", outcome));
			}
			else {
				return(m.dir_response("DIRECTORY_CREATE_RESPONSE", d.getString("pathName")
						, "Unable to procees directory create", outcome));
			}
		}
		else {
			return(m.dir_response("DIRECTORY_CREATE_RESPONSE", d.getString("pathName")
					, "Unsafe path name given/Or Directory Exists already", false));
		}

	}
	public Document deletefile(Document d) {
		if (fs.isSafePathName(d.getString("pathName")) && (fs.fileNameExists(d.getString("pathName")))) {

			Boolean outcome= fs.deleteFile(
					d.getString("pathName"), ((Document)d.get("fileDescriptor")).getLong("lastModified"), ((Document)d.get("fileDescriptor")).getString("md5"));
			if(outcome) {
				return(m.file_response("FILE_DELETE_RESPONSE",
						((Document)d.get("fileDescriptor")).getString("md5"),
						((Document)d.get("fileDescriptor")).getLong("lastModified"),
						((Document)d.get("fileDescriptor")).getLong("fileSize"),
						d.getString("pathName"), "File Deleted", outcome));
			}
			else {
				return(m.file_response("FILE_DELETE_RESPONSE",
						((Document)d.get("fileDescriptor")).getString("md5"),
						((Document)d.get("fileDescriptor")).getLong("lastModified"),
						((Document)d.get("fileDescriptor")).getLong("fileSize"),
						d.getString("pathName"), "Unable to process file delete", outcome));
			}
		}


		else {
			return(m.file_response("FILE_DELETE_RESPONSE",
					((Document)d.get("fileDescriptor")).getString("md5"),
					((Document)d.get("fileDescriptor")).getLong("lastModified"),
					((Document)d.get("fileDescriptor")).getLong("fileSize"),
					d.getString("pathName"), "Unsafe path name/Or file not present", false));
		}

	}

	public Document createfile(Document d) {
		Boolean outcome = false;
		Boolean success = false;
		if (fs.isSafePathName(d.getString("pathName")) && !(fs.fileNameExists(d.getString("pathName")))) {
			try {
				outcome= fs.createFileLoader(d.getString("pathName"), ((Document)d.get("fileDescriptor")).getString("md5"),
						((Document)d.get("fileDescriptor")).getLong("fileSize"), ((Document)d.get("fileDescriptor")).getLong("lastModified"));
				success = fs.checkShortcut(d.getString("pathName"));

				if(success) {
					return(m.file_response("FILE_CREATE_RESPONSE",
							((Document)d.get("fileDescriptor")).getString("md5"),
							((Document)d.get("fileDescriptor")).getLong("lastModified"),
							((Document)d.get("fileDescriptor")).getLong("fileSize"),
							d.getString("pathName"),
							"Local copy of a a file with matching contents was present and that was copied",
							false));
				}
			} catch(NoSuchAlgorithmException | IOException e) {}
			if(outcome) {
				return(m.file_response("FILE_CREATE_RESPONSE",
						((Document)d.get("fileDescriptor")).getString("md5"),
						((Document)d.get("fileDescriptor")).getLong("lastModified"),
						((Document)d.get("fileDescriptor")).getLong("fileSize"),
						d.getString("pathName"), "File Loader ready", true));
			}
			else {
				return(m.file_response("FILE_CREATE_RESPONSE",
						((Document)d.get("fileDescriptor")).getString("md5"),
						((Document)d.get("fileDescriptor")).getLong("lastModified"),
						((Document)d.get("fileDescriptor")).getLong("fileSize"),
						d.getString("pathName"), "Unable to start file loader", false));
			}
		}

		else if(fs.isSafePathName(d.getString("pathName")) && (fs.fileNameExists(d.getString("pathName"),
				((Document)d.get("fileDescriptor")).getString("md5") ))) {
			return(m.file_response("FILE_CREATE_RESPONSE",
					((Document)d.get("fileDescriptor")).getString("md5"),
					((Document)d.get("fileDescriptor")).getLong("lastModified"),
					((Document)d.get("fileDescriptor")).getLong("fileSize"),
					d.getString("pathName"), "File with matching contents already exists", false));

		}

		else if(fs.isSafePathName(d.getString("pathName")) && (fs.fileNameExists(d.getString("pathName")))) {
			return(m.file_response("FILE_CREATE_RESPONSE",
					((Document)d.get("fileDescriptor")).getString("md5"),
					((Document)d.get("fileDescriptor")).getLong("lastModified"),
					((Document)d.get("fileDescriptor")).getLong("fileSize"),
					d.getString("pathName"), "File name already exists please send a modify request", false));

		}
		else {
			return(m.file_response("FILE_CREATE_RESPONSE",
					((Document)d.get("fileDescriptor")).getString("md5"),
					((Document)d.get("fileDescriptor")).getLong("lastModified"),
					((Document)d.get("fileDescriptor")).getLong("fileSize"),
					d.getString("pathName"), "Unsafe path name", false));
		}


	}

	public Document filebytesreq(Document d) {
		ByteBuffer bytesRead = null;
		String content = "";
		Long rem_bytes = ((Document)d.get("fileDescriptor")).getLong("fileSize") - d.getLong("position");
		if(rem_bytes >=  d.getLong("length")) {
			rem_bytes = d.getLong("length");
		}
		try {
			bytesRead = fs.readFile(((Document)d.get("fileDescriptor")).getString("md5"),
					d.getLong("position"), d.getLong("length"));
			content = Base64.getEncoder().encodeToString(bytesRead.array());

		} catch (NoSuchAlgorithmException | IOException e) {}
		if(bytesRead != null) {
			return(m.byte_response(
					((Document)d.get("fileDescriptor")).getString("md5"),
					((Document)d.get("fileDescriptor")).getLong("lastModified"),
					((Document)d.get("fileDescriptor")).getLong("fileSize"),
					d.getString("pathName"), d.getLong("position"), rem_bytes,
					content, "File Bytes Read Successfully", true));

		} else {
			return(m.byte_response(((Document)d.get("fileDescriptor")).getString("md5"),
					((Document)d.get("fileDescriptor")).getLong("lastModified"),
					((Document)d.get("fileDescriptor")).getLong("fileSize"),
					d.getString("pathName"), d.getLong("position"), rem_bytes,
					content, "Unsuccessful read", false));
		}
	}

	public Document filebytesresponse(Document d) {
		Boolean outcome = null;
		Long rem_bytes = ((Document)d.get("fileDescriptor")).getLong("fileSize") - (d.getLong("position") + d.getLong("length"));
		ByteBuffer content = ByteBuffer.wrap(Base64.getDecoder().decode(d.getString("content")));
		try {
			outcome = fs.writeFile(d.getString("pathName"), content, d.getLong("position"));
		} catch (IOException e) {}
		if(outcome) {
			try {
				if(!fs.checkWriteComplete(d.getString("pathName")) && rem_bytes == 0) {
					return(m.byte_request(((Document)d.get("fileDescriptor")).getString("md5"),
							((Document)d.get("fileDescriptor")).getLong("lastModified"),
							((Document)d.get("fileDescriptor")).getLong("fileSize"),
							d.getString("pathName"), d.getLong("length"), rem_bytes));
				}
				else {
					return null;
				}
			} catch (NoSuchAlgorithmException | IOException e) {return null;}
		}
		else return null;
	}

	public Document filemodifyreq(Document d) {
		Boolean outcome = false;
		Boolean success = false;
		if(fs.isSafePathName(d.getString("pathName")) && (fs.fileNameExists(d.getString("pathName"), ((Document)d.get("fileDescriptor")).getString("md5") ))) {
			return(m.file_response("FILE_MODIFY_RESPONSE",
					((Document)d.get("fileDescriptor")).getString("md5"),
					((Document)d.get("fileDescriptor")).getLong("lastModified"),
					((Document)d.get("fileDescriptor")).getLong("fileSize"),
					d.getString("pathName"), "File with matching contents already exists", false));

		}

		else if (fs.isSafePathName(d.getString("pathName")) && (fs.fileNameExists(d.getString("pathName")))) {
			try {
				outcome= fs.modifyFileLoader(d.getString("pathName"), ((Document)d.get("fileDescriptor")).getString("md5"),
						((Document)d.get("fileDescriptor")).getLong("lastModified"));
				success = fs.checkShortcut(d.getString("pathName"));
				if(success) {
					return(m.file_response("FILE_MODIFY_RESPONSE",
							((Document)d.get("fileDescriptor")).getString("md5"),
							((Document)d.get("fileDescriptor")).getLong("lastModified"),
							((Document)d.get("fileDescriptor")).getLong("fileSize"),
							d.getString("pathName"), "Local copy was present to modify and it was used", false));

				}
			} catch(IOException e) {} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(outcome) {
				return(m.file_response("FILE_MODIFY_RESPONSE",
						((Document)d.get("fileDescriptor")).getString("md5"),
						((Document)d.get("fileDescriptor")).getLong("lastModified"),
						((Document)d.get("fileDescriptor")).getLong("fileSize"),
						d.getString("pathName"), "File Loader Ready", outcome));
			}
			else {
				return(m.file_response("FILE_MODIFY_RESPONSE",
						((Document)d.get("fileDescriptor")).getString("md5"),
						((Document)d.get("fileDescriptor")).getLong("lastModified"),
						((Document)d.get("fileDescriptor")).getLong("fileSize"),
						d.getString("pathName"), "Unable to create file modify loader", outcome));
			}
		}


		else if(fs.isSafePathName(d.getString("pathName")) && !(fs.fileNameExists(d.getString("pathName"), ((Document)d.get("fileDescriptor")).getString("md5") ))) {
			return(m.file_response("FILE_MODIFY_RESPONSE",
					((Document)d.get("fileDescriptor")).getString("md5"),
					((Document)d.get("fileDescriptor")).getLong("lastModified"),
					((Document)d.get("fileDescriptor")).getLong("fileSize"),
					d.getString("pathName"), "File does not exist please send a file create request", false));

		}

		else {
			return(m.file_response("FILE_MODIFY_RESPONSE",
					((Document)d.get("fileDescriptor")).getString("md5"),
					((Document)d.get("fileDescriptor")).getLong("lastModified"),
					((Document)d.get("fileDescriptor")).getLong("fileSize"),
					d.getString("pathName"), "Unsafe path name", false));
		}

	}

	public Document authreq(Document d) {
		String key_list = Configuration.getConfigurationValue("key_list");
		String id_list = Configuration.getConfigurationValue("identity_list");
		String[] keys = key_list.split(",");
		String[] ids = id_list.split(",");
		for(int i = 0; i < ids.length; i++) {
			if(ids[i].contains(d.getString("identity"))) {
				String aes;
				try {
					aes = Base64.getEncoder().encodeToString(RSAUtil.encrypt(secretKey, keys[i]));
					return m.auth_resp(aes, true);
				} catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException
						| NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return m.auth_resp(null, false);

	}
	public Document listpeers(Document d) {
		return m.list_peers_resp(hostConnections);
	}
	public Document connectreq(Document d) {
		return m.connect_peers_resp(d.getString("host"), (int) d.getLong("port"), true);
	}
	public Document authrequest(Document d) {
		return m.disconnect_peers_resp(d.getString("host"), (int) d.getLong("port"), true);
	}
}


