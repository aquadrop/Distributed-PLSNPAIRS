package rmi;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.zip.*;
public class FileStreamReceiverThread implements Runnable
{
	Socket socket = null;
	DataInputStream input = null;
	DataOutputStream output = null;
	public FileStreamReceiverThread(Socket socket)
	{
		this.socket = socket;
		try {
			input = new 
					DataInputStream (new BufferedInputStream(socket.getInputStream()));
			output = new 
					DataOutputStream (new BufferedOutputStream(socket.getOutputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public synchronized void run()
	{
		
		//receiveSimpleFolder();
		String path = Information.resultPath;
		File initial = new File(path);
		//System.out.println(path+initial.isDirectory());
		receiveFolder(initial);
		System.out.println("All Transmit Mission Complete");
		
	}

	
	protected void receiveFolder(File dest) //dest to be full relative path
	{
		try
		{
			if (!input.readBoolean())
			{
//				String dest_name = input.readUTF();
//				File destFile = new File(dest_name);
	           // destFile =dest;
				//System.out.println(dest +" is a directory");
				if (!dest.exists())
				{
					dest.mkdir();
					System.out.println("Create Directory "+dest.toString());
				}
				int number = input.readInt();//read sub directory file number
				//System.out.println(number);
				for (int i = 0; i<number; i++)
				{
					String filename = input.readUTF();
					System.out.println(filename);
					File file = new File(dest,filename);//filename contains full directory
					receiveFolder(file);
				}
			}
			else 
			{
				byte []b = new byte[1024];
				int n=-1;
				
				//start accepting files.
//				
			    System.out.println("Receiving File: "+ dest);
			    FileOutputStream fos = new FileOutputStream (dest);
			    long filesize = input.readLong();
			    while((filesize > 0) && ((n = input.read(b, 0, (int)Math.min(b.length, filesize)))!=-1))
				{
			    	fos.write(b,0,n);
			    	fos.flush();
				    filesize -= n;
				}
//			    fos.close();
				System.out.println(dest+" Received");
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	protected void receiveSimpleFolder()
	{
		String path = Information.resultPath;
		 
		try
		{
//			DataInputStream input = new 
//					DataInputStream (new BufferedInputStream(socket.getInputStream()));
//			DataOutputStream output = new 
//					DataOutputStream (new BufferedOutputStream(socket.getOutputStream()));
			//accept file number
			int number = input.readInt();
			ArrayList<File> filenames = new ArrayList<File>(number);
			//accept file names
			for (int i=0; i<number; i++)
			{
				File file = new File (input.readUTF());
				filenames.add(file);
				//System.out.println("File Name: "+ file);
			}
			
			byte []b = new byte[1024];
			int n=-1;
			
			//start accepting files.
//			synchronized(socket)
//			{
				for (int i=0; i<filenames.size(); i++)
			    {
					System.out.println("Receiving File: "+ filenames.get(i).getName());
				    FileOutputStream fos = new FileOutputStream (path+filenames.get(i).getName());
				    long filesize = input.readLong();
				    while((filesize > 0) && ((n = input.read(b, 0, (int)Math.min(b.length, filesize)))!=-1))
			        {
				    	fos.write(b,0,n);
					    fos.flush();
					    filesize -= n;
			        }
//				    fos.close();
			    }
			    output.close();
//			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
