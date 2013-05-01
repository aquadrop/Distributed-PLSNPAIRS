package rmi;

/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
 
import java.io.*;
import java.net.*;
import java.util.*;
 
public class MulticastWorkerThread extends Thread {
 
    private long FIVE_SECONDS = 5000;
    private String BroadCasterIP;
    private int port;
	private InetAddress group=null;
	private MulticastSocket socket=null;
    public MulticastWorkerThread(String BroadCasterIP) throws IOException
    {
    	this.port = Information.broadcastPort;
    	this.BroadCasterIP=BroadCasterIP;
    	try
    	{
    		group=InetAddress.getByName("239.255.8.0");
        	socket=new MulticastSocket(port);
        	socket.setTimeToLive(1);
        	socket.joinGroup(group);
    	}
    	catch (Exception e)
    	{
    		System.out.println("Error: "+e);
    	}
    	
    	
    }
    public void run() {
    	boolean loop = true;
    	System.out.println("Start Broadcasting");
        while (loop = !(PrivateMasterListener.broadcastkiller)) 
        {
        	
            try 
            {
            	DatagramPacket packet = null;
            	byte data[]=BroadCasterIP.getBytes();
            	packet= new DatagramPacket(data,data.length,group,port);
            	System.out.println("BroadCasting: "+new String(data));
            	
            	socket.send(packet);
            // sleep for a while
                try 
                {
                	sleep((long)(Math.random() * FIVE_SECONDS));
                } catch (InterruptedException e) { }
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
                loop = false;
            }
        }
        if (loop ==false)
        	System.out.println("Broadcasting Stopped");
    socket.close();
    }
}
