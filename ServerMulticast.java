import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.*;
import java.util.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

class ServerMulticast extends Thread{

	private static String MULTICAST_DESCOBRIR;
    private static String MULTICAST_COMUNICAR;
    private static int PORT_DESCOBRIR = 4321;
    private static int PORT_COMUNICAR = 1234;
    private static String departamento;

    public ServerMulticast(String departamento, String grupoDescobrir, String grupoComunicar) {
        super("User " + (long) (Math.random() * 1000));
        this.departamento = departamento;
        this.MULTICAST_DESCOBRIR = grupoDescobrir;
        this.MULTICAST_COMUNICAR = grupoComunicar;
    }

    public static void doSomething(ServerRMI_Interface server){
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        int opt;
        try{
            

            while(true){
                try{
                System.out.println("[0]  DESBLOQUEIA TERMINAL");
                System.out.println("[1]  IDENTIFICAR ELEITOR");
                System.out.print("OPCAO:  ");
                opt = Integer.parseInt(reader.readLine());

                switch(opt){
                    case 0:
                        desbloqueiaTerminal();
                        break;
                    case 1:
                        identificarEleitor(server);
                        break;

                }

                }catch(Exception e){

                }
            }

        }catch(Exception e){

        }finally{
        }
        
    }

    public static void identificarEleitor(ServerRMI_Interface server){
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        int numero;
        try{

            System.out.print("NUMERO DE IDENTIFICACAO: ");
            numero = Integer.parseInt(reader.readLine());
            if(server.identificarEleitor(numero)){
                System.out.println("IDENTIFICACAO CONCLUIDA COM SUCESSO");
                desbloqueiaTerminal();
            }


        }catch(IOException e){
            System.out.println("\nFORMATO DE DADOS INVALIDOS");
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

                            System.out.println("MANDA BLOQUEAR "+terminal);
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

    public static void main(String[] args) {
        String departamento;
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        ArrayList<String> gruposMulticast;

        try{
            ServerRMI_Interface server = (ServerRMI_Interface) LocateRegistry.getRegistry(7000).lookup("ServerRMI");
            System.out.print("LOCALIZACAO DA MESA: ");
            departamento = reader.readLine();
            if(server.abreMesaVoto(departamento)){
                gruposMulticast = server.getGruposMulticast(departamento);
                
                ServerMulticast mesa = new ServerMulticast(departamento, gruposMulticast.get(0), gruposMulticast.get(1));

                mesa.start();// thread que vai comunicar com os terminais com eleitores la
                doSomething(server);
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
        try {
            socket = new MulticastSocket();  // create socket without binding it (only for sending)
            while (true) {
                byte[] buffer = "TESTE".getBytes();

                InetAddress group = InetAddress.getByName(MULTICAST_COMUNICAR);
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT_COMUNICAR);
                socket.send(packet);

                try { 
                    sleep((long) (Math.random() * 5000)); //5seg 
                } catch (InterruptedException e) {
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }
}