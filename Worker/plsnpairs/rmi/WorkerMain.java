package rmi;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Enumeration;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import pls.*;

public class WorkerMain
{
	public static void main(String[] argv) throws Exception
	{
		try
		{
//			String registry_name = "localhost";
//			if(argv.length>=1)
//			{
//				registry_name = argv[1];
//			}
//			InetAddress localhost=null;
//			try
//			{
//				 localhost = InetAddress.getLocalHost();
//				
//			}
//			catch (UnknownHostException e)
//			{
//				System.out.println(e.toString());
//			}
			
			
            Information.argv = argv;
            String address = GetLocalIP.getLocalIP();//"10.0.1.43";//localhost.toString();
            Information.workerIP = address;
            System.out.println("IP selected: "+address);
            
           
            
			int broadcastport = Information.broadcastPort;
			int rmiport = Information.rmiPort;
            String broadcastregistration = "Worker"+":"+address+":"+(Integer.toString(broadcastport));
            String rmiregistration = "rmi://"+address+":"+(Integer.toString(rmiport))+"/rmiImp";
            
            //BroadCastIP(registration);
            
			LocateRegistry.createRegistry(rmiport);
			//System.out.println("HI");
			System.setProperty("java.rmi.server.hostname",address);
			MainInterfaceImp worker =  new MainInterfaceImp();
			
//			String []commandline={"NPAIRS","RUN_ANALYSIS", "./SetupFiles/2-2-4.mat"};
//			server.run(commandline,0,1);
			
			Naming.rebind(rmiregistration, worker);
			System.out.println("Worker Starts");
			StartMasterListeningandBroadcasting(broadcastregistration);
			//Naming.unbind(registration);
//			
//			Main m1= new Main();
//			m1.remoteRun(commandline, 1,2);
//			server.remoteRun(commandline, 1,2);
		}
//		
		catch(RemoteException e)
		{
			System.out.println("Connection Failed");
			System.out.println(e.toString());
		}
		catch(Exception e)
		{
			System.out.println(e.toString());
		}
	}
	
	private static void StartMasterListeningandBroadcasting(String registration) throws IOException
	{
        Thread listenerthread = new Thread(new PrivateMasterListener(registration));
        listenerthread.start();
	}
}
