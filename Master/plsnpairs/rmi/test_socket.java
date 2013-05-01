package rmi;
import java.io.IOException;
import java.net.*;
import java.io.*;

public class test_socket {

	
	public static void main(String[] argv) throws UnknownHostException, IOException
	{
		while(true)
		{
			try
			{
				Socket socket1 = new Socket("192.168.0.101",8889);
			
				try
			
				{	
					DataOutputStream out =  new DataOutputStream(socket1.getOutputStream());
					out.writeUTF("client");
				}
				catch(Exception e)
				{
					System.out.println(e.toString());
				}
				try
				{
					DataInputStream in =  new DataInputStream(socket1.getInputStream());
					String reply = in.readUTF();
					System.out.println(reply);
				}
				catch(Exception e)
				{
					System.out.println(e.toString());
				}
			}
			catch(Exception e)
			{
				System.out.println(e.toString());
			}
		}
	}
}
