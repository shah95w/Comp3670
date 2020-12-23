// A Java program for a Server


import org.pcap4j.core.PacketListener;
import org.pcap4j.core.PcapPacket;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;






public class Jobserver {

    public static Scanner scanner = new Scanner(System.in);
    public static ServerSocket servSoc = null;
    public static Socket socket = null;
    public static Thread th = new Thread();
    static FileWriter filewriter;
    static PrintWriter printWriter = null;
    

    public static void main(String[] args) {
        int port = 8080; 
        try {

            filewriter = new FileWriter("jobserveroutput.txt");
            filewriter.write("Job server output\n");

            servSoc = new ServerSocket(port);

            while(true) {

                run();
                String client = socket.getInetAddress().getHostAddress();
                InputStream input = socket.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(input));
                filewriter.write("Connected with job client\n");
                filewriter.write("Client address: " + client + "\n");
                System.out.println("Client address: " + client);
                System.out.println("Connected with job client");
                OutputStream output = socket.getOutputStream();
                printWriter = new PrintWriter(output, true);

                boolean st = true;
                while(st) {
                    String res;
                    switch (jobOptions()) {
                     
                        case 0:
                            printWriter.println("0");
                            break;
                        case 1:
                            filewriter.write("1. Fetching IP address.\n");
                            System.out.println("1. Fetching IP address..");

                            filewriter.write("2. Detecting the host name\n");
                            System.out.println("2. Detecting the host name");

                            int iHost = scanner.nextInt();
                            if (iHost == 1) {
                                filewriter.write("Enter the IP address: \n");
                                System.out.print("Enter the IP address: ");
                            }
                            else {
                                filewriter.write("Enter the IP address: \n");
                                System.out.print("Enter the IP address: ");
                            }

                            scanner.nextLine();
                            String o = scanner.nextLine();

                            printWriter.printf("1,%d,%s\n", iHost , o);
                            res = br.readLine();
                            if (res.compareTo("error") == 0) {
                                filewriter.write("Error in input data. Cannot complete job.\n");
                                System.out.println("Error in input data. Cannot complete job.");   
                            }

                            else {
                                filewriter.write(res + "\n");
                                System.out.println(res);
                            }
                            break;
                        case 2:
                            filewriter.write("Enter the IP address: ");
                            System.out.print("Enter the IP address: ");
                            scanner.nextLine();
                            
                            String input_pr = scanner.nextLine();
                            filewriter.write("Enter the IP address: ");
                            System.out.print("Enter the port number: ");
                            
                            String portNum = scanner.nextLine();

                            printWriter.printf("2,%s,%s\n", input_pr, portNum);

                            res = br.readLine();
                            if (res.compareTo("error") == 0) {
                                filewriter.write("Error in input data!! Cannot complete job!");
                                System.out.println("Error in input data!! Cannot complete job!");  
                            }
                            else {
                                filewriter.write("Status of port " +portNum+ " at IP address "+input+" is: "+res+"\n");
                                System.out.printf("Status of port %s at IP address %s is: %s\n", portNum, input, res);  
                            }
                            break;

                        // The Jobserver wishes to disconnect from the Jobclient
                        case 3:
                            printWriter.println("3,");
                            st= false;
                            break;
                        case 4:
                            printWriter.println("4,");
                            System.out.println("Enter the IP address for which you want to launch an attack:");
                            String target = scanner.next();
                            printWriter.println(target);
                            break;
                        case 5:
                            printWriter.println("5,");
                            System.out.println("Enter the IP address:");
                            String targetIp = scanner.next();
                            System.out.println("Enter the Port number: ");
                            int portNumber = scanner.nextInt();
                            printWriter.println(targetIp);
                            printWriter.println(portNumber);
                            break;
                        default:
                            filewriter.write("Invalid option!\n");
                            System.out.println("Invalid option!");
                    }
                }

                printWriter.flush();
                socket.close();

                // Check if Jobserver wants to find another Jobclient or stop creating jobs
                filewriter.write("1. Wait  for other Jobclient.\n2. Exit.\n");
                System.out.println("1. Wait for other Jobclient.\n2. Exit.");
                
                int ans;
                do {
                    ans = scanner.nextInt();
                    if(ans < 1 || ans > 2) {
                        filewriter.write("Enter a valid option number.\n");
                        System.out.println("Enter a valid option number.");
                    }
                    else
                        break;
                } while(true);

                if(ans == 1) {
                    filewriter.write("Waiting for Jobclient....");
                    System.out.println("Waiting for Jobclient....");
                }
                else
                    break;
            }

            filewriter.close();             
        }
        catch(Exception e){
            e.printStackTrace();
        }
        th.interrupt();
        scanner.close();
    }
    
    public static int jobOptions() throws IOException {
         filewriter.write("1. Looking for online IP address or Hostname\n");
        filewriter.write("2. Checking for the status\n");
        filewriter.write("3. Disconnecting from Jobclient.\n");
        filewriter.write("4. ICMP flood attack\n");
        filewriter.write("5. TCP flood attack\n");

        System.out.println("1. Looking for online IP address or Hostname");
        System.out.println("2. Checking for the status");
        System.out.println("3. Disconnecting from Jobclient.");
        System.out.println("4. ICMP flood attack");
        System.out.println("5. TCP flood attack");

        return scanner.nextInt();
    }
    
    public static void run(){
        try {
            socket = servSoc.accept();
            if(th.isInterrupted())
                th.start();
            PacketListener packList= new PacketListener() {
                @Override
                public void gotPacket(PcapPacket pcapPacket) {
                    try {
                        filewriter.write("Received packets: \n"+pcapPacket+"\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Received packets: ");
                    System.out.println(pcapPacket);   
                }
            };
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void icmpattack(String hostName, String target) throws IOException {
        ArrayList<String> client = new ArrayList<>();
        if(hostName.equals(target)) 
        {
            client.add(hostName);
        }
        filewriter.write(client.size());
        System.out.println(client.size());
        
        printWriter.println("icmp");
        printWriter.println(target);
    }
}

