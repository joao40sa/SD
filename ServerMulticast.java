import java.net.MulticastSocket;
import java.net.Socket;
import java.net.DatagramPacket;
import java.net.InetAddress;
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

class ServerMulticast extends Thread{

	private static String MULTICAST_DESCOBRIR;
    private static String MULTICAST_COMUNICAR;
    private static int PORT_DESCOBRIR = 4321;
    private static int PORT_COMUNICAR = 1234;
    private static String departamento; //departamento onde se encontra a mesa
    private static ServerRMI_Interface server;

    public ServerMulticast(String departamento, String grupoDescobrir, String grupoComunicar) {
        super("User " + (long) (Math.random() * 1000));
        this.departamento = departamento;
        this.MULTICAST_DESCOBRIR = grupoDescobrir;
        this.MULTICAST_COMUNICAR = grupoComunicar;
    }

    public static void doSomething(){
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        int opt;
        try{
            

            while(true){
                try{
                    identificarEleitor();

                }catch(Exception e){
                }
            }

        }catch(Exception e){

        }finally{
        }
        
    }


    

    public static void identificarEleitor(){
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        int numero;
        try{

            System.out.print("NUMERO DE IDENTIFICACAO: ");
            numero = Integer.parseInt(reader.readLine());
            try{
                if(server.identificarEleitor(numero)){
                    System.out.println("\nIDENTIFICACAO CONCLUIDA COM SUCESSO\n");
                    desbloqueiaTerminal();
                }
            }catch(java.rmi.ConnectException c){
                connectToRMI();
            
                if(server.identificarEleitor(numero)){
                    System.out.println("\nIDENTIFICACAO CONCLUIDA COM SUCESSO\n");
                    desbloqueiaTerminal();
                }
            }


        }catch(IOException e){
            System.out.println("\nFORMATO DE DADOS INVALIDOS\n");
        }
    }

    public static void desbloqueiaTerminal(){
        MulticastSocket socket = null;
        String estado, terminal = null;
        String message;
        String[] tokens;
        String[] pares;
        boolean loop = true; 
        try {
            socket = new MulticastSocket(PORT_DESCOBRIR);
            InetAddress group = InetAddress.getByName(MULTICAST_DESCOBRIR);
            socket.joinGroup(group);

            socket.setSoTimeout(3000);

            byte[] buffer = "type|getEstado;".getBytes();

            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT_DESCOBRIR);
            socket.send(packet);

            
            while(loop){
                buffer = new byte[256];
                packet = new DatagramPacket(buffer, buffer.length);
                try{
                    socket.receive(packet);

                    message = new String(packet.getData(), 0, packet.getLength());
                    tokens = message.split(";");
                    pares = tokens[0].split("\\|");

                    if(pares[1].equals("sendEstado")){
                        pares = tokens[1].split("\\|");
                        estado = pares[1];
                        
                        if(estado.equals("LIVRE")){
                            pares = tokens[2].split("\\|");
                            terminal = pares[1];

                            //System.out.println("MANDA BLOQUEAR "+terminal);
                            message = "type|setEstado;estado|OCUPADO;target|"+terminal+";group|"+MULTICAST_COMUNICAR;
                            buffer = message.getBytes();

                            packet = new DatagramPacket(buffer, buffer.length, group, PORT_DESCOBRIR);
                            socket.send(packet);

                            loop = false;
                        }
                    }
                } catch(IOException e){
                    System.out.println("SEM TERMINAIS DISPONIVEIS");
                    loop = false;
                }
            }          
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }

    public static void connectToRMI() throws java.rmi.ConnectException, RemoteException{
        
        try{
            server = (ServerRMI_Interface) LocateRegistry.getRegistry(7000).lookup("ServerRMI");
        }catch(java.rmi.ConnectException c){
            System.out.println("SERVIDORES DOWN");
        }catch(NotBoundException ne){

        }
    }

    public static void main(String[] args) {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        ArrayList<String> gruposMulticast;

        try{
            server = (ServerRMI_Interface) LocateRegistry.getRegistry(7000).lookup("ServerRMI");
            System.out.print("LOCALIZACAO DA MESA: ");
            departamento = reader.readLine();
            if(server.abreMesaVoto(departamento)){
                gruposMulticast = server.getGruposMulticast(departamento);
                
                ServerMulticast mesa = new ServerMulticast(departamento, gruposMulticast.get(0), gruposMulticast.get(1));

                mesa.start();// thread que vai comunicar com os terminais com eleitores la
                doSomething();
            }
            else{
                System.out.println("[IMPOSSIVEL ABRIR MESA DE VOTO]");
            }
        } catch(Exception e){
            System.out.println("Exception no main: "+e);
        }
        
    }

    public void run() {
        MulticastSocket socket = null;
        String[] tokens; //parsing da mensagem
        String[] pares; //parsing da mensagem
        String nomeEleicao = null; //eleicao a processar recebida por mensagem
        String listaEscolhida = null; //lista em que o eleitor pretende votar
        int num; //numero do eleitor que enviou a mensagem(usada no login e no voto)
        String pass = null; //pass do eleitor(para login)
        int idTerminal; //id do terminal com que estamos a comunicar

        try {
            socket = new MulticastSocket(PORT_COMUNICAR);  // create socket without binding it (only for sending)
            InetAddress group = InetAddress.getByName(MULTICAST_COMUNICAR);
            socket.joinGroup(group);
            
            
            while (true) {

                byte[] buffer = new byte[256];

                
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                try { 
                    sleep((long) (Math.random() * 5000)); //5seg
                    socket.setSoTimeout(2000);


                    //Recebe informações do terminal
                    socket.receive(packet);
                    String message = new String(packet.getData(), 0, packet.getLength());
                    


                    //System.out.println("Tamanhao recebido: "+packet.getLength());
                    //System.out.println("######Message:" + message);


                    tokens = message.split(";");

                    //guardar id terminal**********
                    pares = tokens[tokens.length-1].split("\\|");
                    idTerminal = Integer.parseInt(pares[1]);

                    //System.out.println("ID TERMINAL: " + idTerminal);
                    //************************ */


                    pares = tokens[0].split("\\|");

                    if(pares[1].equals("login")){
                        pares = tokens[1].split("\\|");
                        num = Integer.parseInt(pares[1]);
                        pares = tokens[2].split("\\|");
                        pass = pares[1];
                        boolean verificao;
                        try{
                            verificao= server.verificaLogin(num, pass);

                        }catch(java.rmi.ConnectException c){
                            connectToRMI();
                            verificao= server.verificaLogin(num, pass);
                            

                        }

                        if(verificao){
                            //envia mensagem com as eleições a decorrer

                            //depois de escolher, devolver as listas canidatas
                            ArrayList<Eleicao> arrayEleicao;
                            

                            message = "type|status;logged|on;msg|Welcome to eVoting;target|"+idTerminal;

                            
                            buffer = message.getBytes();
                            packet = new DatagramPacket(buffer, buffer.length, group, PORT_COMUNICAR);
                            socket.send(packet);

                            try{
                                arrayEleicao = server.eleicoesAtivas(num);
                            }
                            catch(java.rmi.ConnectException c){
                                connectToRMI();
                                arrayEleicao = server.eleicoesAtivas(num);

                            }
                            //System.out.println(arrayEleicao);

                            //====== ENVIAR MENSAGEM COM AS ELEIÇÕES DE ACORDO COM AS CARACTERISTICA DO ELEITOR =========================
                            message = "type|listaEleicoes;item_count|"+arrayEleicao.size();

                            for(int i = 0; i < arrayEleicao.size(); i++){
                                message = message + ";item_"+i+"_name|"+arrayEleicao.get(i).getTitulo();
                            }
                            message = message + ";target|" + idTerminal;
                            //System.out.println("LISTA: "+message);

                            buffer = message.getBytes();
                            packet = new DatagramPacket(buffer, buffer.length, group, PORT_COMUNICAR);
                            socket.send(packet);

                            //=============================================================================================


                        }
                        else{
                            message = "type|status;logged|off;msg|Invalid;target|" + idTerminal;

                            buffer = message.getBytes();
                            packet = new DatagramPacket(buffer, buffer.length, group, PORT_COMUNICAR);
                            socket.send(packet);
                        }
                    }

                    if(pares[1].equals("getListaCandidatos")){
                        ArrayList<String> listaCandidatos;
                        pares = tokens[1].split("\\|");
                        nomeEleicao = pares[1];

                        pares = tokens[2].split("\\|");
                        num = Integer.parseInt(pares[1]);

                        try{
                            listaCandidatos = server.getListaCandidatos(nomeEleicao, num);
                        }catch(java.rmi.ConnectException c){
                            connectToRMI();
                            listaCandidatos = server.getListaCandidatos(nomeEleicao, num);
                        }    
                        //System.out.println(listaCandidatos);

                        //====== ENVIAR MENSAGEM COM AS LISTAS DE CANDIDATOS DE ACORDO COM A ELEICAO =========================
                        message = "type|returnListaCandidatos;item_count|"+listaCandidatos.size();

                        for(int i = 0; i < listaCandidatos.size(); i++){
                            message = message + ";item_"+i+"_name|"+listaCandidatos.get(i);
                        }
                        message = message + ";target|" + idTerminal;
                        //System.out.println("LISTA: "+message);

                        buffer = message.getBytes();
                        packet = new DatagramPacket(buffer, buffer.length, group, PORT_COMUNICAR);
                        socket.send(packet);

                        //=============================================================================================
                    }

                    if(pares[1].equals("voto")){
                        pares = tokens[1].split("\\|");
                        nomeEleicao = pares[1];

                        pares = tokens[2].split("\\|");
                        listaEscolhida = pares[1];

                        pares = tokens[3].split("\\|");
                        num = Integer.parseInt(pares[1]);

                        //Id do terminal já é guardado no inicio
                        try{
                            
                            if(server.processaVoto(nomeEleicao, listaEscolhida, num, departamento)){

                            //voto aceite
                            message = "type|estadoVoto;estado|aceite;target|" + idTerminal;

                            }
                            else{
                                //voto recusado
                                message = "type|estadoVoto;estado|recusado;target|" + idTerminal;
                            }
                        }catch(java.rmi.ConnectException c){
                            connectToRMI();
                            if(server.processaVoto(nomeEleicao, listaEscolhida, num, departamento)){

                                //voto aceite
                                message = "type|estadoVoto;estado|aceite;target|" + idTerminal;
    
                            }
                            else{
                                //voto recusado
                                message = "type|estadoVoto;estado|recusado;target|" + idTerminal;
                            }
                        }
                        
                        

                        buffer = message.getBytes();
                        packet = new DatagramPacket(buffer, buffer.length, group, PORT_COMUNICAR);
                        socket.send(packet);

                    }
                } catch (InterruptedException e) {
                } catch(IOException io){
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }
}
