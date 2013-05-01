package rmi;

import java.io.IOException;
import java.net.Socket;

public class TestMasterMain
{
	public static void main(String []argv) throws IOException
	{
		Socket sock = new Socket("127.0.0.1",13267);
		System.out.println("Connecting...");
		Information.resultPath = "./Results/2-2-4/";
		Thread filereceiverthread = new Thread (new FileStreamReceiverThread(sock));
		filereceiverthread.start();
		try {
			filereceiverthread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Complete");
	}
	 
	 
	 

}
