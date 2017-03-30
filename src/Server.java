import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * Server Class, Creates a network server to act as the hub for the file transfer client.
 *
 * @author  Justin Duong(100588398)
 * @version 1.0
 * @since   3/30/2017
 *
 */
public class Server {

    private int PORT = 8080;
    private ServerSocket serverSocket;
    public static String folderLocation = "C:/Users/100588398/Desktop/school/randy/Assignment2/SharedServer";

    /**
     *
     * Constructor for Server creates a socket at a port. I have chosen 8080 as my arbitrary port.
     *
     */
    public Server(){
        System.out.println("Server open on.. " + PORT);
        try {
            this.serverSocket = new ServerSocket(PORT);
        } catch(IOException e){
            e.printStackTrace();
        }

    }
    /**
     *
     * handleRequests method is one that is a continuous loop while the server is alive that constantly listens for
     * any clients that are trying to connect to it on its port. It does this by creating a socket and once is connected
     * sends it off the RequestHandler class for further instructions.
     *
     *
     * @throws IOException
     */
    public void handleRequests() throws IOException {

        while (true) {
            Socket socket = this.serverSocket.accept();
            RequestHandler handler =
                    new RequestHandler(socket);
            Thread handlerThread = new Thread(handler);
            handlerThread.start();

        }
    }

    /**
     * Main method creates a server and begins its connection listener with the handleRequest method.
     *
     */
    public static void main(String[] args){
        try {

            Server server = new Server();

            server.handleRequests();


        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
