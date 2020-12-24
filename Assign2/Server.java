import java.io.*;
import java.util.*;
import java.net.*;

// Server: class to establish connection and send to thread
public class Server
{
	public static void main(String[] args)
	{
		// port number
		int portnum = 8080;
		
		// Accepts a connection from Client and sends it to thread.
		try(ServerSocket serverSocket = new ServerSocket(portnum)){
			System.out.println("The Job Creator is available at Port " +portnum + " looking for a Job Seeker to add 2 numbers");
		
			while(true) {
				// accept and send
				Socket socket = serverSocket.accept();
				System.out.println("\nConnected to a Job Seeker!\n");
				new Thread(socket).start();
			}
			
    	} catch (UnknownHostException ex) {
    		System.out.println("Server not found: " + ex.getMessage());
    	} catch (IOException ex) {
    		System.out.println("IO error: " + ex.getMessage());
    	}		
	}
}