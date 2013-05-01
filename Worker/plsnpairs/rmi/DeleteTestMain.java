package rmi;

public class DeleteTestMain 
{
	public static void main(String[] argc)
	{
		String path = "./Results/2-2-16-copy/";
		DeleteDirectory delete = new DeleteDirectory(path);
		delete.run();
	}
	
	

}
