import java.rmi.*;
import java.rmi.server.*;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerRMI extends UnicastRemoteObject implements ServerRMI_Interface{

	private int REG_PORT = 7000;
	private ArrayList<Eleicao> eleicoes;
	private ArrayList<Pessoa> eleitores;

	public ServerRMI() throws RemoteException{
		super();
		eleicoes = new ArrayList<Eleicao>();
		eleitores = new ArrayList<Pessoa>();
	}

	/*MÉTODOS REMOTOS*/
	
	public void print_on_ServerRMI(String s) throws java.rmi.RemoteException	{
		System.out.println(s);
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
			System.out.println(e);
		}
		return add;
	}

	public boolean registarPessoa(Pessoa p) throws java.rmi.RemoteException{
		boolean add = true;
		for(int i=0; i<eleitores.size(); i++)	{
			if(eleitores.get(i).getNumero() == p.getNumero())
			{
				System.out.println("[ERRO]  PESSOA COM NUM: " + p.getNumero() + " JA SE ENCONTRA REGISTADA");
				add = false;
				break;
			}
		}

		if(add)	{
			eleitores.add(p);
			System.out.println("PESSOA COM NUM: " + p.getNumero() + " REGISTADA COM SUCESSO");
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