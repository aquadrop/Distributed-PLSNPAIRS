package rmi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import npairs.NpairsjSetupParams;
import npairs.io.*;
public class NpairsDataLoaderContainer implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6803474075621282894L;
	public String signature = "Container for ndl";
	public static NpairsDataLoader ndl;
    public static boolean avoid_load_sign = false;
    public static NpairsjSetupParams nsp;
   
    
//    private void translate()
//    {
//    	ByteArrayOutputStream outbyte = new ByteArrayOutputStream();
//    	try {
//			outobject = new ObjectOutputStream(outbyte);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
////    	try {
////			outobject.writeObject(npairsj);
////		} catch (IOException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		}
//    	try {
//			writeObject(outobject);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//    }
//
//    private synchronized void writeObject(java.io.ObjectOutputStream stream) throws java.
//    io.IOException 
//    {
//    	System.out.println("Hi");
//    	stream.defaultWriteObject();
//    	
//    	stream.writeObject(npairsj); 
//    }
//    
//    private synchronized void readObject (java.io.ObjectInputStream stream) throws java.io.IOException, ClassNotFoundException
//    {
//    	stream.defaultReadObject();
//    	stream.readObject();
//    }
}
