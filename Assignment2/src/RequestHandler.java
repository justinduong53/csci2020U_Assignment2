import java.io.*;
import java.net.Socket;

/**
 *
 * RequestHandler Class is a class that decides how a socket for the Server is recieved and used.
 *
 * @author  Justin Duong(100588398)
 * @version 1.0
 * @since   3/30/2017
 *
 */
public class RequestHandler implements Runnable{

    private Socket socket;
    private DataOutputStream out;
    private Boolean command = false;


    /**
     *
     * Request Handler constructor creates a socket using the given socket. The method is the main handling method.
     * There are 3 main types of handling DIR, DOWNLOAD, AND UPLOAD. DIR scans the server folder for files and writes them
     * out through the socket. DOWNLOAD sends/writes the information of the file that the user selected to the client.
     * UPLOAD receives data from socket and writes to a file with the same name. The socket is then closed.
     *
     * @param socket Given socket
     *
     */
    public RequestHandler(Socket socket){
        this.socket = socket;
    }



    public void run(){
        try {
            InputStream is = socket.getInputStream();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(is)
            );
            System.out.println("Connected!");
            OutputStream os = socket.getOutputStream();
            out = new DataOutputStream(os);

            while (command == false) {
                String response = in.readLine();

                if (response.equals("DIR")) {

                    File[] directory = new File(Server.folderLocation).listFiles();
                    for (File file : directory) {
                        System.out.println(file.getName());
                        out.writeBytes(file.getName() + "\n");
                        out.flush();
                    }
                    System.out.println(directory.length);


                } else if (response.equals("DOWNLOAD")) {
                    String target = in.readLine();
                    System.out.println(target);
                    BufferedReader br = new BufferedReader(new FileReader(new File(Server.folderLocation +"\\"+ target)));
                    String fileText = "";
                    String line;
                    try {
                        while ((line = br.readLine()) != null) {
                            fileText = fileText + line + "\n";
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    out.writeBytes(fileText + "\n");
                    out.flush();

                } else if (response.equals("UPLOAD")) {
                    String target = in.readLine();
                    System.out.println(target);

                    BufferedWriter bw = null;
                    FileWriter fw = null;

                    try {

                        String line;
                        fw = new FileWriter(new File(Server.folderLocation +"\\"+ target));
                        bw = new BufferedWriter(fw);
                        while ((line = in.readLine()) != null) {
                            bw.write(line);
                            bw.newLine();
                        }

                        //System.out.println("Done");

                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (bw != null)
                                bw.close();

                            if (fw != null)
                                fw.close();

                        } catch (IOException ex) {

                            ex.printStackTrace();

                        }

                    }
                }

                command = true;
            }
            socket.close();


        } catch (IOException e){
            System.out.println("Disconnected!");

        }
    }

}