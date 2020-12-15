import java.io.*;
import java.util.*;
import java.net.*;

// JobCreatorServer: class to establish connection and send to thread
public class JobCreatorServer
{
	public static void main(String[] args)
	{
		// port number, can change to whoever is running 
		int port = 8080;
		
		// accept a connection from a job seeker and send to thread to start
		try(ServerSocket serverSocket = new ServerSocket(port)){
			System.out.println("The Job Creator is available at Port " +port + " looking for a Job Seeker to check if a IP Address/Host Name is Connected to the Network or check the status of a given Port at a specific IP Address or Host Name");
		
			while(true) {
				// accept and send
				Socket socket = serverSocket.accept();
				System.out.println("\nConnected to a Job Seeker\n");
				new JobCreatorServerThread(socket).start();
			}
			
    	} catch (UnknownHostException ex) {
    		System.out.println("Server not found: " + ex.getMessage());
    	} catch (IOException ex) {
    		System.out.println("I/O error: " + ex.getMessage());
    	}		
	}
}