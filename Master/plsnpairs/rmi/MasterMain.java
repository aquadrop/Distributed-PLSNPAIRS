package rmi;

import java.net.SocketException;
import pls.*;
public class MasterMain 
{
	
	public static void main(String[] argv) throws SocketException
	{
		boolean mastermode = true;
		long start = System.currentTimeMillis();
		Main plsmain = new Main(mastermode);
        try {
			Main.run(argv);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		System.out.println("********Exit**********");
		System.out.println("Mission Time: "+ (end-start)/1000+" seconds");
	}
	
	
}
