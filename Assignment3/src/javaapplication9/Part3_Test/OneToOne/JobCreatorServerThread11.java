import java.io.*;
import java.util.*;
import java.net.*;

// JobCreatorServerThread: thread of server to communicate with the Job Seeker
public class JobCreatorServerThread11 extends Thread
{ 
	private Socket xok; // used for Socket
	
	// constructor
	public JobCreatorServerThread11(Socket xok)
	{
		this.xok = xok;
	}
	 
	// run: method for communication with Job Seeker
	public void run()
	{
		try {
			// used for Input
			InputStream Inp = xok.getInputStream();
			BufferedReader Bfr = new BufferedReader(new InputStreamReader(Inp));
			
			// used for Output
			OutputStream Out = xok.getOutputStream();
			PrintWriter Prw = new PrintWriter(Out,true);
			
			String opt = ""; // used to get Input on if to do another equation
			String cont = ""; // used to get Input from user on if to continue
			// used for IP Address, port, and result
			String zen = "";
			int givenPort = 0;
			String result = "";
			
			String start = ""; // used to check if connection accepted
			Scanner sc = new Scanner(System.in); // used for user Input
			
			start = Bfr.readLine(); // read on if to start
			
			// if accepted
			if(start.equals("yes")) {
				do {
					// check for option (question 1 or 2)
					int getJob = 0;
					System.out.println("Would you want to check if an IP Address/Host Name is connected to the Network, then enter 0 or check to the status of a given Port at a specific IP Address/Host Name, then enter 1!");
					getJob = sc.nextInt();
					
					// send option to Job Seeker
					Prw.println(getJob);
					
					if(getJob == 0) {
						// get the IP Address or host name from the user and send to Job Seeker
						System.out.print("Enter the IP Address or Host Name: ");
						zen = sc.next();
						Prw.println(zen);
						
						result = Bfr.readLine(); // get result from Job Seeker
	
						// Output result
						if(result.equals("Yes")) {
							System.out.println("\nCompleted Successfully\nJob Seeker says " +result + " " +zen + " is Connected to the Network.\n");
						}else if(result.equals("No")){
							System.out.println("\nCompleted Successfully\nJob Seeker says " +result + " " +zen + " is not Connected to the Network.\n");
						}else {
							System.out.println("\nCompleted Unsuccessfully\nJob Seeker says " +zen +" does not exist.");
						}
					}else {
						// get the IP Address or Host Name
						System.out.print("Enter the IP Address or Host Name: ");
						zen = sc.next();
						Prw.println(zen); // send IP Address to Job Seeker
						
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
						Prw.println(givenPort); // send port to Job Seeker
						
						// get the result and Output
						result = Bfr.readLine();
						if(result.equals("true")) {
							System.out.println("\nPort Number " +givenPort + " is open at IP Address/Host Name " +zen);				
						}else {
							System.out.println("\nPort Number " +givenPort + " is closed/filtered at IP Address/Host Name " +zen);				
						}
					}
					
					// check for another
					opt = Bfr.readLine();

					// if another
					if(opt.equals("yes")) {
						// get from user if to continue
						System.out.println("Job Seeker wants another Job");
						System.out.println("Would you like to continue with this Job Seeker? (yes/no)");
						cont = sc.next();
						
						Prw.println(cont);
					}else {
						System.out.println("Job Seeker terminated the connection");
					}			
					
				}while(opt.equals("yes") && cont.equals("yes")); // check if another iteration is wanted
				
			}else {
				System.out.println("Job Seeker has rejected the check IP Address/Host Name job or check the status of a given Port at a specific IP Address/Host Name job and terminated the connection");
			}
			
			xok.close(); // close connection
			
		}catch(IOException ex) {
			System.out.println("I/O error: " + ex.getMessage());
		}
	}
}