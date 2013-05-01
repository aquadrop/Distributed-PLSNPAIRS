package rmi;
import java.util.LinkedList;
import java.util.Queue;
import java.net.*;
import java.net.*;
import java.io.*;
import java.util.*;
public class MasterRunReceiver extends Thread
{
	static public String localIP;
	static int portNum = 8889;
	public String serverIP;
	public static int totalAnalysis = 2; 
//	static int currentAnalysis = 1;// indicate the analysis need to be done currently
	private static Queue<String> analysisQueue =  new LinkedList<String>();// queue for storing the analysis remains to be done
	//private static String[] commandForAnalysis = {"NPAIRS", "RUN_ANALYSIS", "./SetupFiles/2-2-4.mat"};
	private static String[] argv;
	private static String[] commandToServer={"Finished", "All Mission Complete"};
	private static String[] replyFromServer = {"Ready"};
	private Socket socketToServer;
	protected static boolean masterReceiverKiller = false;
	public static boolean[] indexFinished; 
	public MasterRunReceiver(String IP, Socket socket1, String []argv, int totalAnalysis) throws SocketException
	{
		localIP = GetLocalIP.getLocalIP();
		Information.masterIP = localIP;
		serverIP = IP;
		socketToServer = socket1;
		this.argv=argv;
        this.totalAnalysis = totalAnalysis;
	}

	@Override
	public void run() 
	{
		// TODO Auto-generated method stub
		while(true)
		{
			if(!analysisQueue.isEmpty())
			{
				String temp = analysisQueue.poll();
				int currentAnalysis = Integer.parseInt(temp);
				RMIRun remoteRun = new RMIRun(serverIP, argv, currentAnalysis, currentAnalysis);
				try 
				{
			        //run remote analysis; main function
					boolean success = remoteRun.runAnalysis();
					if(success)
					{
		//				Socket socketToServer = new Socket(serverIP, portNum);
						indexFinished[currentAnalysis]=true;
						try
						{
							Socket socketToServer = new Socket(serverIP, portNum);
							DataOutputStream out = new DataOutputStream(socketToServer.getOutputStream());
				
							out.writeUTF(MasterRunReceiver.commandToServer[0]);
							//establish tunnel to accept data;
							
							DataInputStream in =  new DataInputStream(socketToServer.getInputStream());
							String reply = in.readUTF();
							StringTokenizer token =  new StringTokenizer(reply,":");
					
							if(token.nextToken().equals(replyFromServer[0]))
							{
								continue;
							}
							
					
						}
						catch(Exception e)
						{
							//maybe push the unfinished analysis back to queue here
						}
					}
					
					else
					{
						analysisQueue.offer(String.valueOf(currentAnalysis));
						break;
					}
                  
					
				}
				catch (Exception e) {
			// TODO Auto-generated catch block
					System.out.println("Queue Server Error"+e.toString());
				}
			}
			else
			{
				break;
			}
		}
		//Request to Collect Results
		try
		{
			Socket socketToServer = new Socket(serverIP, portNum);
			DataOutputStream out = new DataOutputStream(socketToServer.getOutputStream());
			out.writeUTF(MasterRunReceiver.commandToServer[1]);
		    //establish tunnel to accept data;
		
		    FileStreamReceiverThread receiver = new FileStreamReceiverThread(socketToServer);
		    System.out.println("Start Receiving Results");
		    receiver.run();
		
		    System.out.println("Complete Receiving Results");
		    out.close();
		    socketToServer.close();
		    
		    
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		
	}


	public static void initialQueue()
	{
		indexFinished =  new boolean[totalAnalysis];
		for(int i=0; i<totalAnalysis; i++)
		{
			analysisQueue.add(String.valueOf(i));
			indexFinished[i] = false;
		}
		
	}
	
	public static boolean analysisFinished()
	{
		for(int i=0; i<totalAnalysis; i++)
		{
			if(indexFinished[i]==false)
			{
				return false;
			}
			
		}
		return true;
	}

}
