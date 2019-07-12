package unimelb.bitbox;

import org.json.simple.JSONObject;
import unimelb.bitbox.util.Configuration;
import unimelb.bitbox.util.Document;
import unimelb.bitbox.util.FileSystemManager;
import unimelb.bitbox.util.FileSystemManager.FileSystemEvent;
import unimelb.bitbox.util.FileSystemObserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Logger;


public class ServerMain implements FileSystemObserver {
	private static Logger log = Logger.getLogger(ServerMain.class.getName());
	static HashMap<String, Long> hostConnections = new HashMap<>();
	static FileSystemManager fileSystemManager;
	private Socket s = null;
	private PrintWriter out = null;
	private BufferedReader in = null;
	private DatagramSocket ds = null;
	private InetAddress address = null;
	private int port;
	private int packet_sent = 0;
	private int packet_recieved = 0;
	private Boolean packetsync = true;

	public ServerMain(Socket s, BufferedReader in, PrintWriter out) throws NumberFormatException, IOException, NoSuchAlgorithmException {
		fileSystemManager=new FileSystemManager(Configuration.getConfigurationValue("path"),this);
		this.s = s;
		this.out = out;
		this.in = in;
		ArrayList<FileSystemEvent> syncEvents = new ArrayList<FileSystemEvent>();
		syncEvents = fileSystemManager.generateSyncEvents();


		for(int i = 0; i < syncEvents.size(); i++) {
			processFileSystemEvent(syncEvents.get(i));
		}

		try {
			while (true) {
				Document response = null;
				String mg = in.readLine();
				if (!(mg == null)){
					Checker check = new Checker(fileSystemManager);
					System.out.println("Message Received: " + mg);
					response = check.check_command(mg, fileSystemManager,null);
				}

				if (!(response == null))
				{
					System.out.println("We sent: " + response.toJson());
					out.println(response.toJson());
					try {
						if (response.getString("command").equals("FILE_CREATE_RESPONSE") && response.getBoolean("status")) {
							Messages m2 = new Messages();
							Document d2 = m2.byte_request(((Document) response.get("fileDescriptor")).getString("md5"),
									((Document) response.get("fileDescriptor")).getLong("lastModified"),
									((Document) response.get("fileDescriptor")).getLong("fileSize"),
									response.getString("pathName"), 0L, 6L);
							System.out.println("We sent message 2:" + d2.toJson());
							out.println(d2.toJson());
						}
						else if (response.getString("command").equals("FILE_MODIFY_RESPONSE") && response.getBoolean("status")){
							Messages m2 = new Messages();
							Document d2 = m2.byte_request(((Document) response.get("fileDescriptor")).getString("md5"),
									((Document) response.get("fileDescriptor")).getLong("lastModified"),
									((Document) response.get("fileDescriptor")).getLong("fileSize"),
									response.getString("pathName"), 0L,
									((Document) response.get("fileDescriptor")).getLong("fileSize"));
							System.out.println("We sent message 2:" + d2.toJson());
							out.println(d2.toJson());
						}
					} catch (Exception e){e.printStackTrace();}
				}

			}
//			System.out.println("Disconnected..");
		}
		catch (Exception e){
			e.printStackTrace();
			s.close();
			System.out.println("Error in Connection..");
		}

//		Checker check = new Checker(fileSystemManager);
//		Document response = check.check_command(msg, fileSystemManager);
		//System.out.println(response.toJson());

	}

	public ServerMain() {

	}

	public ServerMain(DatagramSocket s, String hostname, int port) throws IOException, NoSuchAlgorithmException {
		InetAddress server_address = InetAddress.getByName(hostname);
		this.ds = s;
		this.address = server_address;
		this.port = port;
		fileSystemManager=new FileSystemManager(Configuration.getConfigurationValue("path"),this);
		DatagramPacket DpSend;
		DatagramPacket DpReceive;
		byte[] sendData;
		byte[] receiveData = new byte[65535];
//		System.out.println("Peer trying to connect to ..."+ hostname +":"+ port);
//		Messages m = new Messages();
//		Document hs = m.handshake_request();
//		sendData = hs.toJson().getBytes();
//		DpSend = new DatagramPacket(sendData, sendData.length, server_address, port);
//		s.send(DpSend);
//		this.packet_sent+=1;
//		System.out.println("Handshake Sent:"+ hs.toJson());
//
//		DpReceive = new DatagramPacket(receiveData, receiveData.length);
//		s.receive(DpReceive);
//		this.packet_recieved+=1;
//		System.out.println("Handshake Result:" + data(receiveData));
//
//		Document d;
//		d = Document.parse(data(receiveData).toString());
//		if (d.getString("command").equals("CONNECTION_REFUSED")){
//			hostConnections.remove(hostname);
//			ArrayList A =  d.getArray("peers");
//			JSONObject a = (JSONObject) A.get(0);
//			String hostname_new = (String) a.get("host");
//			Long portL = (Long) a.get("port");
//			int port_new = portL.intValue();
//			UDPPeer client_side = new UDPPeer(hostname_new, port_new,
//					Integer.parseInt(Configuration.getConfigurationValue("port")));
//		}
//		hostConnections.put(hostname, (long) port);

		try {
			while (true) {
				Document response = null;
				receiveData = new byte[65535];
				DpReceive = new DatagramPacket(receiveData, receiveData.length);
				s.receive(DpReceive);
				this.packet_recieved+=1;
				String mg = data(receiveData).toString();

				if (!(mg.equals(""))){
					Checker check = new Checker(fileSystemManager);
					System.out.println("Message Received: " + mg);
					response = check.check_command(mg, fileSystemManager, null);
				}

				if (!(response == null))
				{
					System.out.println("We sent: " + response.toJson());
					sendData = response.toJson().getBytes();
					DpSend = new DatagramPacket(sendData, sendData.length, server_address, port);
					s.send(DpSend);
					this.packet_sent+=1;
					try {
						if (response.getString("command").equals("FILE_CREATE_RESPONSE") && response.getBoolean("status")) {
							Messages m2 = new Messages();
							Document d2 = m2.byte_request(((Document) response.get("fileDescriptor")).getString("md5"),
									((Document) response.get("fileDescriptor")).getLong("lastModified"),
									((Document) response.get("fileDescriptor")).getLong("fileSize"),
									response.getString("pathName"), 0L, 6L);
							System.out.println("We sent message 2:" + d2.toJson());
							sendData = d2.toJson().getBytes();
							DpSend = new DatagramPacket(sendData, sendData.length, server_address, port);
							s.send(DpSend);
							this.packet_sent+=1;
						}
						else if (response.getString("command").equals("FILE_MODIFY_RESPONSE") && response.getBoolean("status")){
							Messages m2 = new Messages();
							Document d2 = m2.byte_request(((Document) response.get("fileDescriptor")).getString("md5"),
									((Document) response.get("fileDescriptor")).getLong("lastModified"),
									((Document) response.get("fileDescriptor")).getLong("fileSize"),
									response.getString("pathName"), 0L,
									((Document) response.get("fileDescriptor")).getLong("fileSize"));
							System.out.println("We sent message 2:" + d2.toJson());
							sendData = d2.toJson().getBytes();
							DpSend = new DatagramPacket(sendData, sendData.length, server_address, port);
							s.send(DpSend);
							this.packet_sent+=1;
						}
					} catch (Exception e){e.printStackTrace();}
				}

			}
		}
		catch (Exception e){
			e.printStackTrace();
			s.close();
			System.out.println("Error in Connection..");
		}
	}

	@Override
	public void processFileSystemEvent(FileSystemEvent fileSystemEvent) {
		Messages m = new Messages();
		Document d = new Document();
		if(fileSystemEvent.event.toString() == "FILE_CREATE") {

			d = m.file_request("FILE_CREATE_REQUEST",
					fileSystemEvent.fileDescriptor.md5,
					fileSystemEvent.fileDescriptor.lastModified  ,
					fileSystemEvent.fileDescriptor.fileSize, fileSystemEvent.pathName);
		}
		else if(fileSystemEvent.event.toString() == "FILE_MODIFY") {

			d = m.file_request("FILE_MODIFY_REQUEST",
					fileSystemEvent.fileDescriptor.md5,
					fileSystemEvent.fileDescriptor.lastModified  ,
					fileSystemEvent.fileDescriptor.fileSize, fileSystemEvent.pathName);
		}
		else if(fileSystemEvent.event.toString() == "FILE_DELETE") {

			d = m.file_request("FILE_DELETE_REQUEST",
					fileSystemEvent.fileDescriptor.md5,
					fileSystemEvent.fileDescriptor.lastModified  ,
					fileSystemEvent.fileDescriptor.fileSize, fileSystemEvent.pathName);
		}
		else if(fileSystemEvent.event.toString() == "DIRECTORY_CREATE") {

			d = m.dir_request("DIRECTORY_CREATE_REQUEST", fileSystemEvent.pathName);
		}
		else if(fileSystemEvent.event.toString() == "DIRECTORY_DELETE") {

			d = m.dir_request("DIRECTORY_DELETE_REQUEST", fileSystemEvent.pathName);
		}

		try {
			System.out.println("We sent (FSM): " + d.toJson());
			if (out != null){
				out.println(d.toJson());
			}
			else {
				byte[] sendData = d.toJson().getBytes();
				DatagramPacket DpSend = new DatagramPacket(sendData, sendData.length, this.address, this.port);
				ds.send(DpSend);
				this.packet_sent+=1;
			}

		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static StringBuilder data(byte[] a)
	{
		if (a == null)
			return null;
		StringBuilder ret = new StringBuilder();
		int i = 0;
		while (a[i] != 0)
		{
			ret.append((char) a[i]);
			i++;
		}
		return ret;
	}

}
