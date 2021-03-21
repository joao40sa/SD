import java.rmi.*;
import java.rmi.server.*;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.SimpleDateFormat;
import java.text.ParseException;

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
			System.out.println("[NOVA ELEICAO] TITULO: "+e.getTitulo());
			//System.out.println(e);
		}
		return add;
	}

	public boolean alterarEleicao(String nomeEleicao, String newNome, String newDescricao, Date newDataInicio, Date newDataFim) throws java.rmi.RemoteException{
		int indEleicao = -1;
		Date dataAtual;
		/*
		Descobrir se existe a eleicao que pretendemos alterar dados
		Se já existe uma eleicao com o novo nome que queremos dar não podemos alterar a eleicao
		*/
		for(int i=0; i<eleicoes.size(); i++){
			if(eleicoes.get(i).getTitulo().equals(nomeEleicao)){
				indEleicao = i;
			}
			if(eleicoes.get(i).getTitulo() == newNome)
				return false;
		}

		/*
		Se existe a eleicao a alterar fazer o check das datas
		*/
		if(indEleicao != -1){
			dataAtual = Calendar.getInstance().getTime();
			if(eleicoes.get(indEleicao).getDataInicio().compareTo(dataAtual) < 0){
				return false;
			}
			else{
				if(newNome != null)
					eleicoes.get(indEleicao).setTitulo(newNome);
				if(newDescricao != null)
					eleicoes.get(indEleicao).setDescricao(newDescricao);
				if(newDataInicio != null)
					eleicoes.get(indEleicao).setDataInicio(newDataInicio);
				if(newDataFim != null)
					eleicoes.get(indEleicao).setDataFim(newDataFim);
			}
			System.out.println("[ELEICAO ALTERADA] "+eleicoes.get(indEleicao));
		}
		else{
			System.out.println("[ERRO ALTERAR ELEICAO] NAO EXISTE ELEICAO " + nomeEleicao);
			return false;
		}

		return true;
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
			System.out.println("[NOVO REGISTO] "+p.getTipo()+" COM NUMERO: " +p.getNumero());
		}
		return add;
	}

	public boolean adicionaCandidatura(String nomeEleicao, ListaCandidatos lista) throws java.rmi.RemoteException	{//FALTA MELHORAR, é preciso fazer verificação se os candidatos pertencem ao cargo certo
		for(int i=0; i<eleicoes.size(); i++){
			if(eleicoes.get(i).getTitulo().equals(nomeEleicao))
			{
				return eleicoes.get(i).addListaCandidatos(lista);
			}
		}
		return false;
	}
	/*=====================*/
	private Date getDataAtual(){
		try{
			SimpleDateFormat formatter=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			Date today = Calendar.getInstance().getTime();
			String s_data;
		    s_data = formatter.format(today);
		    today = formatter.parse(s_data);
		    //System.out.println(today);

		    return today;
		} catch(ParseException p1){
			System.out.println(p1);
		}
		return null;
	}

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