import java.io.*;
import java.net.*;

public class Server {
    private Socket socket;
    private ServerSocket serverSocket;
    private BufferedReader entradaCliente = null;
    private PrintWriter salidaCliente = null;
    private BufferedReader entradaConsola = new BufferedReader(new InputStreamReader(System.in));
    final String TERMINAR_CONVERSACION = "salir()";

    public void iniciarConexion(int puerto) {
        try {
            serverSocket = new ServerSocket(puerto);
            System.out.println("Esperando conexión en el puerto " + puerto + "...");
            socket = serverSocket.accept();
            System.out.println("Conexión establecida con: " + socket.getInetAddress().getHostName() + "\n\n\n");
        } catch (IOException e) {
            System.err.println("Error al iniciar la conexión: " + e.getMessage());
            System.exit(1);
        }
    }

    public void establecerFlujos() {
        try {
            entradaCliente = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            salidaCliente = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.err.println("Error al establecer flujos de comunicación");
        }
    }

    public void recibirMensajes() {
        String mensaje = "";
        try {
            do {
                mensaje = entradaCliente.readLine();
                System.out.println("\n[Cliente] => " + mensaje);
                System.out.print("\n[Usted] => ");
            } while (!mensaje.equals(TERMINAR_CONVERSACION));
        } catch (IOException e) {
            cerrarConexion();
        }
    }

    public void enviarMensaje(String mensaje) {
        salidaCliente.println(mensaje);
    }

    public void escribirMensajes() {
        String entradaUsuario = "";
        while (true) {
            System.out.print("[Usted] => ");
            try {
                entradaUsuario = entradaConsola.readLine();
            } catch (IOException e) {
                System.err.println("Error al leer entrada del usuario");
            }
            if (entradaUsuario.length() > 0)
                enviarMensaje(entradaUsuario);
        }
    }

    public void cerrarConexion() {
        try {
            entradaCliente.close();
            salidaCliente.close();
            socket.close();
        } catch (IOException e) {
            System.err.println("Error al cerrar la conexión: " + e.getMessage());
        } finally {
            System.out.println("Conversación finalizada....");
            System.exit(0);
        }
    }

    public void ejecutarConexion(int puerto) {
        Thread hilo = new Thread(() -> {
            while (true) {
                try {
                    iniciarConexion(puerto);
                    establecerFlujos();
                    recibirMensajes();
                } finally {
                    cerrarConexion();
                }
            }
        });
        hilo.start();
    }

    public static void main(String[] args) {
        Server servidor = new Server();
        BufferedReader entradaConsola = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Ingrese el puerto [5050 por defecto]: ");
        String puertoStr = "";
        try {
            puertoStr = entradaConsola.readLine();
        } catch (IOException e) {
            System.err.println("Error al leer entrada del usuario");
        }
        int puerto = (puertoStr.length() > 0) ? Integer.parseInt(puertoStr) : 5050;

        servidor.ejecutarConexion(puerto);
        servidor.escribirMensajes();
    }
}
