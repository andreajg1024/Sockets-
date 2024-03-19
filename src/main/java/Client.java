import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private DataInputStream inputStream = null;
    private DataOutputStream outputStream = null;
    private Scanner scanner = new Scanner(System.in);
    private final String TERMINATION_COMMAND = "salir()";

    public void connect(String ip, int puerto) {
        try {
            socket = new Socket(ip, puerto);
            System.out.println("Conectado a: " + socket.getInetAddress().getHostName());
        } catch (IOException e) {
            System.err.println("Excepción al conectar: " + e.getMessage());
            System.exit(1);
        }
    }

    public void openStreams() {
        try {
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.flush();
        } catch (IOException e) {
            System.err.println("Error al abrir flujos de entrada/salida");
        }
    }

    public void send(String mensaje) {
        try {
            outputStream.writeUTF(mensaje);
            outputStream.flush();
        } catch (IOException e) {
            System.err.println("IOException al enviar mensaje");
        }
    }

    public void closeConnection() {
        try {
            inputStream.close();
            outputStream.close();
            socket.close();
            System.out.println("Conexión cerrada");
        } catch (IOException e) {
            System.err.println("IOException al cerrar conexión");
        } finally {
            System.exit(0);
        }
    }

    public void executeConnection(String ip, int puerto) {
        Thread thread = new Thread(() -> {
            try {
                connect(ip, puerto);
                openStreams();
                receiveData();
            } finally {
                closeConnection();
            }
        });
        thread.start();
    }

    public void receiveData() {
        String mensaje = "";
        try {
            do {
                mensaje = inputStream.readUTF();
                System.out.println("\n[Servidor] => " + mensaje);
                System.out.print("\n[Usted] => ");
            } while (!mensaje.equals(TERMINATION_COMMAND));
        } catch (IOException e) {}
    }

    public void writeData() {
        String entrada = "";
        while (true) {
            System.out.print("[Usted] => ");
            entrada = scanner.nextLine();
            if (entrada.length() > 0)
                send(entrada);
        }
    }

    public static void main(String[] args) {
        Client cliente = new Client();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Ingresa la IP: [localhost por defecto] ");
        String ip = scanner.nextLine();
        if (ip.length() <= 0) ip = "localhost";

        System.out.println("Puerto: [5050 por defecto] ");
        String puerto = scanner.nextLine();
        if (puerto.length() <= 0) puerto = "5050";
        cliente.executeConnection(ip, Integer.parseInt(puerto));
        cliente.writeData();
    }
}
