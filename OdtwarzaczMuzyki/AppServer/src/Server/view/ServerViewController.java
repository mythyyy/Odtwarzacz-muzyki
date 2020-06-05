package Server.view;

import Server.ServerApp;
import Server.music.Song;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

public class ServerViewController {
    @FXML
    private TableView<Song> playlist;
    @FXML
    private TableColumn<Song, String> isPlayingColumn;
    @FXML
    private TableColumn<Song, String> nameColumn;
    @FXML
    private TableColumn<Song, String> durationColumn;

    @FXML
    private Button add;
    @FXML
    private Button remove;
    @FXML
    private Button clear;

    @FXML
    private Button start;
    @FXML
    private Button disconnect;

    @FXML
    private Button load;
    @FXML
    private Button save;

    private ServerApp serverApp;

    public void ServerViewController(){}

    @FXML
    private void loadPlaylist(){
        serverApp.loadPlaylist();
    }

    @FXML
    private void savePlaylist(){
        serverApp.savePlaylist();
    }

    @FXML
    private void handleAddButton() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File path = fileChooser.showOpenDialog(serverApp.getPrimaryStage());
        try {
            Song song = new Song(path.getAbsolutePath());
            (serverApp.getPlaylist()).add(song);
        }
        catch(UnsupportedAudioFileException | IOException e)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("File not MP3");
            alert.setContentText("File format not recognized or file is corrupted!");

            alert.showAndWait();

        }
    }

    @FXML
    private void handleRemoveButton() {
        ObservableList<Song> play = null;
        play = serverApp.getPlaylist();
        Song del = playlist.getSelectionModel().getSelectedItem();
        playlist.getItems().removeAll(playlist.getSelectionModel().getSelectedItem());
        play.remove(del);
        serverApp.setPlaylist(play);
    }

    @FXML
    private void handleClearButton() {
        (serverApp.getPlaylist()).clear();
    }

    @FXML
    void handleStart(){
        if (serverApp.startPortListener()) {
            start.setDisable(true);
            disconnect.setDisable(false);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Look, an Error Dialog");
            alert.setContentText("Ooops, there was an error!");

            alert.showAndWait();
        }
    }

    @FXML
    private void handleDisconnect() {
        if (serverApp.stopPortListener()) {
            start.setDisable(false);
            disconnect.setDisable(true);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Look, an Error Dialog");
            alert.setContentText("Ooops, there was an error!");

            alert.showAndWait();

        }
    }

    @FXML
    private void initialize() {
        isPlayingColumn.setCellValueFactory(cellData -> cellData.getValue().isPlayingProperty());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().displayNameProperty());
        durationColumn.setCellValueFactory(cellData -> cellData.getValue().durationStringProperty());
    }

    public void setServerApp(ServerApp serverApp) {
        this.serverApp = serverApp;
        this.playlist.setItems(serverApp.getPlaylist());
    }
}
