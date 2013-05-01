package rmi;
import java.io.*;
import java.net.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
public class FileStreamThread implements Runnable
{
	
	ServerSocket serversocket;
	Socket socket = null;
	public FileStreamThread(Socket t)	
	{
		this.socket = t;
		//this.filenames = filenames;
		
	}
	
	public void run()
	{
//		ArrayList<File> filenames;
//	    filenames = ListResultFilenames();
//	    transmitSimpleFolder (filenames);
		File src = new File(Information.resultPath);
		try {
			transmitFolder(src);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	//recursive transmit
	protected void transmitFolder(File src) throws IOException//src to be full relative path
	{
		NumberFormat format = NumberFormat.getPercentInstance();
		format.setMinimumFractionDigits(3);
		format.setMaximumFractionDigits(3);
		boolean isFile = true;
		FileInputStream reader = null;
		BufferedInputStream input = null;
		DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream())); 	
		if (src.isDirectory())
		{
			System.out.println(src+" is Directory");
			isFile = false;
			out.writeBoolean(isFile);
			out.flush();
			String []files = src.list();
			//write file number
			out.writeInt(files.length);
			out.flush();
			
			
			for (String file :files)
			{
				System.out.println(file);
			}
			
			for (String file : files)
			{
				File srcFile = new File(src,file);
				//recursive copy
				//write src name
				out.writeUTF(file);
				out.flush();
				transmitFolder (srcFile);
			}
		}
		else
		{
			
//			//write Filename
//			out.writeUTF(src.toString());
//			out.flush();
			isFile = true;
			//write isFile
			out.writeBoolean(isFile);
			out.flush();
			reader = new FileInputStream(src);
			input = new BufferedInputStream(reader);
			long length = src.length();
			//write File Length
			out.writeLong(length);
		    out.flush();
			byte b[] = new byte[1024];
			int n=-1;
			double complete = 0;
			int printcount = 0;
			//transmit file content
			while ((n=input.read(b, 0, 1024))!=-1)
			{
				
			    out.write(b, 0, n);
			    
				complete = complete+n;
				if (length>50E6)
				{
					printcount++;
			    	if (printcount==(int)(length/10240))
			    	{
			    		System.out.println(format.format(complete/length)+" Completed");
					    printcount = 0;
				    }
				}
			}
			out.flush();
			System.out.println("****** "+src.toString()+" Transmit Complete***");
		}
	}
	
	protected void transmitSimpleFolder (ArrayList<File> filenames)
	{
		NumberFormat format = NumberFormat.getPercentInstance();
		format.setMinimumFractionDigits(3);
		format.setMaximumFractionDigits(3);
		try 
		{
			
		    
		    FileInputStream reader = null;
		    
		    DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
		    
		    //report number of files in the folder
			out.writeInt(filenames.size());
		    out.flush();
		    
		    //report file names in the folder
		    for (int i = 0; i<filenames.size(); i++)
		    {
		    	out.writeUTF(filenames.get(i).getName());
		    	out.flush();
		    	//System.out.println("File list:" +filenames.get(i).getName());
		    }
		    
		    //Transmitting Results
		    for (int i=0; i<filenames.size(); i++)
		    {
		    	String filename = filenames.get(i).getName();
		        System.out.println("Trasmitting File: "+ filename);
			    filename = getRelativeFilename(filename);
				File f = new File(filename);
				long length = f.length();
				//report file size
				{
				    out.writeLong(length);
				    out.flush();
				}
				reader = new FileInputStream(filename);
				BufferedInputStream input = new BufferedInputStream(reader);
				byte b[] = new byte[1024];
				int n=-1;
				double complete = 0;
				int printcount = 0;
				while ((n=input.read(b, 0, 1024))!=-1)
				{
					printcount++;
				    out.write(b, 0, n);
				    
					complete = complete+n;
					if (printcount==(int)(length/10240))
					{
						System.out.println("Transmit Complete: "+format.format(complete/length));
						printcount = 0;
					}
				}
				out.flush();
				System.out.println("Transmit Complete");
		    }
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	protected ArrayList<File> ListResultFilenames()
	{
		String path = Information.resultPath;
		File folder = new File(path);
		File []listofFiles = folder.listFiles();
		int num = listofFiles.length;
		ArrayList<File> resultfilenames = new ArrayList<File>(num);
		
		
		for (int i=0; i<num;i++)
		{
			resultfilenames.add(listofFiles[i]);
		}
		return resultfilenames;
	}
    
	protected String getRelativeFilename(String filename)
	{
		String relativefilename;
		String path = Information.resultPath;
		relativefilename = path+filename;
		return relativefilename;
	}
}
