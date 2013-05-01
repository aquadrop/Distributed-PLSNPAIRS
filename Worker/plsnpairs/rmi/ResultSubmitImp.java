package rmi;

import java.io.Serializable;
import java.rmi.RemoteException;

public class ResultSubmitImp implements Serializable, ResultSubmitInterface
{
	private String signature = "Server Result to Be Submitted to Master";
	public ResultSubmitImp() throws RemoteException
	{
		super();
	}

}
