package Server;

import Server.music.Networking.PortListener;
import Server.music.Song;
import Server.view.ServerViewController;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.Scanner;

public class ServerApp extends Application {
    private Stage primaryStage;
    private PortListener portListener = null;

    private ObservableList<Song> playlist = FXCollections.observableArrayList();

    public ObservableList<Song> getPlaylist() {
        return playlist;
    }

    public void setPlaylist(ObservableList<Song> playlist) {
        this.playlist = playlist;
    }

    public boolean startPortListener() {
        if (getPortListener() == null) {
            setPortListener(new PortListener(playlist));
            getPortListener().start();
            return true;
        } else {
            return false;
        }
    }

    public boolean stopPortListener() {
        if (getPortListener() != null) {
            try {
                ServerSocket ss = getPortListener().getServerSocket();
                if (ss != null)
                    ss.close();
                getPortListener().interrupt();
                getPortListener().join();
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
            setPortListener(null);
            return true;
        } else {
            return false;
        }
    }

    public void loadPlaylist(){
        try {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("LOAD PLAYLIST");
            File file = chooser.showOpenDialog(primaryStage);
            Scanner sc=new Scanner(file);
            String path = sc.nextLine();
            playlist.add(new Song(path));
            while(path != null){
                path = sc.nextLine();
                playlist.add(new Song(path));
            }
            sc.close();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void savePlaylist(){
        try {
            FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files (.txt)",".txt"));
            chooser.setInitialFileName("playlist.txt");
            chooser.setTitle("SAVE PLAYLIST");
            File file = chooser.showSaveDialog(primaryStage);
            PrintWriter pw = new PrintWriter(file);
            for (Song s: playlist){
                pw.println(s.getPath());
            }
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public PortListener getPortListener() {
        return portListener;
    }

    public void setPortListener(PortListener portListener) {
        this.portListener = portListener;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Server Application");
        initServerView();

    }

    public void initServerView() {
        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ServerApp.class.getResource("/Server/view/ServerView.fxml"));
            AnchorPane Overview = (AnchorPane) loader.load();

            ServerViewController controller = loader.getController();
            controller.setServerApp(this);

            Scene scene = new Scene(Overview);
            primaryStage.setScene(scene);
            primaryStage.show();

            primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                public void handle(WindowEvent we) {
                    stopPortListener();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
