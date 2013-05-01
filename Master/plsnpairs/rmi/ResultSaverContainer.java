package rmi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import npairs.*;
public class ResultSaverContainer implements Serializable ,ResultSubmitInterface
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6803474075621282894L;
	public String signature = "Results to be Submitted to Client";
	public transient Npairsj npairsj;
	//public ByteArrayOutputStream outbyte = null;
	public ObjectOutputStream outobject;
	public String npairsSetupParamsMatFileName; 
    public String nsp_resultsFilePrefix;
    
    public ResultSaverContainer(Npairsj npairsj, String npairsSetupParamsMatFileName,String nsp_resultsFilePrefix)
    {
    	this.npairsj=npairsj;
    	this.npairsSetupParamsMatFileName = npairsSetupParamsMatFileName;
    	this.nsp_resultsFilePrefix = nsp_resultsFilePrefix;
    	translate();
    }
    
    private void translate()
    {
    	ByteArrayOutputStream outbyte = new ByteArrayOutputStream();
    	try {
			outobject = new ObjectOutputStream(outbyte);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//    	try {
//			outobject.writeObject(npairsj);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
    	try {
			writeObject(outobject);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    private synchronized void writeObject(java.io.ObjectOutputStream stream) throws java.
    io.IOException 
    {
    	stream.defaultWriteObject(  ); 
    	stream.writeObject(npairsj); 
    }
    
    private synchronized void readObject (java.io.ObjectInputStream stream) throws java.io.IOException, ClassNotFoundException
    {
    	stream.defaultReadObject();
    	stream.readObject();
    }
}
