import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.util.Scanner;


class VotingTerminal extends Thread{

	private String MULTICAST_DESCOBRIR;
    private String MULTICAST_COMUNICAR;
    private int PORT_DESCOBRIR = 4321;
    private int PORT_COMUNICAR = 1234;
    private String id;
    private boolean estado; //false se tem alguem, true se esta livre

    public VotingTerminal(String grupoDescobrir){
        this.MULTICAST_DESCOBRIR = grupoDescobrir;
        this.id = ""+ ((long) (Math.random()*1000));
        this.estado = true;
    }

    public static void main(String[] args) {
        if(args.length > 0){
            VotingTerminal terminal = new VotingTerminal(args[0]);
            System.out.println(terminal.id);
            terminal.start();
        }
        else{
            System.out.println("FALTOU ESPECIFICAR O GRUPO MULTICAST PARA DESCOBERTA");
        }
        
    }

    public void run() {
        MulticastSocket socket = null;
        String[] tokens;
        String[] pares;
        String aux_estado;

        try {
            socket = new MulticastSocket(PORT_DESCOBRIR);  // create socket and bind it
            InetAddress group = InetAddress.getByName(MULTICAST_DESCOBRIR);
            socket.joinGroup(group);
            while (true) {
                byte[] buffer = new byte[256];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String message = new String(packet.getData(), 0, packet.getLength());
                tokens = message.split(";");
                pares = tokens[0].split("\\|");
                if(pares[1].equals("getEstado")){
                    if(estado){
                        if(this.estado){
                            message =  "type|sendEstado;estado|LIVRE;sender|"+this.id;
                        }else{
                            message = "type|sendEstado;estado|OCUPADO;sender|"+this.id;
                        }
                        buffer = message.getBytes();
                        packet = new DatagramPacket(buffer, buffer.length, group, PORT_DESCOBRIR);
                        socket.send(packet);
                    }

                }else if(pares[1].equals("setEstado")){
                    pares = tokens[1].split("\\|");
                    aux_estado = pares[1];

                    pares = tokens[2].split("\\|");

                    if(pares[1].equals(this.id)){
                        System.out.println("VOU BLOQUEAR");
                        if(aux_estado.equals("OCUPADO"))
                            estado = false;
                        else
                            estado = true;
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