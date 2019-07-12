package unimelb.bitbox;

import org.json.simple.JSONObject;
import unimelb.bitbox.util.Configuration;
import unimelb.bitbox.util.Document;

import java.io.*;
import java.net.*;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public class UDPPeer extends ServerMain
{
    private DatagramSocket socket_main;
    private int count  = 0;
    private Socket socket = null;
    private static Logger log = Logger.getLogger(Peer.class.getName());
    private static boolean connect_status = true;
    private static boolean disconnect_status = true;


    public UDPPeer(String hostname, int port, int myadd_port) throws IOException, NoSuchAlgorithmException {
        this(myadd_port);
        DatagramPacket DpSend;
        DatagramPacket DpReceive;
        byte[] sendData;
        byte[] receiveData = new byte[65535];
        System.out.println("Peer trying to connect to ..."+ hostname +":"+ port);
        Messages m = new Messages();
        Document hs = m.handshake_request();
        sendData = hs.toJson().getBytes();
        InetAddress server_address = InetAddress.getByName(hostname);
        DpSend = new DatagramPacket(sendData, sendData.length, server_address, port);
        socket_main.send(DpSend);
        System.out.println("Handshake Sent:"+ hs.toJson());

        DpReceive = new DatagramPacket(receiveData, receiveData.length);
        socket_main.receive(DpReceive);
        System.out.println("Handshake Result:" + data(receiveData));

        Document d;
        d = Document.parse(data(receiveData).toString());
        if (d.getString("command").equals("CONNECTION_REFUSED")){
            hostConnections.remove(hostname);
            ArrayList A =  d.getArray("peers");
            JSONObject a = (JSONObject) A.get(0);
            String hostname_new = (String) a.get("host");
            Long portL = (Long) a.get("port");
            int port_new = portL.intValue();
            UDPPeer client_side = new UDPPeer(hostname_new, port_new,
                    Integer.parseInt(Configuration.getConfigurationValue("port")));
        }
        hostConnections.put(hostname, (long) port);
        new ServerMain(socket_main, hostname, port);


    }

    public UDPPeer(int port, int max_connections) throws IOException {
        this(port+1);
        System.out.println("Peer Started Listening to UDP Peers at :" + (port+1));
        System.out.println("Waiting for connections...");
        HashMap<String, Long> hostConnections = new HashMap<>();

        while(true){
            byte[] buf = new byte[512];

            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket_main.receive(packet);
            InetAddress new_address = packet.getAddress();
            int new_port = packet.getPort();
            System.out.println("Client sent: "+ data(buf).toString());

            this.count += 1;
            if ((this.count != 0) && (this.count < max_connections))
            {
                new Thread(new Runnable() {
                    String clientname = "";
                    Long clientport = 0L;
                    InetAddress new_address = packet.getAddress();
                    int new_port = packet.getPort();
                    DatagramPacket DpSend;
                    byte[] sendData;

                    @Override
                    public void run() {
                        try {
                            while(true) {
                                try{
                                    String message = data(buf).toString();
                                    if (message.equals(""))
                                    {
                                        count -= 1;
                                        break;
                                    }
                                    Checker check = new Checker(fileSystemManager);
                                    Document d = Document.parse(message);
                                    if (d.getString("command").equals("HANDSHAKE_REQUEST"))
                                    {
                                        clientname = ((Document)d.get("hostPort")).getString("host");
                                        clientport = ((Document)d.get("hostPort")).getLong("port");
                                        hostConnections.put(clientname, clientport);
                                    }
                                    Document response = check.check_command(message, fileSystemManager,hostConnections);
                                    if (!(response == null))
                                    {
                                        System.out.println("We sent: " + response.toJson());
                                        sendData = response.toJson().getBytes();
                                        DpSend = new DatagramPacket(sendData, sendData.length, new_address, new_port);
                                        socket_main.send(DpSend);
                                        try {
                                            if (response.getString("command").equals("FILE_CREATE_RESPONSE") && response.getBoolean("status")) {
                                                Messages m2 = new Messages();
                                                Document d2 = m2.byte_request(((Document) response.get("fileDescriptor")).getString("md5"),
                                                        ((Document) response.get("fileDescriptor")).getLong("lastModified"),
                                                        ((Document) response.get("fileDescriptor")).getLong("fileSize"),
                                                        response.getString("pathName"), 0L,
                                                        ((Document) response.get("fileDescriptor")).getLong("fileSize"));
                                                System.out.println("We sent message 2:" + d2.toJson());
                                                sendData = d2.toJson().getBytes();
                                                DpSend = new DatagramPacket(sendData, sendData.length, new_address, new_port);
                                                socket_main.send(DpSend);
                                                break;
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
                                                DpSend = new DatagramPacket(sendData, sendData.length, new_address, new_port);
                                                socket_main.send(DpSend);
                                                break;
                                            }
                                            break;
                                        } catch (Exception e){e.printStackTrace();}
                                    }
                                }
                                catch(Exception e){
                                    e.printStackTrace();
                                    hostConnections.remove(clientname);
                                    System.out.println("Connection Closed Unexpectedly!");
                                    break;

                                }
                            }
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                }).start();
            }
            else
            {
                Messages m = new Messages();
                Document d = m.connection_refused(hostConnections);
                byte[] sendData = d.toJson().getBytes();
                DatagramPacket DpSend = new DatagramPacket(sendData, sendData.length, new_address, new_port);
                socket_main.send(DpSend);
            }
        }

    }

    public UDPPeer(int port) throws SocketException {
        socket_main = new DatagramSocket(port);

    }

    public UDPPeer(int port, int max_connections, boolean tcp) throws IOException {
        ServerSocket serverSock = new ServerSocket(port);
        System.out.println("Peer Started Listening to TCP clients at :" + (port));
        String secretKey = "abcdefghijklmnopqrstuvwxyz";
        try {


            while(true)
            {
                try
                {
                    this.socket = serverSock.accept();
                }
                catch (Exception ignored) { }

                count += 1;
                System.out.println("Found Connection!");

                assert socket != null;
                BufferedReader in = new BufferedReader(new
                        InputStreamReader(socket.getInputStream(), "UTF-8"));
                PrintWriter out = new PrintWriter(
                        new BufferedWriter(new OutputStreamWriter(
                                socket.getOutputStream(), "UTF-8")), true);
//					hostConnections.put("TEMP", 80L);
//					hostConnections.put("TEMP222", 80L);

                if ((this.count != 0) && (this.count < max_connections))
                {
                    new Thread(new Runnable() {
                        String clientname = "";
                        Long clientport = 0L;

                        @Override
                        public void run() {
                            try {
                                while(true) {
                                    try{
                                        String message = in.readLine();
                                        if (message == null)
                                        {
                                            count -= 1;
                                            break;
                                        }
                                        System.out.println("Client sent: "+ message);

                                        Checker check = new Checker(fileSystemManager);
                                        Document d = Document.parse(message);
//                                        System.out.println(d.toJson());

                                        Document response = new Document();

                                        if(d.containsKey("command")) {
                                            if (d.getString("command").equals("HANDSHAKE_REQUEST"))
                                            {
                                                clientname = ((Document)d.get("hostPort")).getString("host");
                                                clientport = ((Document)d.get("hostPort")).getLong("port");
                                                hostConnections.put(clientname, clientport);
                                            }
                                        }

                                        String m = "";
                                        if(d.containsKey("payload")) {
                                            String encrypted = d.getString("payload");
                                            message = AesSecurity.decrypt(encrypted, secretKey);

                                            Document dnew = Document.parse(message);
                                            System.out.println(dnew.toJson());
                                            if (dnew.containsKey("host"))
                                            {

                                                String hostname_new = dnew.getString("host");
                                                Long portL = dnew.getLong("port");
                                                int port_new = portL.intValue();


                                                if(dnew.getString("command").equals("CONNECT_PEER_REQUEST")){
                                                    System.out.println("Connections:" + hostConnections);
                                                    if (hostConnections.containsKey(hostname_new) && hostConnections.get(hostname_new).equals(portL)){
                                                        connect_status = false;
                                                        m = "Peer is already connected to " + hostname_new +":"+ port_new;
                                                        System.out.println(m);
                                                        //Already present in host connections
                                                    }
                                                    else{
                                                        //Try to connect to h_name and port.
                                                        connect_status = new_UDPPeer(hostname_new, port_new, port);
                                                        if (connect_status){
                                                            hostConnections.put(hostname_new+"1", (long) port_new);
                                                            System.out.println("Connections:" + hostConnections);
                                                        }
                                                    }

                                                }
                                                if(dnew.getString("command").equals("DISCONNECT_PEER_REQUEST")){
                                                    if (!hostConnections.containsKey(hostname_new)){
                                                        disconnect_status = false;
                                                        m = "Peer is already not connected to " + hostname_new +":"+ port_new;
                                                        System.out.println(m);
                                                    }
                                                    else {
                                                        hostConnections.remove(hostname_new);
                                                        disconnect_status = true;
                                                        //close socket
                                                    }
                                                }
                                            }
                                        }

                                        response = check.check_command(message, fileSystemManager, hostConnections);

                                        if (!(response == null))
                                        {
                                            if(response.getString("command").equals("CONNECT_PEER_RESPONSE")
                                                    || response.getString("command").equals("DISCONNECT_PEER_RESPONSE")
                                                    || response.getString("command").equals("LIST_PEERS_RESPONSE")) {
                                                if(response.getString("command").equals("CONNECT_PEER_RESPONSE")){
                                                    response.append("status", connect_status);
                                                    if (!m.equals("")){
                                                        response.append("message", m);
                                                    }
                                                    System.out.println("Our Response:" + response.toJson());
                                                }
                                                if(response.getString("command").equals("DISCONNECT_PEER_RESPONSE")){
                                                    response.append("status", disconnect_status);

                                                    if (!m.equals("")){
                                                        response.append("message", m);
                                                    }
                                                    System.out.println("Our Response:" + response.toJson());
                                                }
                                                Messages mnew = new Messages();
                                                Document dneww;
                                                dneww = mnew.payload(AesSecurity.encrypt(response.toJson(), secretKey));
                                                System.out.println("We Sent Encrypted:" + dneww.toJson());
                                                out.println(dneww.toJson());

                                            }
                                            else{
                                                System.out.println("We sent: " + response.toJson());
                                                out.println(response.toJson());
                                                try {
                                                    if (response.getString("command").equals("FILE_CREATE_RESPONSE") && response.getBoolean("status")) {
                                                        Messages m2 = new Messages();
                                                        Document d2 = m2.byte_request(((Document) response.get("fileDescriptor")).getString("md5"),
                                                                ((Document) response.get("fileDescriptor")).getLong("lastModified"),
                                                                ((Document) response.get("fileDescriptor")).getLong("fileSize"),
                                                                response.getString("pathName"), 0L,
                                                                ((Document) response.get("fileDescriptor")).getLong("fileSize"));
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
                                    }
                                    catch(Exception e){
                                        hostConnections.remove(clientname);
                                        e.printStackTrace();
                                        System.out.println("Connection Closed Unexpectedly!");
                                        in.close();
                                        break;
                                    }
                                }

                                System.out.println("Closing Connection: No error");
                                in.close();

                            }
                            catch (Exception e) {
                                e.printStackTrace();
                                System.out.println("Closing Connection");
                            }
                            finally
                            {
                                try {
                                    socket.close();
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                    }).start();
                }
                else
                {
                    Messages m = new Messages();
                    Document d = m.connection_refused(hostConnections);
                    out.println(d.toJson());
                }


            }
        }
        catch(IOException ignored) {
        }
        }

    public boolean new_UDPPeer(String hostname, int port, int my_ad_port) throws IOException, NoSuchAlgorithmException {
        socket_main = new DatagramSocket(my_ad_port);
        boolean status = false;
        DatagramPacket DpSend;
        DatagramPacket DpReceive;
        byte[] sendData;
        byte[] receiveData = new byte[65535];
        System.out.println("Peer trying to connect to ..."+ hostname +":"+ port);
        Messages m = new Messages();
        Document hs = m.handshake_request();
        sendData = hs.toJson().getBytes();
        InetAddress server_address = InetAddress.getByName(hostname);
        DpSend = new DatagramPacket(sendData, sendData.length, server_address, port);
        socket_main.send(DpSend);
        System.out.println("Handshake Sent:"+ hs.toJson());

        DpReceive = new DatagramPacket(receiveData, receiveData.length);
        socket_main.receive(DpReceive);
        System.out.println("Handshake Result:" + data(receiveData));

        Document d;
        d = Document.parse(data(receiveData).toString());
        if (d.getString("command").equals("CONNECTION_REFUSED")) {
            return status;
        } else {
            status = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        new ServerMain(socket_main, hostname, port);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            return status;
        }


    }


    public static void main( String[] args ) throws NumberFormatException {

        System.setProperty("java.util.logging.SimpleFormatter.format",
                "[%1$tc] %2$s %4$s: %5$s%n");
        log.info("BitBox Peer starting...");
        Configuration.getConfiguration();

        String [] list_peers = Configuration.getConfigurationValue("peers").split(",");
        int clientPort = Integer.parseInt(Configuration.getConfigurationValue("clientPort"));

        for (int i = 0; i < list_peers.length; ++i)
        {
            String [] current_peer = list_peers[0].split(":");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try
                    {
                        UDPPeer client_side = new UDPPeer(current_peer[0],
                                Integer.parseInt(current_peer[1]),
                                Integer.parseInt(Configuration.getConfigurationValue("port"))
                        );
                    }
                    catch (Exception e){e.printStackTrace();}
                }
            }).start();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UDPPeer server_side = new UDPPeer(Integer.parseInt(Configuration.getConfigurationValue("port")),
                            Integer.parseInt(Configuration.getConfigurationValue("maximumIncomingConnections")));
                }
                catch (Exception e){e.printStackTrace();}
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UDPPeer tcp_side = new UDPPeer(Integer.parseInt(Configuration.getConfigurationValue("clientPort")),
                            Integer.parseInt(Configuration.getConfigurationValue("maximumIncomingConnections")),
                            true);
                }
                catch (Exception e){e.printStackTrace();}
            }
        }).start();


//43.240.97.106 3000 //203.101.225.147 8111

//35.244.98.174  8112


    }
}
