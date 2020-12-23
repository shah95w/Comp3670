

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class Jobserver2 implements Runnable {
    
    public InputStream input;
    public BufferedReader reader;
    public OutputStream output;
    public PrintWriter printwrite;
    
    public String res;
    public int multiJob;
    public int mode;
    public String IP;

    public Socket socket;
    public ServerSocket serverSocket;
    public static int port = 4999;

    public Jobserver2() {
        try {
            serverSocket = new ServerSocket(port);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public Jobserver2(int port) {
        try {
            serverSocket = new ServerSocket(port);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    public void clientConnect() {
        try {
            socket = serverSocket.accept();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clientDisconnect() {
        try {
            socket.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getIOStreams() {
        try {
            input = socket.getInputStream();
            output = socket.getOutputStream();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        reader = new BufferedReader(new InputStreamReader(input));
        printwrite = new PrintWriter(output, true);
    }

    public void closeIOStreams() {
        try {
            input.close();
            output.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getResult() {
        try {
            return reader.readLine();
        }
        catch(IOException e) {
            return "error";
        }
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public String job1(int mode, String user) {
        
        clientConnect();
        getIOStreams();

        printwrite.printf("1,%d,%s\n", mode, user);

        res = getResult();

        closeIOStreams();
        clientDisconnect();

        return res;
    }

    public String job2(String port, String ip) {
        
        clientConnect();
        getIOStreams();

        printwrite.printf("2,%s,%s\n", port, ip);

        res = getResult();

        closeIOStreams();
        clientDisconnect();

        return res;
    }

    public String job3(int mode, String user) {
        
        clientConnect();
        getIOStreams();

        printwrite.printf("3,%d,%s\n", mode, user);

        res = getResult();

        closeIOStreams();
        clientDisconnect();

        return res;
    }

    @Override
    public void run() {
        System.out.println("Entered run.");
        clientConnect();
        System.out.println("Connected.");
        getIOStreams();
        System.out.println("Communication is up and running");
        
        while(IP == null) { }

        System.out.println("Mode: " + mode + ", IP: " + IP);

        switch(multiJob) {
            case 3:
                res = job3(mode, IP);
                break;
            default:
        }
    }
}
