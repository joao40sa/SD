import java.rmi.*;
import java.rmi.server.*;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.SimpleDateFormat;  
import java.util.Date; 
import java.text.ParseException;

public class AdminConsole extends UnicastRemoteObject implements AdminConsole_Interface{

	private int REG_PORT = 7000;

	public AdminConsole() throws RemoteException{
		super();
	}
	
	/*MÉTODOS REMOTOS*/

	public void print_on_AdminConsole(String s) throws RemoteException{
		System.out.println(s);  
	}
	
	/*===============*/

	public static void registarEleitor(ServerRMI_Interface server)	{
		String nome, password, departamento, faculdade, contacto, morada, validade_cc, funcao, tipo = null;
		int numero, opt;
		InputStreamReader input = new InputStreamReader(System.in);
		BufferedReader reader = new BufferedReader(input);

		try{
			System.out.print("[1]  ALUNO\n[2]  DOCENTE\n[3]  FUNCIONARIO\nTIPO: ");
			opt = Integer.parseInt(reader.readLine());
			System.out.print("NOME: ");
			nome = reader.readLine();
			System.out.print("CODIGO: ");
			password = reader.readLine();
			System.out.print("FACULDADE: ");
			faculdade = reader.readLine();
			System.out.print("DEPARTAMENTO: ");
			departamento = reader.readLine();
			System.out.print("NUMERO: ");
			numero = Integer.parseInt(reader.readLine());
			System.out.print("CONTACTO: ");
			contacto = reader.readLine();
			System.out.print("MORADA: ");
			morada = reader.readLine();
			System.out.print("VALIDADE CC: ");
			validade_cc= reader.readLine();

			switch(opt){
				case 1:
					tipo = "ESTUDANTE";
					break;
				case 2:
					tipo = "DOCENTE";
					break;
				case 3:
					tipo = "FUNCIONARIO";
					break;
			}
			if(server.registarPessoa(new Pessoa(nome, password, departamento, faculdade, contacto, morada, numero, validade_cc,tipo))){
				System.out.println("\nREGISTO COM SUCESSO");
			}
			else{
				System.out.println("\n[ERRO NO REGISTO]  JA EXISTE UM REGISTO DE PESSOA COM O NUMERO: " +numero);
			}
		}catch(IOException e1)		{
			System.out.println(e1);
		}
	}

	public static void criarEleicao(ServerRMI_Interface server) {
		String s_data_inicio, s_data_fim, titulo, descricao, restPessoa = null, restDep = null, existRest, existRestPess, existRestDep;
		int escolhaEleitor;
		Date data_inicio, data_fim;
		Eleicao e;
		InputStreamReader input = new InputStreamReader(System.in);
		BufferedReader reader = new BufferedReader(input);
		SimpleDateFormat formatter=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		
		try{
			System.out.print("TITULO: ");
			titulo = reader.readLine();
			System.out.print("DESCRICAO: ");
			descricao = reader.readLine();
			System.out.print("DATA INICIO [dd-mm-aaaa hh:mm:ss]: ");
			s_data_inicio = reader.readLine();
			data_inicio = formatter.parse(s_data_inicio);
			System.out.print("DATA FIM [dd-mm-aaaa hh:mm:ss]: ");
			s_data_fim = reader.readLine();
			data_fim = formatter.parse(s_data_fim);
			System.out.print("EXISTE ALGUMA RESTRICAO [s/n]? ");
			existRest = reader.readLine();
			if(existRest.matches("s")){
				System.out.print("EXISTE RESTRICAO QUANTO AO DEPARTAMENTO [s/n]? ");
				existRestDep = reader.readLine();
				if(existRestDep.matches("s")){
					System.out.print("DEPARTAMENTO ONDE IRA OCORRER A ELEICAO: ");
					restDep = reader.readLine();
				}
				System.out.print("EXISTE RESTRICAO QUANTO AO TIPO DE ELEITOR [s/n]? ");
				existRestPess = reader.readLine();
				if(existRestDep.matches("s")){
					System.out.print("TIPO DE ELEITOR [1-ESTUDANTE][2-DOCENTE][3-FUNCIONARIO]: ");
					escolhaEleitor = Integer.parseInt(reader.readLine());
					switch(escolhaEleitor){
						case 1:
							restPessoa = "ESTUDANTE";
							break;
						case 2:
							restPessoa = "DOCENTE";
							break;
						case 3:
							restPessoa = "FUNCIONARIO";
							break;
					}
				}
			}

			if(server.registarEleicao(new Eleicao(titulo, descricao, data_inicio, data_fim, restPessoa, restDep))){
				System.out.println("\nELEICAO CRIADA COM SUCESSO");
			}
			else{
				System.out.println("\n[ERRO AO CRIAR ELEICAO]  JA EXISTE UMA ELEICAO COM O TITULO: " + titulo);
			}

		} catch(ParseException p1){
			System.out.println(p1);
		} catch(IOException e2)		{
			System.out.println(e2);
		}
	}

	public static void main(String args[]) {

		/* This might be necessary if you ever need to download classes:
		System.getProperties().put("java.security.policy", "policy.all");
		System.setSecurityManager(new RMISecurityManager());
		*/

		try {

			ServerRMI_Interface server = (ServerRMI_Interface) LocateRegistry.getRegistry(7000).lookup("ServerRMI");

			String ligado = "NOVO ADMINISTRADOR CONECTADO";
			server.print_on_ServerRMI(ligado);

			
			//REALIZAR OPERAÇÔES
			int option = 1;
			InputStreamReader input = new InputStreamReader(System.in);
			BufferedReader reader = new BufferedReader(input);
			while(option != 0)
			{
				try{
					System.out.println("====================OPERACOES DISPONIVEIS====================\n");
					System.out.println("[0]  SAIR");
					System.out.println("[1]  REGISTAR ELEITOR");
					System.out.println("[2]  CRIAR ELEICAO");
					System.out.println("[3]  GERIR LISTAS DE CANDIDATOS");
					System.out.println("[4]  GERIR MESAS DE VOTO");
					System.out.println("[5]  ALTERAR PROPRIEDADES DE UMA ELEICAO");
					System.out.println("[6]  ONDE VOTOU O ELEITOR");
					System.out.println("[7]  ESTADO DAS MESAS DE VOTO");
					System.out.println("[8]  MOSTRAR ELEITORES EM TEMPO REAL");
					System.out.println("[9]  TERMINAR ELEICAO");
					System.out.println("[10] RESULTADOS");
					System.out.print("\nINSIRA O ID DA OPERACAO A EXECUTAR: ");
					option = Integer.parseInt(reader.readLine());
					switch(option){
						case 0:
							server.print_on_ServerRMI("ADMINISTRADOR DESCONECTADO");
							break;
						case 1:
							System.out.println("\n=============REGISTAR ELEITOR=============\n");
							registarEleitor(server);
							System.out.println("\n==========================================\n");
							break;
						case 2:
							System.out.println("\n==============CRIAR ELEICAO===============\n");
							criarEleicao(server);
							System.out.println("\n==========================================\n");
							break;
						case 3:
							System.out.println("\n========GERIR LISTAS DE CANDIDATOS========\n");
							
							System.out.println("\n==========================================\n");
							break;
						case 4:
							System.out.println("\n===========GERIR MESAS DE VOTO============\n");
							
							System.out.println("\n==========================================\n");
							break;
						case 5:
							System.out.println("\n===ALTERAR PROPRIEDADES DE UMA ELEICAO====\n");
							
							System.out.println("\n==========================================\n");
							break;
						case 6:
							System.out.println("\n===========ONDE VOTOU O ELEITOR===========\n");
							
							System.out.println("\n==========================================\n");
							break;
						case 7:
							System.out.println("\n=========ESTADO DAS MESAS DE VOTO=========\n");
							
							System.out.println("\n==========================================\n");
							break;
						case 8:
							System.out.println("\n=====MOSTRAR ELEITORES EM TEMPO REAL======\n");
							
							System.out.println("\n==========================================\n");
							break;
						case 9:
							System.out.println("\n============TERMINAR ELEICAO==============\n");
							
							System.out.println("\n==========================================\n");
							break;
						case 10:
							System.out.println("\n===============RESULTADOS=================\n");
							
							System.out.println("\n==========================================\n");
							break;

					}
				}catch(IOException e){
					System.out.println(e);
				}
			}

		} catch (Exception e) {
			System.out.println("Exception in main: " + e);
			e.printStackTrace();
		}

	}

}