import java.rmi.*;

public interface ServerRMI_Interface extends Remote {
	
	public void print_on_ServerRMI(String s) throws java.rmi.RemoteException;

	public boolean registarEstudante(Estudante p) throws java.rmi.RemoteException;
	public boolean registarDocente(Docente p) throws java.rmi.RemoteException;
	public boolean registarFuncionario(Funcionario p) throws java.rmi.RemoteException;

	public boolean registarEleicao(Eleicao e) throws java.rmi.RemoteException;

	public boolean adicionaCandidatura(String nomeEleicao, ArrayList<Pessoa> lista) throws java.rmi.RemoteException;
}