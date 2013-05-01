package rmi;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import pls.analysis.NpairsAnalysis;

public class WorkerStart //extends Thread
{
	public final int analysisNum;
	public final String []argv;
	public WorkerStart(String[] argv, int analysisNum)
	{
		this.analysisNum=analysisNum;
		this.argv=argv;
	}
	
	//int server=getServerNum();
	public void run()
	{
		String []rmistringgroup=getRMIStringGroup();//{"192.168.0.100","192.168.0.101"};
        //	String PORT="8888";
     	//MainInterface []remote_worker=null;
	    for (int i=0;i<rmistringgroup.length;i++)
	    {
	    	String rmistring=rmistringgroup[i];
		    String IP= rmistring.substring("rmi://".length(),rmistring.length()-":8888/rmiImp".length());
   //		int firstremoteIdx = first+i*bundle;
//		    int lastremoteIdx = Math.min(first+bundle+i*bundle,last);
		    //InetAddress IP=InetAddress.getByName(IPname);
		    System.setProperty("java.rmi.server.hostname", IP);
		    
		    MainInterface remote_worker=null;
		
	        try 
	        {
				remote_worker=(MainInterface) Naming.lookup(rmistring);
			} 
	        catch (MalformedURLException e1) 
	        {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
	        catch (RemoteException e1) 
	        {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
	        catch (NotBoundException e1) 
	        {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        System.out.println("Server Connected!"+IP);
	        int analysisIdx=getAnalysisIdx();
	        try 
	        {
				remote_worker.run(argv,analysisIdx,analysisIdx);
			} 
	        catch (RemoteException e) 
	        {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
	        catch (Exception e) 
	        {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
         }
	}
	 private String[] getRMIStringGroup()
	 {
		 String []rmistringgroup = null;
		 return rmistringgroup;
	 }
	 
	 private int getAnalysisIdx()
	 {
		 int analysisIdx=0;
		 return analysisIdx;
	 }
	

}
