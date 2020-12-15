import java.io.*;
import java.util.*;
import java.net.*;

// JobCreatorServerThread: thread of server to communicate with the Job Seeker
public class JobCreatorServerThread extends Thread
{ 
	private Socket socket; // used for socket
	
	// constructor
	public JobCreatorServerThread(Socket socket)
	{
		this.socket = socket;
	}
	 
	// run: method for communication with Job Seeker
	public void run()
	{
		try {
			// used for Input
			InputStream Inp = socket.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(Inp));
			
			// used for output
			OutputStream output = socket.getOutputStream();
			PrintWriter pr = new PrintWriter(output,true);
			
			String Opt = ""; // used to get Inp on if to do another equation
			String cont = ""; // used to get Inp from user on if to continue
			// used for IP Address, port, and result
			String ipH = "";
			int givenPort = 0;
			String result = "";
			
			String start = ""; // used to check if connection accepted
			Scanner sc = new Scanner(System.in); // used for user Inp
			
			start = br.readLine(); // read on if to start
			
			// if accepted
			if(start.equals("yes")) {
				do {
					// check for option (question 1 or 2)
					int getJob = 0;
					System.out.println("Would you like to check if a IP Address/Host Name is connected to the Network (enter 0) or check the status of a given Port at a specific IP Address/Host Name (enter 1)?");
					getJob = sc.nextInt();
					
					// send option to Job Seeker
					pr.println(getJob);
					
					if(getJob == 0) {
						// get the IP Address or host name from the user and send to Job Seeker
						System.out.print("Enter the IP Address or Host Name: ");
						ipH = sc.next();
						pr.println(ipH);
						
						result = br.readLine(); // get result from Job Seeker
	
						// output result
						if(result.equals("Yes")) {
							System.out.println("\nCompleted Successfully\nJob Seeker says " +result + " " +ipH + " is Connected to the Network.\n");
						}else if(result.equals("No")){
							System.out.println("\nCompleted Successfully\nJob Seeker says " +result + " " +ipH + " is not Connected to the Network.\n");
						}else {
							System.out.println("\nCompleted Unsuccessfully\nJob Seeker says " +ipH +" does not exist.");
						}
					}else {
						// get the IP Address or Host Name
						System.out.print("Enter the IP Address or Host Name: ");
						ipH = sc.next();
						pr.println(ipH); // send IP Address to Job Seeker
						
						int n = 0;
						do {
							if(n>0) {
								System.out.println("Port Number not in TCP/UDP Range");
							}
							// Port is open or closed and can be TCP or UDP, so Port can be between 1 and 49190, send this info to Job Seeker
							System.out.print("Enter a Port Number between 1 and 49190: ");
							givenPort = sc.nextInt();
							++n;
						}while((givenPort <= 0) || (givenPort >= 49191));
						pr.println(givenPort); // send port to Job Seeker
						
						// get the result and output
						result = br.readLine();
						if(result.equals("true")) {
							System.out.println("\nPort Number " +givenPort + " is open at IP Address/Host Name " +ipH);				
						}else {
							System.out.println("\nPort Number " +givenPort + " is closed/filtered at IP Address/Host Name " +ipH);				
						}
					}
					
					// check for another
					Opt = br.readLine();

					// if another
					if(Opt.equals("yes")) {
						// get from user if to continue
						System.out.println("Job Seeker wants another Job");
						System.out.println("Would you like to continue with this Job Seeker? (yes/no)");
						cont = sc.next();
						
						pr.println(cont);
					}else {
						System.out.println("Job Seeker terminated the connection");
					}			
					
				}while(Opt.equals("yes") && cont.equals("yes")); // check if another iteration is wanted
				
			}else {
				System.out.println("Job Seeker has rejected the check IP Address/Host Name job or check the status of a given Port at a specific IP Address/Host Name job and terminated the connection");
			}
			
			socket.close(); // close connection
			
		}catch(IOException ex) {
			System.out.println("I/O error: " + ex.getMessage());
		}
	}
}