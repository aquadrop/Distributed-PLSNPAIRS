package rmi;
import pls.*;
import pls.analysis.NpairsAnalysis;

public class MainInterfaceImp extends java.rmi.server.UnicastRemoteObject implements MainInterface
{
	public MainInterfaceImp() throws java.rmi.RemoteException{};
	public void run(String[] argv, int start, int end) throws Exception 
	{
		Main SERVER = new Main();
//		System.out.println("Called");
//		System.out.println(argv[0]);
		SERVER.remoteRun(argv, start, end);
		//return NpairsAnalysis.container;
//		System.out.println(start);
//		System.out.println(end);
//		System.out.println("result:"+Integer.toString(start-end));
	}
}


