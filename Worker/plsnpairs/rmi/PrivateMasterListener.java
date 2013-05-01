package rmi;
import java.io.*;
import java.net.*;
import java.util.StringTokenizer;
//import java.util.zip.*;
public class PrivateMasterListener implements Runnable
{
	private String privatemasterID;
	private String control;
	protected static String masterIP;
	//private volatile boolean multicastserverthreadKiller=false;
	protected int port;;
	protected static volatile boolean broadcastkiller = false;
	protected static volatile boolean resulttransmittingflag = false;
	protected String registrationforbroadcasting;
	protected ServerSocket worker = null;
	protected Socket privatemaster = null;
	protected String clearPath = null;
	public MulticastWorkerThread broadcastthread = null;
	
	public static String [] replyline={"Searched and Ready", "Ready", "-1"};
	public PrivateMasterListener()
	{
		
	}
	public PrivateMasterListener(String registrationforbroadcasting)
	{
		this.registrationforbroadcasting=registrationforbroadcasting;
		
	}
	
	public void run()
	{
		port = Information.socketPort;
		startBroadcasting();
//		broadcastthread.start();
		
		boolean listening = true;
		DataOutputStream out = null;
		DataInputStream in = null;
		//String inputLine, outputLine;
		try
		{
			worker = new ServerSocket(port);
	    }
		catch (IOException e)
		{
			System.out.println("Listening Failed!");
		}
		
		int i=0;
		
		while (listening)
		{
			i++;
			try
			{
				System.out.println("Listening: "+i);
				privatemaster=worker.accept();
			}
			catch (IOException e)
			{
				System.out.println("Waiting for master: "+e.toString());
			}
//			
			if (privatemaster!=null)
			{
				//System.out.println("Connected");
				try {
					in = new DataInputStream(privatemaster.getInputStream());
					//String reply=null;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
	            String input = null;
				try {
					input = in.readUTF();
					System.out.println("Socket Status: "+input);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	            
	            selectIP_Port_ClearPath(input);
				String reply = replyMaster(control);
				//listening = false;
				if (!reply.equalsIgnoreCase("-1"))
				{
					try 
					{
						out = new DataOutputStream(privatemaster.getOutputStream());
					    out.writeUTF(reply);
				    } 
					catch (IOException e) 
					{
						// TODO Auto-generated catch block
					    e.printStackTrace();
				    }
				}
			}
		}
//		
	}
//	
	private String replyMaster(String commandcontrol)
	{
		if (commandcontrol.equalsIgnoreCase("Connected"))
		{
			System.out.println("Start Cleaning Result Path");
			DeleteDirectory delete = new DeleteDirectory(clearPath);
			delete.run();
			System.out.println("Cleaning Complete");
			stopBroadcasting();
			return replyline[0];
			
		}
		else if (commandcontrol.equalsIgnoreCase("Finished"))
		{
//			if (resulttransmittingflag)
//				try {
//					Thread.currentThread().wait();
//				} catch (InterruptedException e) {
//					System.out.println("Obligatory Wait Failed");
//				}
			
			System.out.println("Start Listening Again");
			//startBroadcasting();
			return replyline[1];
			
		}
		else if(commandcontrol.equalsIgnoreCase("All Mission Complete"))
		{
			//Using NFS to avoid result submission.
//			Thread thread = new Thread(new FileStreamThread(privatemaster));
//			thread.start();
//			try {
//				thread.join();
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			try {
				Thread.currentThread();
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			NpairsDataLoaderContainer.avoid_load_sign = false;
			
			try {
				Thread.currentThread();
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println("All Mission Complete, Start Broadcasting Again");
			startBroadcasting();
			return replyline[2];
		}
		else
		{
			return replyline[2];
		}
	}
	
	private void selectIP_Port_ClearPath(String info)
	{
		//String []container = null;
		StringTokenizer ID = new StringTokenizer (info, ":");
		int i=0;
		String []IDcontainer = new String[10];
		while (ID.hasMoreTokens())
		{
			IDcontainer[i] = ID.nextToken();
			i++;
		}
		control = IDcontainer[0];
		clearPath = IDcontainer[3];
//		System.out.println(control);
		//clientIP = IDcontainer[1];
		//port = Integer.parseInt(IDcontainer[2]);
		//return container;
	}
	
	private void startBroadcasting()
	{
		broadcastkiller = false;
		try 
		{
			 broadcastthread= 
					new MulticastWorkerThread(registrationforbroadcasting);
		} 
		catch (IOException e2) 
		{
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		broadcastthread.start();
	}
	
	private void stopBroadcasting()
	{
		broadcastkiller = true;
	}
	
	public static String returnprivatemasterIP()
	{
		return masterIP;
	}

}
