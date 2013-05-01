package rmi;

import java.io.IOException;
import java.net.*;


public class WorkerControlBroadcast extends Thread
{
	private DatagramSocket socket = null;
    //protected BufferedReader in = null;
    private boolean moreQuotes = true;
    private DatagramPacket controlpacket;
    private String controlIP;
    private String control;
//    public ServerShutdownBroadcast() throws IOException {
//    this("QuoteServerThread");
//    }
 
    public WorkerControlBroadcast(DatagramPacket controlpacket,String control) throws IOException {
        //super(shutdownIP);
        socket = new DatagramSocket(8888);
        this.controlpacket=controlpacket;
        this.control=control;
        this.controlIP=new String(controlpacket.getData(),"rmi://".length(),controlpacket.getLength()-":8888/rmiImp".length());
    }
 
    public void run() {
 
        try 
        {
            byte[] buf = control.getBytes();
 
                // receive request
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            
 
            // send the response to the client at "address" and "port"
            InetAddress address = InetAddress.getByName(controlIP);
            int port = 8888;//packet.getPort();
            packet = new DatagramPacket(buf, buf.length, address, port);
            socket.send(packet);
            } 
        catch (IOException e) 
        {
            e.printStackTrace();
            moreQuotes = false;
        }
        
        socket.close();
    }
 
//    protected String getNextQuote() {
//        String returnValue = null;
//        try {
//            if ((returnValue = in.readLine()) == null) {
//                in.close();
//        moreQuotes = false;
//                returnValue = "No more quotes. Goodbye.";
//            }
//        } catch (IOException e) {
//            returnValue = "IOException occurred in server.";
//        }
//        return returnValue;
//    }
}


