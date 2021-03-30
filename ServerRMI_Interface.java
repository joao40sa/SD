import java.rmi.*;
import java.util.ArrayList;
import java.util.*;

public interface ServerRMI_Interface extends Remote {
	
	public void print_on_ServerRMI(String s) throws java.rmi.RemoteException;

	public boolean registarPessoa(Pessoa p) throws java.rmi.RemoteException;
	
	public boolean registarEleicao(Eleicao e) throws java.rmi.RemoteException;
	public boolean alterarEleicao(String nomeEleicao, String newNome, String newDescricao, Date newDataInicio, Date newDataFim) throws java.rmi.RemoteException;

	public boolean adicionaCandidatura(String nomeEleicao, String nomeCandidatura, ArrayList<Integer> lista) throws java.rmi.RemoteException;
	public boolean removeCandidatura(String nomeEleicao, String nomeCandidatura) throws java.rmi.RemoteException;

	public boolean abreMesaVoto(String departamentoMesa) throws java.rmi.RemoteException;
	public ArrayList<String> getGruposMulticast(String departamentoMesa) throws java.rmi.RemoteException; 

	public boolean adicionaMesa(String nomeEleicao, String nomeMesa) throws java.rmi.RemoteException;
	public boolean removeMesa(String nomeEleicao, String nomeMesa) throws java.rmi.RemoteException;

	public ArrayList<String> getMesasVotoAbertas() throws java.rmi.RemoteException;
	public boolean identificarEleitor(int numero) throws java.rmi.RemoteException;

	public ArrayList<Pessoa> getEleitoresOnline() throws java.rmi.RemoteException;

	public boolean verificaLogin(int num, String pass) throws java.rmi.RemoteException;
	public ArrayList<Eleicao> eleicoesAtivas(int num) throws java.rmi.RemoteException;


	public ArrayList<String> getListaCandidatos(String nomeEleicao, int numeroEleitor) throws java.rmi.RemoteException;

	public boolean processaVoto(String eleicaoEscolhida, String listaEscolhida, int numEleitor, String mesa) throws java.rmi.RemoteException;
	public int getResultados(String eleicao) throws java.rmi.RemoteException;

	public ArrayList<ArrayList<String>> getHistoricoVotos(int numEleitor) throws java.rmi.RemoteException;
}