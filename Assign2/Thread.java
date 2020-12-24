import java.io.*;
import java.util.*;
import java.net.*;

// Thread: thread of server to communicate with the Job Seeker
public class Thread extends Thread
{ 
	private Socket socket; // used for socket
	
	// constructor
	public Thread(Socket socket)
	{
		this.socket = socket;
	}
	 
	// run: method for communication with Job Seeker
	public void run()
	{
		try {
			// used for input
			InputStream input = socket.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(input));
			
			// used for output
			OutputStream output = socket.getOutputStream();
			PrintWriter pr = new PrintWriter(output,true);
			
			String option = ""; // used to get input on if to do another equation
			String cont = ""; // used to get input frpm user on if to continue
			// used for addition
			int a = 0;
			int b = 0;
			String result = "";
			
			String start = ""; // used to check if connection accepted
			Scanner sc = new Scanner(System.in); // used for user input
			
			start = br.readLine(); // read on if to start
			
			// if accepted
			if(start.equals("yes")) {
				do {
					
					// get numbers and send to Job Seeker
					System.out.println("Enter an integer between 0 and 10 000: ");
					a = sc.nextInt();
					pr.println(a);
					
					System.out.println("Enter an integer between 0 and 10 000: ");
					b = sc.nextInt();
					pr.println(b);
					
					result = br.readLine(); // get result from Job Seeker

					// output result
					System.out.println("\nCompleted Successfully\nJob Seeker says " +a + " + " +b + " = " +result + "\n");
					
					// check for another
					option = br.readLine();

					// if another
					if(option.equals("yes")) {
						// get from user if to continue
						System.out.println("Job Seeker wants another equation");
						System.out.println("Would you like to continue with this Job Seeker? (yes/no)");
						cont = sc.next();
						
						pr.println(cont);
					}else {
						System.out.println("Job Seeker terminated the connection");
					}			
					
				}while(option.equals("yes") && cont.equals("yes")); // check if another iteration is wanted
				
			}else {
				System.out.println("Job Seeker has rejected the addition job and terminated the connection");
			}
			
			socket.close(); // close connection
			
		}catch(IOException ex) {
			System.out.println("I/O error: " + ex.getMessage());
		}
	}
}