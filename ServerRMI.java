import java.rmi.*;
import java.rmi.server.*;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerRMI extends UnicastRemoteObject implements ServerRMI_Interface{

	private int REG_PORT = 7000;

	public ServerRMI() throws RemoteException{
		super();
	}

	/*MÉTODOS REMOTOS*/
	
	public void print_on_ServerRMI(String s) throws java.rmi.RemoteException
	{
		System.out.println(s);
	}


	/*=====================*/


	public static void main(String args[]) {
		try {
			ServerRMI server = new ServerRMI();
			Registry reg = LocateRegistry.createRegistry(7000);
			reg.rebind("ServerRMI", server);
			System.out.println("ServerRMI ready.");
		} catch (RemoteException re) {
			System.out.println("Exception in HelloImpl.main: " + re);
		}
	}

}