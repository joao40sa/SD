import java.rmi.*;
import java.rmi.server.*;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerRMI extends UnicastRemoteObject implements ServerRMI_Interface{

	private int REG_PORT = 7000;
	private ArrayList<Estudante> estudantes;
	private ArrayList<Docente> docentes;
	private ArrayList<Funcionario> funcionarios;
	private ArrayList<Eleicao> eleicoes;

	public ServerRMI() throws RemoteException{
		super();
		estudantes = new ArrayList<Estudante>();
		docentes = new ArrayList<Docente>();
		funcionarios = new ArrayList<Funcionario>();
		eleicoes = new ArrayList<Eleicao>();
	}

	/*MÉTODOS REMOTOS*/
	
	public void print_on_ServerRMI(String s) throws java.rmi.RemoteException	{
		System.out.println(s);
	}

	public boolean registarEstudante(Estudante p) throws java.rmi.RemoteException	{
		boolean add = true;
		for(int i=0; i<estudantes.size(); i++)	{
			if(estudantes.get(i).getNumero() == p.getNumero())
			{
				System.out.println("[ERRO]  ESTUDANTE COM NUM: " + p.getNumero() + " JA SE ENCONTRA REGISTADO");
				add = false;
				break;
			}
		}

		if(add)	{
			estudantes.add(p);
			System.out.println("ESTUDANTE COM NUM: " + p.getNumero() + " REGISTADO SUCESSO");
		}
		return add;
	}

	public boolean registarDocente(Docente p) throws java.rmi.RemoteException	{
		boolean add = true;

		for(int i=0; i<docentes.size(); i++){
			if(docentes.get(i).getNumero() == p.getNumero())
			{
				System.out.println("[ERRO]  DOCENTE COM NUM: " + p.getNumero() + " JA SE ENCONTRA REGISTADO");
				add = false;
				break;
			}
		}

		if(add)	{
			docentes.add(p);
			System.out.println("DOCENTE COM NUM: " + p.getNumero() + " REGISTADO SUCESSO");
		}
		return add;
	}

	public boolean registarFuncionario(Funcionario p) throws java.rmi.RemoteException	{
		boolean add = true;

		for(int i=0; i<funcionarios.size(); i++) {
			if(funcionarios.get(i).getNumero() == p.getNumero())
			{
				System.out.println("[ERRO]  FUNCIONARIO COM NUM: " + p.getNumero() + " JA SE ENCONTRA REGISTADO");
				add = false;
				break;
			}
		}

		if(add)	{
			funcionarios.add(p);
			System.out.println("FUNCIONARIO COM NUM: " + p.getNumero() + " REGISTADO SUCESSO");
		}
		return add;
	}

	public boolean registarEleicao(Eleicao e) throws java.rmi.RemoteException   {
		boolean add = true;

		for(int i=0; i<eleicoes.size(); i++){
			if(eleicoes.get(i).getTitulo()==e.getTitulo()){
				System.out.println("[ERRO]  JA EXISTE UMA ELEICAO CRIADA COM O TITULO: " + e.getTitulo());
				add = false;
				break;
			}
		}

		if(add){
			eleicoes.add(e);
			System.out.println("ELEICAO COM O TITULO: "+ e.getTitulo() + " CRIADA COM SUCESSO");
		}
		return add;
	}

	public boolean adicionaCandidatura(String nomeEleicao, ListaCandidatos lista) throws java.rmi.RemoteException	{//FALTA MELHORAR, é preciso fazer verificação se os candidatos pertencem ao cargo certo
		for(int i=0; i<eleicoes.size(); i++){
			if(eleicoes.get(i).getTitulo() == nomeEleicao)
			{
				return eleicoes.get(i).addListaCandidatos(lista);
			}
		}
		return false;
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
