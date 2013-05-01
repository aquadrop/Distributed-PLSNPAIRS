package rmi;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TestWorkerMain
{
	public static void main(String []argv) throws IOException
	{
		Information.resultPath = "./Results/2-2-4/";
		ServerSocket serversocket = new ServerSocket(13267);
		System.out.println("Waiting");
		Socket socket = serversocket.accept();
		Thread serverthread = new Thread(new FileStreamThread (socket));
		serverthread.start();
		try {
			serverthread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
