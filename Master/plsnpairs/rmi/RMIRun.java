package rmi;
import java.net.MalformedURLException;
import java.rmi.*; 
public class RMIRun 
{
	public String serverIP;
	static int portNum;
	static String serviceName = "rmiImp"; 
	public int firstAnalysis;
	public int lastAnalysis;
	public String[] command;
	
	public RMIRun(String IP, String[] argv, int firstAnalysis, int lastAnalysis)
	{
		this.portNum = Information.rmiPort;
		this.serverIP = IP;
		this.command = argv;
		this.firstAnalysis = firstAnalysis;
		this.lastAnalysis = lastAnalysis;
	}
	
	public boolean runAnalysis() throws Exception
	{
		try
		{
			String registration = "rmi://"+serverIP+":"+String.valueOf(portNum)+"/"+serviceName;
			Remote obj =  Naming.lookup(registration);
			MainInterface remoteObject = (MainInterface) obj;
			remoteObject.run(command, firstAnalysis, lastAnalysis);
			
			//System.out.println("Filename: "+ resultbuffer.npairsSetupParamsMatFileName);
			return true;
					
		}	
		 catch(RemoteException e)
		 {
			 System.out.println(e.toString());
			 return false;
		 }
		
		
		
	}
}
