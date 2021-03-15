import java.rmi.*;
import java.rmi.server.*;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

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

	private registarEleitor()
	{
		Pessoa p;
		String nome, password, departamento, faculdade, contacto, morada, validade_cc;
		int numero;
		InputStreamReader input = new InputStreamReader(System.in);
		BufferedReader reader = new BufferedReader(input);

		System.out.print("NOME: ");
		nome = reader.readLine();
		System.out.print("CÓDIGO: ");
		password = reader.readLine();
		System.out.print("FACULDADE: ");
		faculdade = reader.readLine();
		System.out.print("DEPARTAMENTO: ");
		departamento = reader.readerdLine();
		System.out.print("NÚMERO: ");
		numero = Integer.parseInt(reader.readLine());
		System.out.print("CONTACTO: ");
		contacto = reader.readLine();
		System.out.print("MORADA: ");
		morada = reader.readLine();
		System.out.print("VALIDADE CC: ");
		validade_cc= reader.readLine();

		p = new Pessoa(nome, password, departamento, faculdade, contacto, morada, numero, validade_cc);
	}

	public static void main(String args[]) {

		/* This might be necessary if you ever need to download classes:
		System.getProperties().put("java.security.policy", "policy.all");
		System.setSecurityManager(new RMISecurityManager());
		*/

		try {

			ServerRMI_Interface server = (ServerRMI_Interface) LocateRegistry.getRegistry(7000).lookup("ServerRMI");

			String ligado = "New AdminConsole created.";
			server.print_on_ServerRMI(ligado);

			
			//REALIZAR OPERAÇÔES
			int option = 1;
			InputStreamReader input = new InputStreamReader(System.in);
			BufferedReader reader = new BufferedReader(input);
			while(option != 0)
			{
				System.out.println("[0]  SAIR\n[1]  REGISTAR ELEITOR\n[2]  CRIAR ELEIÇÃO");
				option = Integer.parseInt(reader.readLine());
				switch(option){
					case 1:
						System.out.println("\n=============REGISTAR ELEITOR=============\n");
						registarEleitor();
						System.out.println("\n==========================================\n");
						break;
					case 2:
						break;
				}
			}

		} catch (Exception e) {
			System.out.println("Exception in main: " + e);
			e.printStackTrace();
		}

	}

}