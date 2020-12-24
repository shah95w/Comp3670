import java.io.*;
import java.util.*;
import java.net.*;

// Client: Job Seeker to communicate with Job Creator
public class Client
{
	public static void main(String[] args)
	{
		// IP and port number, could change, used to run on local machine
		String IPAddress = "192.168.29.208";
		int portnum = 8080;
		
		// used for socket
		try(Socket socket = new Socket(IPAddress,portnum)){
			System.out.println("Job Seeker is available looking for a Job Creator to send 2 numbers to add");
			
			// used for output
			OutputStream output = socket.getOutputStream();
			PrintWriter pw = new PrintWriter(output,true);
			
			// used for input
			InputStream input = socket.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(input));
			
			String opt = "yes"; // used to get option on if to get another equation
			String cont = "yes"; // used to check if Job Creator has terminated the connection
			String check = ""; // used to check if connection is accepted
	
			Scanner sc = new Scanner(System.in); // used to get input
			
			// check if job is wanted
			System.out.println("Do you want to accept this addition job from the Job Creator? (yes/no)");
			check = sc.next();
			pw.println(check); // send equation is wanted to Job Creator
			
			// if wanted
			if(check.equals("yes")) {
				do {	
					
					// parse integers from the Job Creator
					int a = 0;
					int b = 0;
					a = Integer.parseInt(br.readLine());
					b = Integer.parseInt(br.readLine());
					
					// output numbers to be added
					System.out.println("\nAdding " +a + " + " +b);
					
					int result = a + b; // compute addition equation
					
					// send result to Job Creator
					System.out.println("Sending Result " +result + " to Job Creator");
					pw.println(result);
					
					// ask if another is wanted
					System.out.println("\nWant to do another equation? (yes/no)");
					opt = sc.next();
					
					// send option to Job Creator
					pw.println(opt);
					
					// check if Creator terminated
					if((cont=br.readLine()) != null) {
						if(cont.equals("no")) {
							System.out.println("Job Creator terminated the Connection");
						}
					}				
					
				}while(opt.equals("yes") && cont.equals("yes")); // check if another iteration is wanted
				
			}else {
				pw.println(check); // if rejected
			}

			socket.close(); // close connection
			
    		} catch (UnknownHostException ex) {
    			System.out.println("Server not found: " + ex.getMessage());
    		} catch (IOException ex) {
    			System.out.println("I/O error: " + ex.getMessage());
    		}		
	}
}
