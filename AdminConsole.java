import java.rmi.*;
import java.rmi.server.*;
import java.net.*;
import java.io.*;
import java.util.*;
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

    private static void registarEleitor(ServerRMI_Interface server) throws java.rmi.ConnectException    {
        String nome, password, departamento, faculdade, contacto, morada, validade_cc, funcao, tipo = null;
        int numero, opt;
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        try{
            System.out.print("[1]  ALUNO\n[2]  DOCENTE\n[3]  FUNCIONARIO\nTIPO: ");
            opt = Integer.parseInt(reader.readLine());
            System.out.print("NOME: ");
            nome = reader.readLine();
            while(nome.contains(";") || nome.contains("|")){
                System.out.print("Nao pode conter caracteres (; , |)\n");
                System.out.print("NOME: ");
                nome = reader.readLine();
            }
            System.out.print("PASSWORD: ");
            password = reader.readLine();
            while(password.contains(";") || password.contains("|")){
                System.out.print("Nao pode conter caracteres (; , |)\n");
                System.out.print("PASSWORD: ");
                password = reader.readLine();
            }
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
        }catch(java.rmi.ConnectException c){
            throw c;
        }catch(IOException e1)      {
            System.out.println(e1);
        }catch(NumberFormatException  e){
            System.out.println("FORMATO INVALIDO NO NUMERO");
        }
    }

    private static void criarEleicao(ServerRMI_Interface server) throws java.rmi.ConnectException {
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
                if(existRestPess.matches("s")){
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

        } catch(java.rmi.ConnectException c){
            throw c;
        } catch(ParseException p1){
            System.out.println(p1);
        } catch(IOException e2)     {
            System.out.println(e2);
        }
    }

    private static void alterarDadosEleicao(ServerRMI_Interface server)  throws java.rmi.ConnectException{
        int auxData;
        String alterar, nomeEleicao, newNome = null, newDescricao = null, s_data_inicio, s_data_fim;
        Date newDataInicio = null, newDataFim = null;
        Date dataAtual = Calendar.getInstance().getTime();
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        SimpleDateFormat formatter=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        
        try{
            System.out.print("TITULO DA ELEICAO A ALTERAR: ");
            nomeEleicao = reader.readLine();

            System.out.print("ALTERAR TITULO DA ELEICAO [s/n]? ");
            alterar = reader.readLine();
            if(alterar.matches("s")){
                System.out.print("NOVO TITULO: ");
                newNome = reader.readLine();
            }

            System.out.print("ALTERAR DESCRICAO DA ELEICAO [s/n]? ");
            alterar = reader.readLine();
            if(alterar.matches("s")){
                System.out.print("NOVA DESCRICAO: ");
                newDescricao = reader.readLine();
            }

            System.out.print("ALTERAR DATA DE INICIO [s/n]? ");
            alterar = reader.readLine();
            if(alterar.matches("s")){
                auxData = -1;
                while(auxData<0){
                    System.out.print("NOVA DATA DE INICIO [dd-mm-aaaa hh:mm:ss]: ");
                    s_data_inicio = reader.readLine();
                    newDataInicio = formatter.parse(s_data_inicio);
                    if(dataAtual.compareTo(newDataInicio) < 0){
                        auxData = 1;
                    }
                    else{
                        System.out.println("DATA INVALIDA: A NOVA DATA DEVE SER POSTERIOR A DATA ATUAL");
                    }
                }
            }

            System.out.print("ALTERAR DATA DE FIM [s/n]? ");
            alterar = reader.readLine();
            if(alterar.matches("s")){
                auxData = -1;
                while(auxData<0){
                    System.out.print("NOVA DATA DE FIM [dd-mm-aaaa hh:mm:ss]: ");
                    s_data_fim = reader.readLine();
                    newDataFim = formatter.parse(s_data_fim);
                    if(dataAtual.compareTo(newDataFim) < 0){
                        auxData = 1;
                    }
                    else{
                        auxData = -1;
                        System.out.println("DATA INVALIDA: A NOVA DATA DEVE SER POSTERIOR A DATA ATUAL");
                    }
                    if(newDataInicio != null && auxData != -1)
                    {
                        if(newDataFim.compareTo(newDataInicio) > 0){
                            auxData = 1;
                        }
                        else{
                            System.out.println("DATA INVALIDA: A NOVA DATA DEVE SER POSTERIOR A DATA DE INICIO");
                            auxData = -1;
                        }
                    }
                }
            }

            if(server.alterarEleicao(nomeEleicao, newNome, newDescricao, newDataInicio, newDataFim)){
                System.out.println("ELEICAO ALTERADA COM SUCESSO");
            }
            else{
                System.out.println("ERRO AO ALTERAR ELEICAO");
            }

        } catch(java.rmi.ConnectException c){
            throw c;
        } catch(IOException e3){
            System.out.println(e3);
        } catch(ParseException p3){
            System.out.println(p3);
        }
    }

    private static void gerirListaCandidatos(ServerRMI_Interface server) throws java.rmi.ConnectException {
        int opt;
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        try{
            System.out.println("[0]  VOLTAR");
            System.out.println("[1]  ADICIONAR LISTA DE CANDIDATOS A UMA ELEICAO");
            System.out.println("[2]  REMOVER LISTA DE CANDIDATOS DE UMA ELEICAO");
            System.out.print("ESCOLHA A OPCAO A REALIZAR: ");
            opt = Integer.parseInt(reader.readLine());
            switch(opt){
                case 0:
                    break;
                case 1:
                    adicionaCandidatura(server);
                    break;
                case 2:
                    eliminaCandidatura(server);
                    break;
            }
        } catch(java.rmi.ConnectException c){
            throw c;
        } catch(NumberFormatException  e){
            System.out.println("OPCAO NAO RECONHECIDA");
        }  catch(IOException  i){
            System.out.println("OPCAO NAO RECONHECIDA");
        }
    }

    private static void adicionaCandidatura(ServerRMI_Interface server) throws java.rmi.ConnectException{
        String nomeCandidatura = null, nomeEleicao = null;
        int numero, n=0, is_ok = -1;
        ArrayList<Integer> candidatos = new ArrayList<Integer>();
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        System.out.print("NOME DA ELEICAO A INSERIR CANDIDATURA: ");
        try{
            nomeEleicao = reader.readLine();
            System.out.print("NOME DA LISTA CANDIDATA: ");
            nomeCandidatura = reader.readLine();
            System.out.println("INSIRA O NUMERO DO RESPETIVO ELEMENTO");
        } catch(IOException e){

        }
        
        
        while(is_ok==-1){
            try{
                System.out.print("PRESIDENTE: ");
                numero = Integer.parseInt(reader.readLine());
                candidatos.add(numero);
                is_ok = 1;
            } catch(NumberFormatException  e){
                System.out.println("VERIFIQUE QUE INTRODUZIU UM NUMERO E TENTE NOVAMENTE");
            } catch(IOException  i){
                System.out.println("OPCAO NAO RECONHECIDA");
            }
        }
        is_ok = -1;
        while(is_ok==-1){
            try{
                System.out.print("VICE-PRESIDENTE: ");
                numero = Integer.parseInt(reader.readLine());
                candidatos.add(numero);
                is_ok = 1;
            } catch(NumberFormatException  e){
                System.out.println("VERIFIQUE QUE INTRODUZIU UM NUMERO E TENTE NOVAMENTE");
            } catch(IOException  i){
                System.out.println("OPCAO NAO RECONHECIDA");
            }

        }
        is_ok = -1;
        while(is_ok==-1){
            try{
                System.out.print("QUANTOS ELEMENTOS FALTAM ADICIOANAR A CANDIDATURA: ");
                n = Integer.parseInt(reader.readLine());
                is_ok = 1;
            } catch(NumberFormatException  e){
                System.out.println("VERIFIQUE QUE INTRODUZIU UM NUMERO E TENTE NOVAMENTE");
            } catch(IOException  i){
                System.out.println("OPCAO NAO RECONHECIDA");
            }
        }
        try{
            if(server.adicionaCandidatura(nomeEleicao, nomeCandidatura, candidatos)){
                System.out.println("\nCANDIDATURA ADICIONADA COM SUCESSO");
            }
            else{
                System.out.println("\nERRO AO PROCESSAR A CANDIDATURA");
            }
        } catch(java.rmi.ConnectException c){
            throw c;
        } catch(RemoteException r){
            System.out.println("RemoteException encontrada");
        }
        
    }

    private static void eliminaCandidatura(ServerRMI_Interface server) throws java.rmi.ConnectException{
        String nomeCandidatura, nomeEleicao;
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        try{
            System.out.print("NOME DA ELEICAO A ALTERAR: ");
            nomeEleicao = reader.readLine();
            System.out.print("NOME DA LISTA A ELIMINAR: ");
            nomeCandidatura = reader.readLine();

            if(server.removeCandidatura(nomeEleicao, nomeCandidatura)){
                System.out.println("CANDIDATURA REMOVIDA COM SUCESSO");
            }
            else{
                System.out.println("ERRO AO REMOVER CANDIDATURA. VERIFQUE QUE A ELEICAO E A CANDIDATURA EXISTEM ANTES DE ELIMINAR");
            }
        } catch(java.rmi.ConnectException c){
            throw c;
        } catch(IOException e){
            System.out.println("ALGUMA COISA CORREU REBENTOU");
        }
    }

    private static void gerirMesasVoto(ServerRMI_Interface server) throws java.rmi.ConnectException{
        String nomeEleicao, nomeMesa;
        int opt;
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        try{
            System.out.print("NOME ELEICAO: ");
            nomeEleicao = reader.readLine();
            System.out.print("NOME MESA [DEPARTAMENTO]: ");
            nomeMesa = reader.readLine();
            System.out.println("INTENCAO:");
            System.out.println("    [1]  ADICIONAR MESA DE VOTO A UMA ELEICAO");
            System.out.println("    [2]  REMOVER MESA DE VOTO DE UMA ELEICAO");
            opt = Integer.parseInt(reader.readLine());
            switch(opt){
                case 1:
                    if(server.adicionaMesa(nomeEleicao, nomeMesa))
                        System.out.println("\nMESA ASSOCIADA COM SUCESSO");
                    else
                        System.out.println("\nERRO AO ASSOCIAR MESA");
                    break;
                case 2:
                    if(server.removeMesa(nomeEleicao, nomeMesa))
                        System.out.println("\nMESA REMOVIDA COM SUCESSO");
                    else
                        System.out.println("\nERRO AO REMOVER MESA");
                    break;
            }

        }catch(java.rmi.ConnectException c){
            throw c;
        }catch(NumberFormatException  e){
            System.out.println("OPCAO NAO RECONHECIDA");
        }catch(IOException  i){
            System.out.println("OPCAO NAO RECONHECIDA");
        }
    }

    private static void mostarEstadoMesasVoto(ServerRMI_Interface server) throws java.rmi.ConnectException{
        try{

            ArrayList<String> mesas = server.getMesasVotoAbertas();
            int tam = mesas.size();
            if(tam>0){
                System.out.println("AS MESAS ABERTAS SAO AS SEGUINTES");
                for(int i=0; i<mesas.size(); i++){
                    System.out.println("    -"+mesas.get(i));
                }
            }
            else{
                System.out.println("NAO SE ENCONTRA NENHUMA MESA ABERTA");
            }
            
        }catch(java.rmi.ConnectException c){
            throw c;
        }catch(IOException  i){
            System.out.println("EXCECAO");
        }
    }

    private static void mostrarEleitoresTempoReal(ServerRMI_Interface server) throws java.rmi.ConnectException{
        try{

            ArrayList<Pessoa> eleitoresOnline = server.getEleitoresOnline();
            int size = eleitoresOnline.size();
            if(size==0){
                System.out.println("SEM ELEiTORES ONLINE");
            }
            else{
                System.out.println("ELEITORES ONLINE: ");
                for(int i=0; i<size; i++){
                    System.out.println("    "+eleitoresOnline.get(i));
                }
            }

        }catch(java.rmi.ConnectException c){
            throw c;
        }catch(IOException  i){
            System.out.println("EXCECAO");
        }
    }

    private static void mostraResultados(ServerRMI_Interface server) throws java.rmi.ConnectException{
        try{
            String nomeEleicao;
            InputStreamReader input = new InputStreamReader(System.in);
            BufferedReader reader = new BufferedReader(input);

            System.out.println("ELEICAO A CONSULTAR:  ");
            nomeEleicao = reader.readLine();

            System.out.println("TOTAL VOTOS: "+server.getResultados(nomeEleicao));

        }catch(java.rmi.ConnectException c){
            throw c;
        }catch(IOException  i){
            System.out.println("EXCECAO");
        }
    }


    private static void mostraLocalDoVoto(ServerRMI_Interface server) throws java.rmi.ConnectException{
        try{
            int numEleitor;
            InputStreamReader input = new InputStreamReader(System.in);
            BufferedReader reader = new BufferedReader(input);

            System.out.println("NUM ELEITOR:  ");
            numEleitor = Integer.parseInt(reader.readLine());

            System.out.println("INFORMACAO: "+server.getHistoricoVotos(numEleitor));

        }catch(java.rmi.ConnectException c){
            throw c;
        }catch(IOException  i){
            System.out.println("EXCECAO");
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
                            gerirListaCandidatos(server);
                            System.out.println("\n==========================================\n");
                            break;
                        case 4:
                            System.out.println("\n===========GERIR MESAS DE VOTO============\n");
                            gerirMesasVoto(server);
                            System.out.println("\n==========================================\n");
                            break;
                        case 5:
                            System.out.println("\n===ALTERAR PROPRIEDADES DE UMA ELEICAO====\n");
                            alterarDadosEleicao(server);
                            System.out.println("\n==========================================\n");
                            break;
                        case 6:
                            System.out.println("\n===========ONDE VOTOU O ELEITOR===========\n");
                            mostraLocalDoVoto(server);
                            System.out.println("\n==========================================\n");
                            break;
                        case 7:
                            System.out.println("\n=========ESTADO DAS MESAS DE VOTO=========\n");
                            mostarEstadoMesasVoto(server);
                            System.out.println("\n==========================================\n");
                            break;
                        case 8:
                            System.out.println("\n=====MOSTRAR ELEITORES EM TEMPO REAL======\n");
                            mostrarEleitoresTempoReal(server);
                            System.out.println("\n==========================================\n");
                            break;
                        case 9:
                            System.out.println("\n============TERMINAR ELEICAO==============\n");
                            
                            System.out.println("\n==========================================\n");
                            break;
                        case 10:
                            System.out.println("\n===============RESULTADOS=================\n");
                            mostraResultados(server);
                            System.out.println("\n==========================================\n");
                            break;

                    }
                }catch(java.rmi.ConnectException c){
                    System.out.println("Something went wrong, try again");
                    server = (ServerRMI_Interface) LocateRegistry.getRegistry(7000).lookup("ServerRMI");
                    server.print_on_ServerRMI(ligado);
                }
                catch(IOException e){
                    System.out.println("fds");
                }
            }

        } catch (Exception e) {
            System.out.println("Exception in main: " + e);
            e.printStackTrace();
        }

    }

}