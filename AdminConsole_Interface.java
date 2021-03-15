import java.rmi.*;

public interface AdminConsole_Interface extends Remote {
	
	public void print_on_AdminConsole(String s) throws java.rmi.RemoteException;

}