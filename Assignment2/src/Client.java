
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
/**
 *
 * Client Class. This is the client class, it begins with a directory chooser to choose the folder that user wants to use
 * as their client folder. The client uses JavaFx to display UI and handle its information.
 *
 * @author  Justin Duong(100588398)
 * @version 1.0
 * @since   3/30/2017
 *
 */
public class Client extends Application {

    Stage window;
    public BufferedReader in;
    public BufferedWriter out;

    public int size = 0;
    public int size2 = 0;
    public static ListView clientListing;
    public static ListView serverListing;

    public static int currentSelected = 0;


    public static String clientFolderLocation;

    public static ObservableList<String> fileListServer = FXCollections.observableArrayList();
    public static ObservableList<String> fileListClient = FXCollections.observableArrayList();

    /**
     * getDirectory method is meant to send a request to the server to receive a list of files in the directory that
     * the server is using to host its files. The static lists that holds the info is then updated with the new content.
     *
     * @throws IOException
     *
     */
    public void getDirectory() throws IOException {
        if(!fileListServer.isEmpty() && !serverListing.getItems().isEmpty()) {
            fileListServer.clear();
            serverListing.getItems().clear();
        }
        Socket socket = new Socket("localhost", 8080);
        in = new BufferedReader(new InputStreamReader(
                socket.getInputStream()
        ));
        OutputStreamWriter os = new OutputStreamWriter(socket.getOutputStream());
        out = new BufferedWriter(os);
        out.write("DIR"+"\n");
        out.flush();
        String line;
        while ((line = in.readLine()) != null) {
            fileListServer.add(line);
        }
        System.out.println(serverListing);

        serverListing.getItems().addAll(fileListServer);

        socket.close();
    }
    /**
     *
     * downloadServer method creates a socket and reader/writer to get and send the information necessary for the download.
     * After the reading in the download, write to a file with the same file name.
     *
     * @param fileName Name of the file that you would like to download
     * @throws IOException
     *
     */
    public void downloadServer(String fileName) throws IOException{
        Socket socket = new Socket("localhost", 8080);
        in = new BufferedReader(new InputStreamReader(
                socket.getInputStream()
        ));
        OutputStreamWriter os = new OutputStreamWriter(socket.getOutputStream());
        out = new BufferedWriter(os);
        out.write("DOWNLOAD"+"\n");
        out.flush();
        out.write(fileName+"\n");
        out.flush();
        String line;


        BufferedWriter bw = null;
        FileWriter fw = null;

        try {
            fw = new FileWriter(clientFolderLocation+"\\"+fileName);
            bw = new BufferedWriter(fw);
            while ((line = in.readLine()) != null) {
                bw.write(line);
                bw.newLine();
            }
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

        socket.close();
    }
    /**
     *
     * uploadServer method creates a socket and reader/writer to get and send the information necessary for the download.
     * Read the file that you want to upload and write to the server.
     *
     * @param fileName Name of the file that you would like to download
     * @throws IOException
     *
     */
    public void uploadServer(String fileName) throws IOException{
        Socket socket = new Socket("localhost", 8080);
        in = new BufferedReader(new InputStreamReader(
                socket.getInputStream()
        ));
        OutputStreamWriter os = new OutputStreamWriter(socket.getOutputStream());
        out = new BufferedWriter(os);
        out.write("UPLOAD"+"\n");
        out.flush();
        out.write(fileName+"\n");
        out.flush();

        BufferedReader br = new BufferedReader(new FileReader(new File(clientFolderLocation +"\\"+ fileName)));
        String line;
        try {
            while ((line = br.readLine()) != null) {
                out.write(line + "\n");
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        socket.close();
    }

    /**
     *
     * Main method and run the JavaFX
     *
     */
    public static void main(String[] args) {
        launch(args);
    }
    /**
     *
     * updateClientList method scans the client selected folder for files and submits them to the static clientListing,
     *
     */
    public void updateClientList(){
        clientListing.getItems().clear();
        fileListClient.clear();
        File[] directory = new File(clientFolderLocation).listFiles();
        for(File file : directory){
            fileListClient.add(file.getName());
        }
        clientListing.getItems().addAll(fileListClient);
    }
    /**
     *
     * Start method, the main JavaFX and thread handling method. First, handles the directory chooser to choose the client
     * folder.
     *
     * Then has my main updating thread. The thread listens to the folders for any changes in size e.g Upload/Download
     * or even delete. If there is a change then it updates the respective listView.
     *
     * Set up two listviews to display the listings and buttons to download/upload files from/to the server.
     *
     * @param primaryStage Main stage for the JavaFX
     *
     */
    public void start(Stage primaryStage) throws Exception {

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File("."));
        File mainDirectory = directoryChooser.showDialog(null);
        clientFolderLocation = mainDirectory.getAbsolutePath();

        clientListing = new ListView<>();
        serverListing = new ListView<>();

        Runnable redo = new Runnable(){
            public void run() {
                //try {
                    while(true){
                        File[] directory = new File(clientFolderLocation).listFiles();
                        File[] directory2 = new File(Server.folderLocation).listFiles();
                        if(size != directory.length){
                            Platform.runLater(new Runnable() {
                                public void run(){
                                    updateClientList();
                                }
                            });
                        }
                        if(size2 !=directory2.length) {
                            Platform.runLater(new Runnable() {

                                public void run() {
                                    try {
                                        getDirectory();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                        }
                        size = directory.length;
                        size2 = directory2.length;
                    }

               // }// catch (IOException e) {
                   // e.printStackTrace();
                //}
            }
        };
        window = primaryStage;
        window.setTitle("Assignment 2 - Client");

        BorderPane border = new BorderPane();


        //updateClientList();
        clientListing.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                System.out.println("ListView selection changed from oldValue = "
                        + oldValue + " to newValue = " + newValue);
                currentSelected = 0;

            }
        });



        //getDirectory();


        Thread keepRunningThread = new Thread(redo);
        keepRunningThread.start();
        serverListing.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                System.out.println("ListView selection changed from oldValue = "
                        + oldValue + " to newValue = " + newValue);
                currentSelected = 1;

            }
        });


        clientListing.setMaxWidth(200);
        serverListing.setMaxWidth(200);
        border.setLeft(clientListing);
        border.setRight(serverListing);

        HBox hbox = new HBox();


        Button buttonDL = new Button("Download");
        buttonDL.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                try {
                    System.out.println(serverListing.getSelectionModel().getSelectedItem());
                    if(serverListing.getSelectionModel().getSelectedItem() !=null) {
                        if(currentSelected == 1) {
                            downloadServer(serverListing.getSelectionModel().getSelectedItem().toString());
                        }
                    }
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        });
        buttonDL.setPrefSize(100, 20);

        Button buttonUP = new Button("Upload");

        buttonUP.setPrefSize(100, 20);
        buttonUP.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                System.out.println(currentSelected);
                try {
                    //System.out.println(clientListing.getSelectionModel().getSelectedItem());
                    if(clientListing.getSelectionModel().getSelectedItem() !=null) {
                        if(currentSelected == 0) {
                            uploadServer(clientListing.getSelectionModel().getSelectedItem().toString());
                        }
                    }
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        });
        hbox.getChildren().

                addAll(buttonDL, buttonUP);

        border.setTop(hbox);
        Scene scene = new Scene(border, 400, 600);

        window.setScene(scene);
        window.show();




    }
}

