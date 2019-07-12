package unimelb.bitbox;

import unimelb.bitbox.util.Configuration;
import org.kohsuke.args4j.Option;
import unimelb.bitbox.util.Document;
import org.kohsuke.args4j.CmdLineParser;

import java.io.*;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

public class Client {
	public static void main(String [] args) {
		String hostname = new String();
		Integer port;
		String command;
		String sharedKey = new String();
		RSAUtil rsaobj = new RSAUtil();
		AesSecurity aesobj = new AesSecurity();
	    String privateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDmkzb84FZNY8gn4K5d9qHQIOc7XjNY/75+yGqvPMDvJ8Kid3x2/AfT88Hi1V45QF/lt2LuF6RY/eyPjrHk/57Xu794XrraGsHz1SHI+B9bTPnRnDGeDu3PNtN6JFlFqwmGZG6Lbh/Dz/jkqJq8vxN0T9rVtwi2bfHDQxesvJZyvpFBDwNcHry5sQx6WTIWukN9ctvm+iiaGPf9+dea56GANFji5Jhrb/ToL+9zWIUXOcPKygmAHWmsgujQE5EpRRn5/M6kLzG02RYFG2Lz2x86s30DLbQdV4liyLnlcPINLKh1spNh1DfJcoGjuT+LLI85VRFdR+za2RJS4peZNcCVAgMBAAECggEBAIwAXUYELULGUHUuLpGtPCk0oVJh60mh15gBtw16FSfhe4PMi01v0Iz4mARUS+b1WIUR6cbpogfK/oVYPlnugBM705GR62CdGKEmC2KMjfWQ8qs8bMAtDmZgQt4KDg6IQLtSZfDRSAZMVDL60SHD31sjVZuojGjAryhBp7FhvPQj7KXJy/1Z/fW4Z8rb/L9gPyDbzuiOU/9/UBm+wWtVRyHDJ4n1sDTp2E6d6eqsHZaZ5umLBL1HvkSqb5wbeZKzJFggpwZM+2ttZq0nA6bUoM1OlmdKdvK4z/GGLBLWyAmn/Zodhtt/lD+6iJYTeqK6eU96LSPBjehpr00lwzqVX+0CgYEA/Nn98jiQY9fbmTC8mArmWV6GOjq5ofRb1DxYJa+wy7ZLUnGPc0ZKKurb0rEfRBH5QzkvpUIJD24C8vTLbLJJDoFRacXlUVNKAWg06OD28xD7KdJW/vDPpl0yAbSEhhOT6KdJ+9DxTReGEr9Lcxvbk1Fhlc1PHkdub63CIl+AYosCgYEA6XI2c8XutaBY0kEM6QaARaWFp8NJxFz0/VsNv10jCxaZhUY6Y1GqaPC8uH33WYONuwga0vHV4BLDBO0gkR85MWZ7jHPhwwrqnoHHj0kNJ6qguRuXI4kvQ5Bn1G7ld9q+D4iMceln2x/dDjpo0ItbvX87t9fAB2NyM+WgZptUbV8CgYAOfb/cHfnIfxqK0Qw9+oHxJUW0GKGC8qpAo6S5pDQRuMTgWLnL9X9Srlsi3Bvant0WSTS91+cFB10L55OxCxa8yhSMZ1cZLhjTs9E0d5Avpg1+/BsYSVzdQAIZrurZdE3Jy6ylzffGX07DzErasgIHk2ZwW2/pYFan9+FkbuzAuQKBgBRpGf2xVpemt11atqhBG0H7oN30IyT6A6mLJn6OxBuaFD4kz8ITR9T5B2cSDGhVKjUqFj5PSqXWvhpWKTzHABcjoLW9BAYrlCvbqPkMKAxJzNeiY+qFeg5sN8fJEmMSSv/MrorfH2d3N7qgvL1PEexVjYEbafy7YybKcuXFuvH9AoGBAPXxdGyOq3Uf1FU4wwKOaI5alcF7y6FVHsab3/Rs+5nVYHpuNCkJZDQs/3FMM0u9230fc0zUq/6LHQS2IF8pm2mtY5y7ZwUzUpAKX3//kzjoKGc0Wq1cLOi7vDkq1QpD7h+zJ9+8xKsRoPAPGFKMaJ963ZhAIxU3eakdPyuGZsKx"; 
		String peerHost = new String();
		Integer peerPort = 5000;
		String identity;
		String resp1;
		String resp2; 
		String decryptResp2;
		Boolean connection = true;
		Socket socket_main = null;
		Document d1 = new Document();
		Document d2 = new Document();
		Messages m1 = new Messages();
		Messages m2 = new Messages();
		Messages m3 = new Messages();
		Document d3 = new Document();
		Document d4 = new Document();
		
		try {
			
			
			CmdLineArgs argsBean = new CmdLineArgs();
			CmdLineParser parser = new CmdLineParser(argsBean);	
			parser.parseArgument(args);
			command = argsBean.getCommand();
			String peername[]; 
			if(command.equals("connect_peer") || command.equals("disconnect_peer")) {
				peername = argsBean.getPeer().split(":");
				peerPort = Integer.parseInt(peername[1]);
				peerHost = peername[0];
			}
			identity = argsBean.getIdentity();
			String serv[] = argsBean.getServer().split(":");
			hostname = serv[0];
			port = Integer.parseInt(serv[1]);
			socket_main = new Socket(hostname, port);
			System.out.println("Peer Connected...");
			PrintWriter out = new PrintWriter(
					new BufferedWriter(new OutputStreamWriter(
							socket_main.getOutputStream(), "UTF-8")), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket_main.getInputStream(), "UTF-8"));
			d1 = m1.auth_req(identity);
			out.println(d1.toJson());
			resp1 = in.readLine();
			System.out.println(resp1);
			//Decrpyt secret key using private key.
			Document d1Resp = Document.parse(resp1);

	
			if(d1Resp.getBoolean("status")) {
				try {
					sharedKey = rsaobj.decrypt(d1Resp.getString("aes128"), privateKey);
					//System.out.println(sharedKey);
				} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException
						| NoSuchAlgorithmException | NoSuchPaddingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(command.equals("list_peers"))  {
					d2 = m2.list_peers_req();
					d3 = m3.payload(AesSecurity.encrypt(d2.toJson(), sharedKey));
					out.println(d3.toJson());
					resp2 = in.readLine();
					Document dnn = Document.parse(resp2);
					System.out.println("Received: " + resp2);
					String encrypted = dnn.getString("payload");
					System.out.println(AesSecurity.decrypt(encrypted, sharedKey));
					String message = AesSecurity.decrypt(encrypted, sharedKey);
			
					Document dnew = new Document();
					dnew = Document.parse(message);
					System.out.println((ArrayList<Document>) dnew.get("peers"));
					socket_main.close();
					
					
				}
				else if(command.equals("connect_peer")) {
					d2 = m2.connect_peers_req(peerHost, peerPort);
					d3 = m3.payload(AesSecurity.encrypt(d2.toJson(), sharedKey));
					out.println(d3.toJson());
					resp2 = in.readLine();
					Document dnn = Document.parse(resp2);
					System.out.println("Received: " + resp2);
					String encrypted = dnn.getString("payload");
					System.out.println(AesSecurity.decrypt(encrypted, sharedKey));
					String message = AesSecurity.decrypt(encrypted, sharedKey);
			
					Document dnew = new Document();
					dnew = Document.parse(message);
					System.out.println(dnew.getString("message"));
					socket_main.close();
					
				}
				else if(command.equals("disconnect_peer")) {
					d2 = m2.disconnect_peers_req(peerHost, peerPort);
					d3 = m3.payload(AesSecurity.encrypt(d2.toJson(), sharedKey));
					out.println(d3.toJson());
					resp2 = in.readLine();
					Document dnn = Document.parse(resp2);
					System.out.println("Received: " + resp2);
					String encrypted = dnn.getString("payload");
					System.out.println(AesSecurity.decrypt(encrypted, sharedKey));
					String message = AesSecurity.decrypt(encrypted, sharedKey);
			
					Document dnew = new Document();
					dnew = Document.parse(message);
					System.out.println(dnew.getString("message"));
					socket_main.close();
				}
			}
			else {
				System.out.println(d1Resp.getString("message"));
			}
		}
		catch(IOException | CmdLineException e) {

		}
	}	
		
}
