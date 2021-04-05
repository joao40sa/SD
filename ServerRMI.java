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
import java.util.Date;


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
	private static ArrayList<Eleicao> eleicoes;
	private static ArrayList<Pessoa> eleitores;
	private static ServerRMI server;
	private ArrayList<String> mesasVotoAbertas; //são as mesas que já estão abertas, no maximo existe uma por departamento
	private static String[] departamentos = {"DEI", "DEEC", "DEM", "DEC", "DEQ"};
	private String[][] gruposMulticastDepartamento = {   {"225.0.0.0","225.0.0.1"},
														 {"226.0.0.0","226.0.0.1"},
														 {"227.0.0.0","227.0.0.1"},
													     {"228.0.0.0","228.0.0.1"},
														 {"229.0.0.0","229.0.0.1"}}; 
	private static Registry reg;

	public static void loadDatabase(){
		File f = new File("database.obj");
		File f1 = new File("databaseEleicoes.obj");	
		if(f.exists() && !f.isDirectory()) {
			eleitores = readFile();
			System.out.println("[DADOS CARREGADOS]  COMUNIDADE ACADEMICA"); 
		}
		else{
			eleitores = new ArrayList<Pessoa>();
		}
		if(f1.exists() && !f1.isDirectory()) {
			eleicoes = readFileEleicao();
			System.out.println("[DADOS CARREGADOS]  ELEICOES"); 
		}
		else{
			eleicoes = new ArrayList<Eleicao>();
		}
	}

	public ServerRMI() throws RemoteException{
		super();
		loadDatabase();
		mesasVotoAbertas = new ArrayList<String>();
	}

	public static void writeToFile(int param){ //eleitores = 0     eleições = 1
		//System.out.println("################# WRITE ###################");						
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

	public static ArrayList<Pessoa> readFile(){
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

	public static ArrayList<Eleicao> readFileEleicao(){
		//System.out.println("################# READ ###################");
		try{
			FileInputStream readData = new FileInputStream("databaseEleicoes.obj");
            ObjectInputStream readStream = new ObjectInputStream(readData);

            ArrayList<Eleicao> el = (ArrayList<Eleicao>) readStream.readObject();
            readStream.close();

            //System.out.println(el.toString());
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
	public ArrayList<String> getGruposMulticast(String departamentoMesa) throws java.rmi.RemoteException {
		//devolve quais os grupos multicast respetivos a uma determinada mesa de voto
		int ind = 0;
		ArrayList<String> grupos = new ArrayList<String>();
		for(int i=0; i<departamentos.length; i++){
			if(departamentos[i].equals(departamentoMesa)){
				ind = i;
				break;
			}
		}
		grupos.add(gruposMulticastDepartamento[ind][0]);
		grupos.add(gruposMulticastDepartamento[ind][1]);
		return grupos;
	}


	public boolean abreMesaVoto(String departamentoMesa) throws java.rmi.RemoteException {
		//abre uma mesa de voto(apenas existem 5 disponiveis ate ao momento)
		//se já existe uma mesa de voto no departamento não é possivel abri uma nova
		if(existeMesa(departamentoMesa)){
			for(int i=0; i<mesasVotoAbertas.size(); i++){
				if(mesasVotoAbertas.get(i).equals(departamentoMesa)){
					System.out.println("[ERRO ABRIR MESA] MESA "+departamentoMesa+" JA SE ENCONTRA ABERTA");
					return false;
				}
			}
			//se não exisitr abrimos a mesa
			System.out.println("MESA "+departamentoMesa+" ABERTA COM SUCESSO");
			mesasVotoAbertas.add(departamentoMesa);
			return true;
		}
		else{
			System.out.println("[ERRO ABRIR MESA] MESA "+departamentoMesa+" NAO EXISTE");
			return false;
		}
		
	}

	public boolean adicionaMesa(String nomeEleicao, String nomeMesa) throws java.rmi.RemoteException {
		//adiciona uma mesa a uma eleicao
		if(existeMesa(nomeMesa)){
			for(int i=0; i<eleicoes.size(); i++){
				if(eleicoes.get(i).getTitulo().equals(nomeEleicao)){
					if(eleicoes.get(i).adicionaMesa(nomeMesa)){
						System.out.println("[SUCESSO ASSOCIAR MESA] MESA "+nomeMesa+" ASSOCIADA COM SUCESSO A ElEICAO "+nomeEleicao);
						writeToFile(1);
						return true;
					}
					else{
						System.out.println("[ERRO ASSOCIAR MESA] MESA "+nomeMesa+" NAO SE ENCONTRA ASSOCIADA NA ElEICAO "+nomeEleicao);
						return false;
					}
				}
			}
			System.out.println("[ERRO ASSOCIAR MESA] ELEICAO "+nomeEleicao+" NAO EXISTE");
			return false;
		}
		else{
			System.out.println("[ERRO ASSOCIAR MESA] MESA "+nomeMesa+" NAO EXISTE");
			return false;
		}
	}

	public boolean removeMesa(String nomeEleicao, String nomeMesa) throws java.rmi.RemoteException{
		//remove uma mesa de uma eleicao
		if(existeMesa(nomeMesa)){
			for(int i=0; i<eleicoes.size(); i++){
				if(eleicoes.get(i).getTitulo().equals(nomeEleicao)){
					if(eleicoes.get(i).removeMesa(nomeMesa)){
						System.out.println("[SUCESSO REMOVER MESA] MESA "+nomeMesa+" REMOVIDA COM SUCESSO A ElEICAO "+nomeEleicao);
						writeToFile(1);
						return true;
					}
					else{
						System.out.println("[ERRO REMOVER MESA] MESA "+nomeMesa+" NAO SE ENCONTRA ASSOCIADA NA ElEICAO "+nomeEleicao);
						return false;
					}
				}
			}
			System.out.println("[ERRO REMOVER MESA] ELEICAO "+nomeEleicao+" NAO EXISTE");
			return false;
		}
		else{
			System.out.println("[ERRO REMOVER MESA] MESA "+nomeMesa+" NAO EXISTE");
			return false;
		}
	}

	public ArrayList<String> getMesasVotoAbertas() throws java.rmi.RemoteException{
		//devolve quais as mesas abertas
		return mesasVotoAbertas;
	}



	public ArrayList<ArrayList<String>> getVotosCadaMesa(String eleicao) throws java.rmi.RemoteException{
		//devolve quantas pessoas ja votaram ate ao momento nas mesas que se encontram abertas 
		ArrayList<ArrayList<String>> historicoVotos;
		ArrayList<ArrayList<String>> votosCadaMesa = new ArrayList<ArrayList<String>>();



		for(int i = 0; i < mesasVotoAbertas.size(); i++){
			int contadorVotos = 0;
			for(int j = 0; j < eleitores.size(); j++){
				historicoVotos = eleitores.get(j).getHistoricoVotos();

				for(int k = 0; k < historicoVotos.size(); k++){
					if(historicoVotos.get(k).get(0).equals(eleicao) && historicoVotos.get(k).get(1).equals(mesasVotoAbertas.get(i))){
						contadorVotos++;
					}
				}
			}
			System.out.println(contadorVotos);
			ArrayList<String> conjunto = new ArrayList<String>(); //PAR eleicao e local onde votou
			conjunto.add(mesasVotoAbertas.get(i));
			conjunto.add(""+contadorVotos);
			votosCadaMesa.add(conjunto);
		}

		return votosCadaMesa;





	}

	public ArrayList<Pessoa> getEleitoresOnline() throws java.rmi.RemoteException{
		//devolve quais os eleitores online(que estao num terminal de voto)
		ArrayList<Pessoa> eleitoresOnline = new ArrayList<Pessoa>();
		for(int i=0; i<eleitores.size(); i++){
			if(eleitores.get(i).getEstado())
				eleitoresOnline.add(eleitores.get(i));
		}
		return eleitoresOnline;
	}

	public void print_on_ServerRMI(String s) throws java.rmi.RemoteException{
		System.out.println(s);
	}

	public boolean registarEleicao(Eleicao e) throws java.rmi.RemoteException   {
		//regista uma eleicao
		//nome da eleicao e unico
		boolean add = true;
		System.out.println("Entrou no server registar eleicao");
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
		//altera dados de uma eleicao se esta ainda nao comecou
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
			writeToFile(1);
		}
		else{
			System.out.println("[ERRO ALTERAR ELEICAO] NAO EXISTE ELEICAO " + nomeEleicao);
			return false;
		}

		return true;
	}

	public boolean registarPessoa(Pessoa p) throws java.rmi.RemoteException{
		//regista uma pessoa
		//numero é o identificador unico de uma pessoa logo nao pode haver repeticoes
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
		//adiciona uma lista nas candidaturas de uma eleica
		//so pode haver uma lista com um determinado nome e nao pode haver uma pessoa em dois cargos diferentes
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

		if(eleicoes.get(indEleicao).addListaCandidatos(new ListaCandidatos(nomeCandidatura, candidatos))){
			System.out.println("CANDIDATURA ADICIONA COM SUCESSO");
			writeToFile(1);
			return true;
		}
		else{
			return false;
		}
	}

	public boolean removeCandidatura(String nomeEleicao, String nomeCandidatura) throws java.rmi.RemoteException{
		//remove uma lista das candidaturas a uma eleicao
		for(int i=0; i<eleicoes.size(); i++){
			if(eleicoes.get(i).getTitulo().equals(nomeEleicao)){
				if(eleicoes.get(i).removeListaCandidatos(nomeCandidatura)){
					System.out.println("CANDIDATURA REMOVIDA COM SUCESSO");
					writeToFile(1);
					return true;
				}
				else{
					return false;
				}
			}
		}
		return false;
	}

	public boolean identificarEleitor(int numero) throws java.rmi.RemoteException{
		//verifica se existe algum eleitor com o numero passado por parametro
		for(int i=0; i<eleitores.size(); i++){
			if(eleitores.get(i).getNumero() == numero){
				System.out.println("ELEITOR "+numero+" IDENTIFICADO");
				return true;
			}
		}
		return false;
	}


	public ArrayList<Eleicao> eleicoesAtivas(int num) throws java.rmi.RemoteException{
		//devolve todas as eleicoes que estao a decorrer no exato momento e na qual o eleitor pode votar
		//para poder votar o eleitor deve corresponder a todos os requisitos da eleicao
		Date dataAtual = Calendar.getInstance().getTime();
		ArrayList<Eleicao> eleicoesAtuais = new ArrayList<Eleicao>();
		Pessoa p = null;
		int indiceEleitor = -1;


		for(int i = 0; i < eleitores.size(); i++){
			if(eleitores.get(i).getNumero() == num){
				p = eleitores.get(i);
				indiceEleitor = i;
				break;
			}
		}
		
		for(int i = 0; i < eleicoes.size(); i++){
			if(dataAtual.compareTo(eleicoes.get(i).getDataInicio()) > 0 && dataAtual.compareTo(eleicoes.get(i).getDataFim()) < 0) {
				if(eleicoes.get(i).getRestricaoTipo().equals(p.getTipo())){
					if(eleicoes.get(i).getRestricaoDep() == null || eleicoes.get(i).getRestricaoDep().equals(p.getDepartamento())){
						eleicoesAtuais.add(eleicoes.get(i));
					}
				}
				
				//eleicoesAtuais.get(i).toString();
			}
		}

		//se a lista de eleicoes em que pode votar estiver vazia o eleitor sai porque nao pode votar em nada
		if(eleicoesAtuais.size()<=0){
			eleitores.get(indiceEleitor).setEstado(false);
		}

		return eleicoesAtuais;
	}


	//login
	public boolean verificaLogin(int num, String pass) throws java.rmi.RemoteException{
		//para ser valido o eleitor devera estar offline antes de efetuar o login e as credenciais devem estar corretas
		for(int i=0; i<eleitores.size(); i++){
			if(eleitores.get(i).getNumero() == num){
				if(eleitores.get(i).getPassword().equals(pass)){
					if(eleitores.get(i).getEstado() == false){
						eleitores.get(i).setEstado(true); //mete o eleitor online
						return true;
					}
					else{
						return false;
					}
				}
			}
		}
		return false;
		
	}


	public ArrayList<String> getListaCandidatos(String nomeEleicao, int numeroEleitor) throws java.rmi.RemoteException{
		//devolve todas as listas candidatas a uma eleicao
		ArrayList<String> lista = new ArrayList<String>();
		ArrayList<ListaCandidatos> listasCandi = null;
		for(int i = 0; i < eleicoes.size(); i++){
			if(eleicoes.get(i).getTitulo().equals(nomeEleicao)){
				listasCandi = eleicoes.get(i).getListasCandidatos();
				break;
			}
		}

		for(int j = 0; j < listasCandi.size(); j++){
			lista.add(listasCandi.get(j).getNome());
		}


		if(lista.size()<=0){
			for(int i=0; i<eleitores.size(); i++){
				if(eleitores.get(i).getNumero() == numeroEleitor){
					eleitores.get(i).setEstado(false);
					break;
				}
			}
		}
		return lista;
	}


	public boolean processaVoto(String eleicaoEscolhida, String listaEscolhida, int numEleitor, String mesa) throws java.rmi.RemoteException{
		//para o voto ser valido o eleitor ainda não deverá ter votado na eleicao antes
		int indiceEleitor = 0; //guardar indice eleitor, depois de percorrer todos os eleitores
		for(int i=0; i<eleitores.size(); i++){
			if(eleitores.get(i).getNumero() == numEleitor){
				indiceEleitor = i;
				if(eleitores.get(i).adicionaEleicaoVotada(eleicaoEscolhida, mesa)){
					System.out.println("[VOTO] ELEITOR "+numEleitor+" PODE VOTAR NA ELEICAO "+eleicaoEscolhida);
					break;
				}
				else{
					eleitores.get(indiceEleitor).setEstado(false);
					return false;
				}
			}
		}

		eleitores.get(indiceEleitor).setEstado(false);
		writeToFile(0);

		for(int i=0; i<eleicoes.size(); i++){
			if(eleicoes.get(i).getTitulo().equals(eleicaoEscolhida)){
				if(eleicoes.get(i).adicionaVoto(listaEscolhida)){
					System.out.println("[VOTO] ELEITOR "+numEleitor+" VOTOU COM SUCESSO NA ELEICAO "+eleicaoEscolhida);
					writeToFile(1);
					return true;
				}
				else{
					writeToFile(1);
					return false;
				}
			}
		}
		return false;
	}


	public ArrayList<ArrayList<String>> getResultados(String eleicao) throws java.rmi.RemoteException{
		//devolve os resultados da eleicao se esta já acabou 
		//no formato [[total de votos,200]; [brancos,2]; [nulos, 10];[listaA, 1];[listaB, 4]; ...]
		Date dataAtual = Calendar.getInstance().getTime();

		ArrayList<ArrayList<String>> resultados = new ArrayList<ArrayList<String>>();
		ArrayList<String> conjunto = new ArrayList<String>();

		ArrayList<ListaCandidatos> listacandidatos;


		for(int i=0; i<eleicoes.size(); i++){
			if(eleicoes.get(i).getTitulo().equals(eleicao)){
				if(eleicoes.get(i).getEstado() == false || dataAtual.compareTo(eleicoes.get(i).getDataFim()) > 0){
					eleicoes.get(i).setEstado(false);

					//[[total de votos,200]; [brancos,2]; [nulos, 10];[listaA, 1];[listaB, 4]; ...]
					conjunto.add("Total");
					conjunto.add(""+eleicoes.get(i).getTotalVotos());
					resultados.add(conjunto);
					
					conjunto = new ArrayList<String>();
					conjunto.add("Brancos");
					conjunto.add(""+eleicoes.get(i).getVotosBrancos());
					resultados.add(conjunto);

					conjunto = new ArrayList<String>();
					conjunto.add("Nulos");
					conjunto.add(""+eleicoes.get(i).getVotosNulos());
					resultados.add(conjunto);

					listacandidatos = eleicoes.get(i).getListasCandidatos();

					for(int j = 0; j < listacandidatos.size(); j++){

						conjunto = new ArrayList<String>();
						conjunto.add(listacandidatos.get(j).getNome());
						conjunto.add(""+listacandidatos.get(j).getVotos());
						resultados.add(conjunto);
					}

					return resultados;
				}
				else{
					return resultados;
					//retorna array vazio->eleicao ainda a decorrer
				}


			}
		}
		return null; // nao existe nenhuma eleicao com aquele nome
	}


	public ArrayList<ArrayList<String>> getHistoricoVotos(int numEleitor) throws java.rmi.RemoteException{
		//devolve todas as eleicoes em que o eleitor votou e os respetivos locais
		for(int i=0; i<eleitores.size(); i++){
			if(eleitores.get(i).getNumero() == numEleitor){
				return eleitores.get(i).getHistoricoVotos();
			}
		}
		return null;
	}
	/*=====================*/


	private static void primarySide(){//responde aos heartbeats do server secundário
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

	private static void secundarySide(String address){//pergunta se o server principal esta vivio e espera no maximo 3 seg pela resposta
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
				
				aSocket.setSoTimeout(3000);

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




	private static boolean existeMesa(String nomeMesa){
		for(int i=0; i<departamentos.length; i++){
			if(departamentos[i].equals(nomeMesa))
				return true;
		}
		return false;
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
				loadDatabase();
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