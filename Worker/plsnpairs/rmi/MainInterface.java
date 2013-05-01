package rmi;
import java.rmi.RemoteException;

public interface MainInterface extends java.rmi.Remote
{
	 public void run(String[] argv, int start, int end) throws RemoteException, Exception;
}

