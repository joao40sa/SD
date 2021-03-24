import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.*;
import java.util.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

class ServerMulticast extends Thread{

	private String MULTICAST_DESCOBRIR;
    private String MULTICAST_COMUNICAR;
    private int PORT = 4321;
    private String departamento;

    public ServerMulticast(String departamento, String grupoDescobrir, String grupoComunicar) {
        super("User " + (long) (Math.random() * 1000));
        this.departamento = departamento;
        this.MULTICAST_DESCOBRIR = grupoDescobrir;
        this.MULTICAST_COMUNICAR = grupoComunicar;
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

                mesa.start();
            }
            else{
                System.out.println("[IMPOSSIVEL ABRIR MESA DE VOTO]  JA EXISTE UMA MESA NESTE DEPARTAMENTO");
            }
        } catch(Exception e){
            System.out.println("Exception no main: "+e);
        }
        
    }

    public void run() {
        MulticastSocket socket = null;
        System.out.println("Mesa de voto: "+ this.departamento + " ready...");
        try {
            socket = new MulticastSocket();  // create socket without binding it (only for sending)
            Scanner keyboardScanner = new Scanner(System.in);
            while (true) {
                String readKeyboard = keyboardScanner.nextLine();
                byte[] buffer = readKeyboard.getBytes();

                InetAddress group = InetAddress.getByName(MULTICAST_DESCOBRIR);
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }
}