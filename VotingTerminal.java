import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.util.Scanner;
import java.io.*;


class VotingTerminal extends Thread{

	private static String MULTICAST_DESCOBRIR;
    private static String MULTICAST_COMUNICAR;
    private static int PORT_DESCOBRIR = 4321;
    private static int PORT_COMUNICAR = 1234;
    private static String id;
    private static boolean estado; //false se tem alguem, true se esta livre
    private static VotingTerminal terminal;

    public VotingTerminal(String grupoDescobrir){
        MULTICAST_DESCOBRIR = grupoDescobrir;
        id = ""+ ((long) (Math.random()*1000));
        estado = true;
    }

    public static void main(String[] args) {
        if(args.length > 0){
            terminal = new VotingTerminal(args[0]);
            System.out.println(terminal.id);
            connect();
            //terminal.start();
        }
        else{
            System.out.println("FALTOU ESPECIFICAR O GRUPO MULTICAST PARA DESCOBERTA");
        } 
    }

    private static void connect(){
        MulticastSocket socket = null;
        String[] tokens;
        String[] pares;
        String aux_estado;
        int numero;
        String pass;

        try {
            socket = new MulticastSocket(PORT_DESCOBRIR);  // create socket and bind it
            InetAddress group = InetAddress.getByName(MULTICAST_DESCOBRIR);
            socket.joinGroup(group);
            InputStreamReader input = new InputStreamReader(System.in);
			BufferedReader reader = new BufferedReader(input);

            while (true) {
                byte[] buffer = new byte[256];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String message = new String(packet.getData(), 0, packet.getLength());
                tokens = message.split(";");
                pares = tokens[0].split("\\|");
                if(pares[1].equals("getEstado")){
                    if(estado){
                        if(estado){
                            message =  "type|sendEstado;estado|LIVRE;sender|"+id;
                        }else{
                            message = "type|sendEstado;estado|OCUPADO;sender|"+id;
                        }
                        buffer = message.getBytes();
                        packet = new DatagramPacket(buffer, buffer.length, group, PORT_DESCOBRIR);
                        socket.send(packet);
                    }

                }else if(pares[1].equals("setEstado")){
                    pares = tokens[1].split("\\|");
                    aux_estado = pares[1];

                    pares = tokens[2].split("\\|");

                    if(pares[1].equals(id)){
                        System.out.println("[TERMINAL DESBLOQUEADO]");
                        if(aux_estado.equals("OCUPADO")){
                            estado = false;
                            pares = tokens[3].split("\\|");
                            MULTICAST_COMUNICAR = pares[1];

                            

                            terminal.start();
                        }
                        else{
                            System.out.println("Esta livre");
                            estado = true;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }
    

    public void run() {
        MulticastSocket socket = null;
        String[] tokens;
        String[] pares;
        String aux_estado;

        try {

            InputStreamReader input = new InputStreamReader(System.in);
			BufferedReader reader = new BufferedReader(input);

            socket = new MulticastSocket(PORT_COMUNICAR);  // create socket and bind it
            InetAddress group = InetAddress.getByName(MULTICAST_COMUNICAR);
            int numero; //numero ao fazer login
            String pass; //password de login
            int auxId; //Variavel para verificar se a mensagem recebida é para o id do terminal. (VERIFICAR SE FOI ENVIADA PARA O TERMINAL CORRETO)

            socket.joinGroup(group); //entrar no grupo multicast

            byte[] buffer = new byte[256];
                

            System.out.print("Numero: ");
            numero = Integer.parseInt(reader.readLine());
            System.out.print("Password: ");
            pass = reader.readLine();

            String message =  "type|login;username|" + numero + ";password|" + pass +";sender|"+id;  
            
            //System.out.println("Message:" + message);
            buffer = message.getBytes();

            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT_COMUNICAR);
            socket.send(packet);


            while (true) {
                

                buffer = new byte[256];
                packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                message = new String(packet.getData(), 0, packet.getLength());

                tokens = message.split(";");


                //guardar id terminal**********
                pares = tokens[tokens.length-1].split("\\|");
                auxId = Integer.parseInt(pares[1]);

                //System.out.println("ID TERMINAL: " + auxId);
                //************************ */


                if(auxId == Integer.parseInt(id)){ //verifica se o id do terminal é o correspondente

                    pares = tokens[0].split("\\|");

                    if(pares[1].equals("status")){


                        pares = tokens[1].split("\\|");
                        if(pares[1].equals("on")){
                            pares = tokens[2].split("\\|");
                            String msg = pares[1];
                            System.out.println("\n***********  " + msg + "  ***********");

                            //vai mostrar listas que recebeu por mensagem

                        }
                        else{
                            pares = tokens[2].split("\\|");
                            String msg = pares[1];
                            System.out.println("***********  " + msg + "  ***********");



                            System.out.println("Numero: ");
                            numero = Integer.parseInt(reader.readLine());
                            System.out.println("Password: ");
                            pass = reader.readLine();

                            message =  "type|login;username|" + numero + ";password|" + pass;  
                            
                            buffer = message.getBytes();

                            packet = new DatagramPacket(buffer, buffer.length, group, PORT_COMUNICAR);
                            socket.send(packet);


                        }
                    }
                
                    if(pares[1].equals("listaEleicoes")){
                        int nEleicoes; //numero de eleicoes disponiveis para votar
                        int opcaoEscolhida; //variavel de escolha da eleicao


                        pares = tokens[1].split("\\|");
                        nEleicoes = Integer.parseInt(pares[1]);
                        if(nEleicoes == 0){
                            System.out.println("\nNAO EXISTEM ELEICOES EM QUE POSSA VOTAR");
                        }
                        else{
                            System.out.println("\nELEICOES DISPONIVEIS:");
                            for(int i=0; i<nEleicoes; i++){
                                pares = tokens[i+2].split("\\|"); //+2 por causa do offset da mensagem
                                System.out.println("    ["+(i+1)+"]  "+pares[1]);
                            }

                            System.out.print("OPCAO: ");
                            opcaoEscolhida = Integer.parseInt(reader.readLine());

                            
                            pares = tokens[opcaoEscolhida+1].split("\\|"); //+2 por causa do offset da mensagem
                            
                            
                            message =  "type|getListaCandidatos;eleicao|"+ pares[1] + ";sender|" + id;
                            
                            buffer = message.getBytes();

                            packet = new DatagramPacket(buffer, buffer.length, group, PORT_COMUNICAR);
                            socket.send(packet);
                            



                        }
                    }
                
                    if(pares[1].equals("returnListaCandidatos")){

                        int nListas; //numero de eleicoes disponiveis para votar
                        int opcaoEscolhida; //variavel de escolha da eleicao


                        pares = tokens[1].split("\\|");
                        nListas = Integer.parseInt(pares[1]);
                        if(nListas == 0){
                            System.out.println("\nNAO EXISTEM LISTAS EM QUE POSSA VOTAR");
                        }
                        else{
                            System.out.println("\nListas DISPONIVEIS:");
                            for(int i=0; i<nListas; i++){
                                pares = tokens[i+2].split("\\|"); //+2 por causa do offset da mensagem
                                System.out.println("    ["+(i+1)+"]  "+pares[1]);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }
}