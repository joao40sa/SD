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

//Imports FILE===============================

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
//============================================

public class ServerRMI extends UnicastRemoteObject implements ServerRMI_Interface{

	private int REG_PORT = 7000;
	private static int UDP_PORT = 6000;
	private ArrayList<Eleicao> eleicoes;
	private ArrayList<Pessoa> eleitores;
	private static ServerRMI server;
	private static Registry reg;

	public ServerRMI() throws RemoteException{
		super();
		eleicoes = new ArrayList<Eleicao>();
		File f = new File("database.obj");
		File f1 = new File("databaseEleicoes.obj");
		if(f.exists() && !f.isDirectory()) {
			System.out.println("[DADOS CARREGADOS]  COMUNIDADE ACADEMICA"); 
			eleitores = readFile();
		}
		else{
			eleitores = new ArrayList<Pessoa>();
		}
		if(f1.exists() && !f1.isDirectory()) {
			System.out.println("[DADOS CARREGADOS ELEICOES]"); 
			eleicoes = readFileEleicao();
		}
		else{
			eleicoes = new ArrayList<Eleicao>();
		}
	}

	public void writeToFile(int param){ //eleitores = 0     eleições = 1
		System.out.println("################# WRITE ###################");						
		try{
			if(param == 0){
				FileOutputStream writeData = new FileOutputStream("database.obj");
				ObjectOutputStream writeStream = new ObjectOutputStream(writeData);

				writeStream.writeObject(eleitores);
				writeStream.flush();
				writeStream.close();
			}
			if(param == 1){
				FileOutputStream writeData = new FileOutputStream("databaseEleicoes.obj");
				ObjectOutputStream writeStream = new ObjectOutputStream(writeData);

				writeStream.writeObject(eleicoes);
				writeStream.flush();
				writeStream.close();
			}

        }catch (IOException e) {
            e.printStackTrace();
        } 
	}

	public ArrayList<Pessoa> readFile(){
		//System.out.println("################# READ ###################");
		try{
			FileInputStream readData = new FileInputStream("database.obj");
            ObjectInputStream readStream = new ObjectInputStream(readData);

            ArrayList<Pessoa> people2 = (ArrayList<Pessoa>) readStream.readObject();
            readStream.close();

            //System.out.println(people2.toString());
			return (people2);
			
		
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("Error initializing stream");
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		return null;
	}
	//Ler ficheiro de eleicoes ==========================================

	public ArrayList<Eleicao> readFileEleicao(){
		//System.out.println("################# READ ###################");
		try{
			FileInputStream readData = new FileInputStream("databaseEleicoes.obj");
            ObjectInputStream readStream = new ObjectInputStream(readData);

            ArrayList<Eleicao> el = (ArrayList<Eleicao>) readStream.readObject();
            readStream.close();

            System.out.println(el.toString());
			return (el);
			
		
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("Error initializing stream");
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		return null;
	}

	//=====================================================================

	/*MÉTODOS REMOTOS*/

	public void print_on_ServerRMI(String s) throws java.rmi.RemoteException{
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
			writeToFile(1);
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
			System.out.println("[NOVO REGISTO] "+p.getTipo()+" COM NUMERO: " + p.getNumero());
			writeToFile(0);
		}
		return add;
	}

	public boolean adicionaCandidatura(String nomeEleicao, String nomeCandidatura, ArrayList<Integer> lista) throws java.rmi.RemoteException	{//FALTA MELHORAR, é preciso fazer verificação se os candidatos pertencem ao cargo certo
		ArrayList<Pessoa> candidatos = new ArrayList<Pessoa>();
		ListaCandidatos candidatura;
		int indEleicao = -1;
		boolean found = false;
		String restricao;
		for(int i=0; i<eleicoes.size(); i++){
			if(eleicoes.get(i).getTitulo().equals(nomeEleicao)){
				indEleicao = i;
				break;
			}
		}

		if(indEleicao == -1){
			return false;
		}

		restricao = eleicoes.get(indEleicao).getRestricaoTipo();
		//para cada elemento da lista verificamos se ele está registado e se o seu tipo corresponde com a restricao se esta existir
		for(int i=0; i<lista.size(); i++){
			found = false;
			for(int j=0; j<eleitores.size(); j++){
				if(lista.get(i) == eleitores.get(j).getNumero()){ //se o elemento da lista estiver registado
					if((restricao != null) && (eleitores.get(j).getTipo().equals(restricao))){ //se satisfazer os requisitos é adicionado
						candidatos.add(eleitores.get(j));
						found = true;
						break;
					}
					else{
						return false;
					}
				}
			}
			if(!found)
				return false;
		}

		return eleicoes.get(indEleicao).addListaCandidatos(new ListaCandidatos(nomeCandidatura, candidatos));
	}

	public boolean removeCandidatura(String nomeEleicao, String nomeCandidatura) throws java.rmi.RemoteException{
		for(int i=0; i<eleicoes.size(); i++){
			if(eleicoes.get(i).getTitulo().equals(nomeEleicao)){
				return eleicoes.get(i).removeListaCandidatos(nomeCandidatura); 
			}
		}
		return false;
	}


	/*=====================*/


	private static void primarySide(){
		DatagramSocket aSocket = null;
		String s;
		try{
			aSocket = new DatagramSocket(UDP_PORT);
			//System.out.println("[CHECK CONNECTION]SERVER PRIMARIO na escuta no porto " + UDP_PORT);
			while(true){
				byte[] buffer = new byte[1000]; 
				byte[] m = 	"Yup".getBytes();	

				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);
				s = new String(request.getData(), 0, request.getLength());	
				//System.out.println("[CHECK CONNECTION]Server PRIMARIO Recebeu: " + s);	

				DatagramPacket reply = new DatagramPacket(m, 
														  m.length, 
														  request.getAddress(), 
														  request.getPort());
				aSocket.send(reply);
			}
		}catch (SocketException e){
			System.out.println("Socket: " + e.getMessage());
		}catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		}finally {
			if(aSocket != null) 
				aSocket.close();
		}
	}

	private static void secundarySide(String address){
		int timeouts = 0;
		DatagramSocket aSocket = null;
		String s;
		aSocket = null;
		try {
			aSocket = new DatagramSocket();    

			while(timeouts<5){		  
				byte [] m = "Alive?".getBytes();
				
				InetAddress aHost = InetAddress.getByName(address);
				int serverPort = UDP_PORT;		                                                
				DatagramPacket request = new DatagramPacket(m,m.length,aHost,serverPort);
				aSocket.send(request);			                        
				byte[] buffer = new byte[1000];
				DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
				
				aSocket.setSoTimeout(3000);//ex 3.2 definir timeout para receber mensagens até 10 seg 

				try{
					aSocket.receive(reply);
					s = new String(reply.getData(), 0, reply.getLength());
					timeouts = 0;
					//System.out.println("Recebeu: " + s);
				}catch (IOException e){
					System.out.println("TIMEOUT: " + e.getMessage());
					timeouts++;
				}
			} // while
		}catch (SocketException e){
			System.out.println("Socket: " + e.getMessage());
		}catch (IOException e){
			System.out.println("IO: " + e.getMessage());
		}
		finally {
			if(aSocket != null) 
				aSocket.close();
		}
	}















	public static void main(String args[]) {
		boolean isPrimary = false;
		try {
			server = new ServerRMI();
			reg = LocateRegistry.createRegistry(7000);
			reg.rebind("ServerRMI", server);
			System.out.println("ServerRMI ready as primary server.");
			isPrimary = true;
		} catch (RemoteException re) {
			System.out.println("ServerRMI ready as secundary server.");
		}
		//SE ELE FOR PRIMARIO VAI FICAR A ESCUTA E RESPONDE AS MENSAGENS DO SERVIDOR SECUNDARIO
		while(true){
			if(isPrimary){
				primarySide();
			}

			else{
				secundarySide(args[0]);
			}

			try {
				reg = LocateRegistry.createRegistry(7000);
				reg.rebind("ServerRMI", server);
				System.out.println("ServerRMI ready as primary server.");
				isPrimary = true;
			} catch (RemoteException re) {
				System.out.println("ServerRMI ready as secundary server.");
			}
		}
	}

}
