import java.rmi.*;
import java.util.ArrayList;

public interface ServerRMI_Interface extends Remote {
	
	public void print_on_ServerRMI(String s) throws java.rmi.RemoteException;

	public boolean registarPessoa(Pessoa p) throws java.rmi.RemoteException;
	
	public boolean registarEleicao(Eleicao e) throws java.rmi.RemoteException;

	public boolean adicionaCandidatura(String nomeEleicao, ListaCandidatos lista) throws java.rmi.RemoteException;
}