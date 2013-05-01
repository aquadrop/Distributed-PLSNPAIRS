package rmi;
import java.io.IOException;
import java.util.StringTokenizer;
import java.net.*;
import java.util.Vector;
import java.net.Socket;
import java.io.*;

public class MasterReceiver implements Runnable
{
	private static int max_Thread_Volume = 300;
	private int port;;
	private InetAddress group = null;
	private MulticastSocket socket = null;
	private String controlcommand;
	//private Thread thread;
	
//	static String connected = "connected";//let server know the connection
//	static String finished = "finished";//let server know one analysis is done
	static String worker ="Worker";
	protected static  Vector IPArray;// to store the IP of server available currently
	public int totalAnalysis; 
	public int currentAnalysis;
	protected String[] argv;
	static String[] command={"Connected","Finished","Searched and Ready"};
	
	static public String localIP;
	
	public int socketoPortNum;
	
	public MasterReceiver(int totalAnalysis, String[] argv) throws SocketException
	{
		//this.controlcommand=controlcommand;
		this.socketoPortNum = Information.socketPort;
		this.argv=argv;
		Information.argv = argv;
		this.port=Information.broadcastPort;
		this.totalAnalysis=totalAnalysis;
		try 
		{
			group = InetAddress.getByName("239.255.8.0");
			socket = new MulticastSocket(port);
			socket.joinGroup(group);
		}
		
		catch(Exception e)
		{}
		IPArray = new Vector();
		
//		localIP = GetLocalIP.getLocalIP();
		localIP = Information.masterIP;
	}
	
	
	@Override
	public void run() 
	{
		//String commandcontrol="shutdown";
		System.out.println("Start Cleaning Result Path"+" "+Information.resultPath);
		DeleteDirectory delete = new DeleteDirectory(Information.resultPath);
		delete.run();
		System.out.println("Cleaning Complete");
		boolean loop = true;
		System.out.println("Start Searching!");
		Thread[] threadpool = new Thread[max_Thread_Volume];
		int threadpool_count = 0;
		while (loop)
		{
			loop = MasterRunReceiver.masterReceiverKiller;
			byte []data = new byte[8192];
			DatagramPacket packet=null;
			packet = new DatagramPacket(data, data.length, group, port);
			
			try
			{
				socket.receive(packet);
				
				String message = new String (packet.getData(),0,packet.getLength());
				System.out.println("Address Received: "+message);
				StringTokenizer str = new StringTokenizer(message, ":");
				
				if(str.nextToken().equals(worker))
				{
					String IP = str.nextToken();
					String portNum = str.nextToken();
					int length = IPArray.size();
					int workerportNum = Integer.parseInt(portNum);
					WorkerIP workerIP =  new WorkerIP(IP, workerportNum);
					boolean exist = false;
					for(int i=0; i< length; i++)
					{
						WorkerIP tempIP = (WorkerIP) IPArray.get(i);
						if(workerIP.equals(tempIP))
						{
							exist = true;
							break;
						}
					}
					
					if(!exist)
					{
						IPArray.add(workerIP);
					
					
						try{
							
							//Socket serverSocket = new Socket(IP, serverportNum);
							Socket serverSocket = new Socket(IP, socketoPortNum);
							try
							{
								DataOutputStream out = new DataOutputStream(serverSocket.getOutputStream());
								String str1 = new String(command[0]+":"+localIP+":"+String.valueOf(this.socketoPortNum)+":"+Information.resultPath);
								out.writeUTF(str1);
							}
							catch(Exception e)
							{
								System.out.println(e.toString());
							}
							
							try
							{
								
								
								DataInputStream in = new DataInputStream(serverSocket.getInputStream());
								String reply;
								reply = in.readUTF();
								System.out.println(reply);
								StringTokenizer str2 = new StringTokenizer(reply, ":");
//								
								if(str2.nextToken().equals(command[2]));
								{
									threadpool[threadpool_count] =  new Thread(new 
											MasterRunReceiver(IP, serverSocket, argv, totalAnalysis));
									threadpool[threadpool_count].start();
									threadpool_count++;
									//runThread.join();
									//Thread.currentThread().sleep(1000);
								}
								
							}
							catch(Exception e)
							{
								System.out.println(e.toString());
							
							}
							
						

//							in.close();
//							out.close();
//							serverSocket.close();
						
					
						}
					
					
						catch (Exception e)
						{
								System.out.println(e.toString());
								
						}
					}
				}				
				
				
			}
			catch(Exception e)
			{
				System.out.println(e.toString());
			}
//			try 
//			{
//				ServerControlBroadcast shutdownserverthread= new ServerControlBroadcast(packet,controlcommand);
//				shutdownserverthread.start();
//				try
//				{
//					shutdownserverthread.join();
//				}
//				catch (Exception e)
//				{
//					System.out.println("shutdownserverthread exception"+e);
//				}
//			} 
//			catch (IOException e) 
//			{
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
            boolean finished =  MasterRunReceiver.analysisFinished();
			
			loop = !finished;
			
			if (finished)
			{
				for (int i=0;i<threadpool_count;i++)
				{
					try {
						threadpool[i].join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		
		
		
		System.out.println("All Mission Complete");
		// TODO Auto-generated method stub
		
	}
	
	class WorkerIP
	{
		public String IP;
		public int portNum;
		
		public WorkerIP(String IP, int portNum)
		{
			this.IP = IP;
			this.portNum = portNum; 
		}
		
		public boolean equals(WorkerIP IP1)
		{
			return IP.equals(IP1.IP)&&(this.portNum==IP1.portNum);
		}
	}

}
