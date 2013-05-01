package rmi;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class GetLocalIP 
{
	public static String getLocalIP() throws SocketException
	{
		Enumeration netInterfaces=NetworkInterface.getNetworkInterfaces();
		InetAddress ip=null;
		InetAddress ia=null;
	    //NetworkInterface ni = NetworkInterface.getByName("wlan0");
	    //System.out.println("O");
		while(netInterfaces.hasMoreElements())
		{
			NetworkInterface ni = (NetworkInterface)netInterfaces.nextElement();
			Enumeration<InetAddress> inetAddresses =  ni.getInetAddresses();
            
			if (ni.getName().contains("lo"))
				continue;

            while(inetAddresses.hasMoreElements()) 
            {
            	ia = inetAddresses.nextElement();
                if(!ia.isLinkLocalAddress()) 
                {
                	
                	//System.out.println("IP: " + ia.getHostAddress());
                	return ia.getHostAddress();
                }
            }
		}
		return null;
		
	}
}
