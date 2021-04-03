import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.util.Scanner;
import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;

//deixar comment
abstract class MyTimerTask extends TimerTask
{
  public int secondsPassed;
  public Thread t;
  public MulticastSocket s;
  public MyTimerTask(int secondsPassed, Thread t, MulticastSocket s){
    this.secondsPassed = secondsPassed;
    this.t = t;
    this.s = s;
  }
  public void init(int arg)
  {
    secondsPassed = arg;
  }
}


class VotingThread implements Runnable{
    String id; // name of thread
    Thread t;
    private static int PORT_COMUNICAR = 1234;
    private static String MULTICAST_COMUNICAR;

    VotingThread(String idTerminal, String multicast) {
        id = idTerminal;
        MULTICAST_COMUNICAR = multicast;        
        t = new Thread(this, id);
        t.start(); // Start the thread
    }

    public void run() {
        System.out.println("[TERMINAL DESBLOQUEADO]");
        MulticastSocket socket = null;
        String[] tokens;
        String[] pares;
        String aux_estado;
        String eleicaoEscolhida = null; //eleicao em que o eleitor escolheu votar
        String listaEscolhida = null; //lista candidata da eleicao em que o eleitore escolheu votar

        try {
            InputStreamReader input = new InputStreamReader(System.in);
            BufferedReader reader = new BufferedReader(input);

            socket = new MulticastSocket(PORT_COMUNICAR);  // create socket and bind it
            InetAddress group = InetAddress.getByName(MULTICAST_COMUNICAR);
            int numero = 0; //numero ao fazer login
            String pass = null; //password de login
            int auxId; //Variavel para verificar se a mensagem recebida é para o id do terminal. (VERIFICAR SE FOI ENVIADA PARA O TERMINAL CORRETO)
            int valido = 0;
            int opcaoEscolhida = -1; //variavel de escolha da eleicao
            socket.joinGroup(group); //entrar no grupo multicast

            byte[] buffer = new byte[256];
            
            Timer timer = new Timer();
            MyTimerTask task = null;

                task = new MyTimerTask(0, t, socket){
                    public void run()  {
                        secondsPassed++;
                        //System.out.println("PASSOU "+secondsPassed);
                        if(secondsPassed == 60){
                            timer.cancel();
                            s.close();
                            t.stop();
                            
                           
                            return;
                        }
                    }
                };    
            
            
            timer.scheduleAtFixedRate(task,1000,1000); 

            while(valido == 0){
                try{
                    System.out.print("Numero: ");
                    numero = Integer.parseInt(reader.readLine());
                    valido = 1;
                    task.secondsPassed = 0;
                } catch(NumberFormatException ne){
                    System.out.println("Numero Invalido");
                }
            }

            System.out.print("Password: ");
            pass = reader.readLine();
            task.secondsPassed = 0;            
            

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


                            valido = 0;
                            while(valido == 0){
                                try{
                                    System.out.print("Numero: ");
                                    numero = Integer.parseInt(reader.readLine());
                                    valido = 1;
                                    task.secondsPassed = 0;
                                } catch(NumberFormatException ne){
                                    System.out.println("Numero Invalido");
                                }
                            }

                            System.out.print("Password: ");
                            pass = reader.readLine();
                            task.secondsPassed = 0; 

                            message =  "type|login;username|" + numero + ";password|" + pass +";sender|"+id;  
                            
                            buffer = message.getBytes();

                            packet = new DatagramPacket(buffer, buffer.length, group, PORT_COMUNICAR);
                            socket.send(packet);


                        }
                    }
                
                    if(pares[1].equals("listaEleicoes")){
                        int nEleicoes; //numero de eleicoes disponiveis para votar
                        


                        pares = tokens[1].split("\\|");
                        nEleicoes = Integer.parseInt(pares[1]);
                        if(nEleicoes == 0){
                            System.out.println("\nNAO EXISTEM ELEICOES EM QUE POSSA VOTAR");
                            return;
                        }
                        else{
                            System.out.println("\nELEICOES DISPONIVEIS:");
                            for(int i=0; i<nEleicoes; i++){
                                pares = tokens[i+2].split("\\|"); //+2 por causa do offset da mensagem
                                System.out.println("    ["+(i+1)+"]  "+pares[1]);
                            }

                            valido = 0;
                            while(valido == 0){
                                try{
                                    System.out.print("OPCAO: ");
                                    opcaoEscolhida = Integer.parseInt(reader.readLine());
                                    valido = 1;
                                    task.secondsPassed = 0;
                                } catch(NumberFormatException ne){
                                    System.out.println("OPCAO INVALIDA");
                                }
                            }

                            
                            pares = tokens[opcaoEscolhida+1].split("\\|"); //+2 por causa do offset da mensagem
                            eleicaoEscolhida = pares[1];
                            
                            message =  "type|getListaCandidatos;eleicao|"+ eleicaoEscolhida + ";username|"+ numero +";sender|" + id;
                            
                            buffer = message.getBytes();

                            packet = new DatagramPacket(buffer, buffer.length, group, PORT_COMUNICAR);
                            socket.send(packet);
                            



                        }
                    }
                
                    if(pares[1].equals("returnListaCandidatos")){

                        int nListas; //numero de eleicoes disponiveis para votar


                        pares = tokens[1].split("\\|");
                        nListas = Integer.parseInt(pares[1]);
                        if(nListas == 0){
                            System.out.println("\nNAO EXISTEM LISTAS EM QUE POSSA VOTAR");
                            return;
                        }
                        else{
                            System.out.println("\nListas DISPONIVEIS:");
                            for(int i=0; i<nListas; i++){
                                pares = tokens[i+2].split("\\|"); //+2 por causa do offset da mensagem
                                System.out.println("    ["+(i+1)+"]  "+pares[1]);
                            }
                            System.out.println("    ["+(nListas+1)+"]  VOTO BRANCO");
                            System.out.println("    ["+(nListas+2)+"]  VOTO NULO");

                            valido = 0;
                            while(valido == 0){
                                try{
                                    System.out.print("OPCAO: ");
                                    opcaoEscolhida = Integer.parseInt(reader.readLine());
                                    valido = 1;
                                    task.secondsPassed = 0;
                                } catch(NumberFormatException ne){
                                    System.out.println("OPCAO INVALIDA");
                                }
                            }

                            if(opcaoEscolhida == nListas+1){
                                listaEscolhida = "branco";
                            }
                            else if(opcaoEscolhida == nListas+2){
                                listaEscolhida = "nulo";
                            }
                            else{
                                pares = tokens[opcaoEscolhida+1].split("\\|");
                                listaEscolhida = pares[1];
                            }

                            

                            //ystem.out.println("OPCAO ESCOLHIDA: " + pares[1]);

                            message =  "type|voto;eleicao|"+ eleicaoEscolhida + ";lista|" + listaEscolhida + ";numero|" + numero + ";sender|" + id;
                            //System.out.println("MESSAGE: "+message);
                            buffer = message.getBytes();

                            packet = new DatagramPacket(buffer, buffer.length, group, PORT_COMUNICAR);
                            socket.send(packet);


                        }
                    }

                    if(pares[1].equals("estadoVoto")){


                        pares = tokens[1].split("\\|");
                        if(pares[1].equals("aceite")){
                            System.out.println("\n***********  VOTO REGISTADO COM SUCESSO  ***********");
                        }
                        else{
                            System.out.println("\n***********  VOTO INVALIDO  ***********");
                        }
                        socket.close();
                        return;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }catch(IllegalStateException te){
            System.out.println("FIM TIMER");
            socket.close();
            return;
        }finally {
            socket.close();
        }
    }
    





}

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
                        if(aux_estado.equals("OCUPADO")){
                            estado = false;
                            pares = tokens[3].split("\\|");
                            MULTICAST_COMUNICAR = pares[1];

                            VotingThread terminalThread = new VotingThread(id, MULTICAST_COMUNICAR);

                            terminalThread.t.join();
                            System.out.println("[TERMINAL BLOQUEADO]");
                            estado = true;


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
        }catch (InterruptedException e) {
            System.out.println("Main thread Interrupted");  // set interrupt flag
        }finally {
            socket.close();
        }
    }

}
