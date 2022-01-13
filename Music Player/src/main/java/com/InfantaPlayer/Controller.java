package com.InfantaPlayer;

import com.jfoenix.controls.JFXSlider;
import com.mpatric.mp3agic.*;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.media.MediaPlayer.Status;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class Controller {

    /*
    fields' declaration(scenes)
     */
    @FXML
    private AnchorPane window;

    @FXML
    private AnchorPane playlistNode;

    @FXML
    private Pane showPlaylist;
    @FXML
    private Pane exit;
    @FXML
    private Pane minimize;
    @FXML
    private Pane imagePane;

    @FXML
    private TableView<Track> songTable;
    @FXML
    private TableColumn<Track, String> idColumn;
    @FXML
    private TableColumn<Track, String> artistColumn;
    @FXML
    private TableColumn<Track, String> songColumn;
    @FXML
    private TableColumn<Track, String> durationColumn;
    @FXML
    private TableColumn<Track, String> rateColumn;
    @FXML
    private TableColumn<Track, String> formatColumn;


    @FXML
    private Label artist;
    @FXML
    private Label album;
    @FXML
    private Label song;
    @FXML
    private Label totalDuration;
    @FXML
    private Label currentDuration;
    @FXML
    private Label volumeValue;
    @FXML
    private Label songsCounter;

    @FXML
    private JFXSlider songSlider;
    @FXML
    private Slider volumeSlider;

    @FXML
    private ImageView folderChooser;

    @FXML
    private ImageView playButton;
    @FXML
    private ImageView pauseButton;
    @FXML
    private ImageView nextSongButton;
    @FXML
    private ImageView previousSongButton;
    @FXML
    private ImageView muteIcon;
    @FXML
    private ImageView volumeIcon;
    @FXML
    private ToggleButton autoPlayIcon;

    /*
    scenes declaration(another)
     */
    @FXML
    private Stage stage;

    private Main main;

    /* vars declaration */
    private List<MediaPlayer> players;
    private MediaPlayer mediaPlayer;
    private MediaView mediaView;

    private boolean isAutoplay;
    private double volume = 10;
    private String path;

    private double xOffset = 0;
    private double yOffset = 0;

    private FadeTransition fadeIn = new FadeTransition();
    private FadeTransition fadeOut = new FadeTransition();


    /* constructor */
    public Controller() {
        players = new ArrayList<>();
        songSlider = new JFXSlider();
        isAutoplay = false;
        volume = 0.27;
        stage = Main.getStage();
        stage.getIcons().add(new Image(ClassLoader.getSystemResource("images/logo.png").toExternalForm()));
    }

    @FXML
    private void initialize() throws Exception {

        /*
        adding EventHandlers to all active scenes
         */

        window.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = stage.getX() - event.getScreenX();
                yOffset = stage.getY() - event.getScreenY();
            }
        });

        window.addEventHandler(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage.setX(event.getScreenX() + xOffset);
                stage.setY(event.getScreenY() + yOffset);
            }
        });

        autoPlayIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(isAutoplay) {
                    autoPlayIcon.setSelected(false);
                    isAutoplay = false;
                }
                else {
                    autoPlayIcon.setSelected(true);
                    isAutoplay = true;
                }
            }
        });

        showPlaylist.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(playlistNode.isVisible()) {
                    hideTransation(playlistNode);
                }
                else {
                    showTransation(playlistNode);
                }
            }
        });

        minimize.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage.setIconified(true);
            }
        });

        exit.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.exit(0);
            }
        });

        /* filling the table header */
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        artistColumn.setCellValueFactory(cellData -> cellData.getValue().artistProperty());
        songColumn.setCellValueFactory(cellData -> cellData.getValue().songProperty());
        durationColumn.setCellValueFactory(cellData -> cellData.getValue().durationProperty());
        rateColumn.setCellValueFactory(cellData -> cellData.getValue().rateProperty());
        formatColumn.setCellValueFactory(cellData -> cellData.getValue().formatProperty());

        /* default Song ToDo */
        showSongInfo(null);

        songTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> showSongInfo(newValue));

        folderChooser.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                DirectoryChooser chooser = new DirectoryChooser();
                File selectedDirectory = chooser.showDialog(stage);
                if(selectedDirectory == null) {
                    System.out.println("No directory selected!");
                }
                else {

                    try {
                        if(!(players.isEmpty())) {
                            players.clear();
                            System.out.println("new array list");
                        }
                        songTable.setItems(songsUrls(selectedDirectory));

                        songTable.setOnMouseClicked((MouseEvent e) -> {
                            if(e.getClickCount() == 1) {
                                try {
                                    takeCare();
                                }
                                catch (Exception ex) {};
                            }
                        });
                    }
                    catch(Exception e) {}
                }
            }
        });
    }

    /* "ranker" function */
    public void showSongInfo(Track track) {
        if(track != null) {
            artist.setText(track.getArtist());
            song.setText(track.getSong());
            album.setText(track.getFormat());
        }
        else {
            artist.setText("-");
            song.setText("-");
            album.setText("-");
        }

    }

    /* filling the table */
    public ObservableList<Track> songsUrls(File dir)   throws Exception{
        ObservableList<Track> trackData = FXCollections.observableArrayList();
        File[] files = dir.listFiles();
        String name;
        int i = 0;
        for(File file : files) {
            if(file.isFile()) {
                name = file.getName();

                if(name.endsWith("mp3") || name.endsWith("wav") || name.endsWith("m4a")) {
                    try {
                        i++;        // I'll use it *_*
                        Mp3File mp3 = new Mp3File(file.getPath());
                        ID3v2 tag = mp3.getId3v2Tag();
                        Track track = new Track(String.valueOf(i), tag.getArtist(), tag.getTitle(), kbToMb(file.length()), secToMin(mp3.getLengthInSeconds()),tag.getAlbum(), file.getAbsolutePath());
                        players.add(createPlayer(file.getAbsolutePath()));
                        trackData.add(track);
                    }
                    catch(IOException e) {e.printStackTrace();}
                }
            }
        }
        setImage();
        i = 0;
        System.out.println(players.size());
        songsCounter.setText("");
        songsCounter.setText("Songs: " + players.size());
        return trackData;
    }

    public void playPauseSong(Track track) throws Exception{
        if(track != null) {
            File file = new File(track.getUrl());
            String path = file.getAbsolutePath();
            path.replace("\\", "/");

            if((mediaView != null) && (mediaPlayer != null)) {
                volume = mediaView.getMediaPlayer().getVolume();
                mediaView.getMediaPlayer().stop();
                mediaView = null;
                mediaPlayer = null;
            }

            Media media = new Media(new File(path).toURI().toString());

            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.stop();
            mediaPlayer.setAutoPlay(false);

            mediaView = new MediaView(mediaPlayer);
            pauseIcon();
            mediaView = new MediaView(players.get(Integer.parseInt(track.getId()) - 1));

            volumeValue.setText(String.valueOf((int)volumeSlider.getValue()));
            volumeSlider.setValue(volume * 100);
            mediaView.getMediaPlayer().setVolume(volume);
            mediaView.getMediaPlayer().seek(Duration.ZERO);
            updateSliderPosition(Duration.ZERO);

            updateValues();
            mediaView.mediaPlayerProperty().addListener(new ChangeListener<MediaPlayer>() {
                @Override
                public void changed(ObservableValue<? extends MediaPlayer> observable, MediaPlayer oldValue, MediaPlayer newValue) {
                    try {
                        setCurrentlyPlayer(newValue);
                        updateValues();
                    }
                    catch(IOException | UnsupportedTagException | InvalidDataException e) {}
                }
            });


            playButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    mediaView.getMediaPlayer().play();
                    playIcon();
                    updateValues();
                    for (int i = ((players.indexOf(mediaView.getMediaPlayer())) % players.size()); i < players.size(); i++) {
                        final MediaPlayer player = players.get(i);
                        mediaPlayer = player;
                        final MediaPlayer nextPlayer = players.get((i + 1) % players.size());
                        mediaPlayer.setOnEndOfMedia(new Runnable() {
                            @Override
                            public void run() {
                                mediaView.getMediaPlayer().stop();
                                mediaView.getMediaPlayer().seek(Duration.ZERO);
                                if(isAutoplay) {
                                    mediaView.getMediaPlayer().seek(Duration.ZERO);
                                    repeatSongs();
                                    return;
                                }
                                mediaPlayer = nextPlayer;
                                mediaView.setMediaPlayer(mediaPlayer);
                                mediaView.getMediaPlayer().seek(Duration.ZERO);
                                updateSliderPosition(Duration.ZERO);
                                songSlider.setValue(0);
                                updateValues();
                                mediaPlayer.setVolume(volume);
                                mediaPlayer.play();
                                playIcon();
                            }
                        });
                        pauseSong();
                    }
                }
            });

            nextSongButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    seekAndUpdate(players.get(players.indexOf(mediaView.getMediaPlayer())).getTotalDuration());
                }
            });

            previousSongButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    seekAndUpdate(Duration.ZERO);
                }
            });



            songSlider.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    Bounds b1 = songSlider.getLayoutBounds();
                    double mouseX = event.getX();
                    double percent = (((b1.getMinX() + mouseX) * 100) / (b1.getMaxX() - b1.getMinX()));
                    songSlider.setValue((percent) / 100);
                    seekAndUpdate(new Duration(mediaView.getMediaPlayer().getTotalDuration().toMillis() * percent / 100));
                    songSlider.setValueFactory(slider ->
                            Bindings.createStringBinding(
                                    () -> (secToMin((long) mediaView.getMediaPlayer().getCurrentTime().toSeconds())),
                                    songSlider.valueProperty()
                            )
                    );
                }
            });

        }
        else {
            if(pauseButton.isVisible()) {
                if ((mediaPlayer != null) && (mediaView != null)) {
                    mediaPlayer = mediaView.getMediaPlayer();
                    mediaPlayer.stop();
                    mediaView = null;
                    mediaPlayer = null;
                }
                pauseIcon();
            }
            System.out.println("Song does not exist!");
        }
    }

    /* useless property -_- */
    public void setMain(Main main) {
        this.main = main;
    }

    /*
    usefull convertors
    */
    public String kbToMb(long length) {
        Long l = length;
        double d = l.doubleValue();
        DecimalFormat df = new DecimalFormat("#.00");
        String form = df.format((d/1024)/1024);
        return form + "Mb";
    }

    public String secToMin(long sec) {
        Long s = sec;
        String time = null;
        if((s%60) < 10) {
            time = s/60 + ":0" + s%60;
        }
        else {
            time = s/60 + ":" + s%60;
        }
        return time;
    }

    /* creators */
    public MediaPlayer createPlayer(String url) {
        url.replace("\\", "/");
        final Media media = new Media(new File(url).toURI().toString());
        final MediaPlayer player = new MediaPlayer(media);
        System.out.println("+++++ " + url);
        return player;
    }

    public Media createMedia(String url) {
        url.replace("\\", "/");
        final Media media = new Media(new File(url).toURI().toString());
        return media;
    }

    /* methods for re-drawing play button*/
    public void playIcon() {
        playButton.setVisible(false);
        playButton.setDisable(true);
        pauseButton.setVisible(true);
        pauseButton.setDisable(false);
    }

    public void pauseIcon() {
        pauseButton.setVisible(false);
        pauseButton.setDisable(true);
        playButton.setVisible(true);
        playButton.setDisable(false);
    }

    /* they all got together now */
    public void setCurrentlyPlayer(MediaPlayer player) throws InvalidDataException, IOException, UnsupportedTagException {
        String source = player.getMedia().getSource();
        source = source.replace("/","\\");
        source = source.replaceAll("%20", " ");
        source = source.replaceAll("%5B", "[");
        source = source.replaceAll("%5D", "]");
        source = source.substring(6,source.length());
        System.out.println(source + " +++");
        Mp3File mp3 = new Mp3File(source);
        ID3v2 tag = mp3.getId3v2Tag();
        artist.setText(tag.getArtist());
        song.setText(tag.getTitle());
        album.setText(tag.getAlbum());
    }

    /* for songs' table EventHandler */
    public void takeCare() throws Exception {
        if(songTable.getSelectionModel().getSelectedItem() != null) {
            Track track = songTable.getSelectionModel().getSelectedItem();
            playPauseSong(track);
        }
        else {
            System.out.println("null");
        }
    }

    /*
    update methods
     */
    private void seekAndUpdate(Duration duration) {
        final MediaPlayer player = players.get(players.indexOf(mediaView.getMediaPlayer()));

        player.seek(duration);
    }

    private void updateValues() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                do {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            final MediaPlayer player = mediaView.getMediaPlayer();
                            if((player.getStatus() != Status.PAUSED) && (player.getStatus() != Status.STOPPED) && (player.getStatus() != Status.READY)) {
                                double tduration = player.getTotalDuration().toSeconds();
                                totalDuration.setText(secToMin((long) tduration));
                                currentDuration.setText(secToMin((long) player.getCurrentTime().toSeconds()));
                                updateSliderPosition(player.getCurrentTime());
                                volumeHandler();
                            }
                        }
                    });
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        break;
                    }
                }
                while(!players.isEmpty());
            }
        });
        thread.start();
    }

    private void updateSliderPosition(Duration currentTime) {
        final MediaPlayer player = mediaView.getMediaPlayer();
        final Duration totalDuration = player.getTotalDuration();
        if((totalDuration == null) || (currentTime == null)) {
            songSlider.setValue(0);
        }
        else {
            songSlider.setValue((currentTime.toMillis() / totalDuration.toMillis()) * 100);
        }
    }

    /* volume handling */
    private void volumeHandler() {
        volumeSlider.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                mediaView.getMediaPlayer().setVolume(volumeSlider.getValue() / 100);
                volumeValue.setText(String.valueOf((int)volumeSlider.getValue()));
                volume = mediaView.getMediaPlayer().getVolume();
                volumeIconChanger();
            }
        });
    }

    private void volumeIconChanger() {
        if(volumeSlider.getValue() == 0) {
            muteIcon.setVisible(true);
            volumeIcon.setVisible(false);
        }
        else {
            muteIcon.setVisible(false);
            volumeIcon.setVisible(true);
        }
    }

    /* flexing with anchor */
    private void showTransation(AnchorPane anchorPane) {
        fadeIn.setNode(anchorPane);
        fadeIn.setDuration(Duration.millis(1000));
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        anchorPane.setVisible(true);
        fadeIn.play();
    }

    private void hideTransation(AnchorPane anchorPane) {
        fadeOut.setNode(anchorPane);
        fadeOut.setDuration(Duration.millis(1000));
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        anchorPane.setVisible(false);
        fadeOut.play();
    }

    private void setImage() throws Exception {
        String path = "";
        path = path.replace("\\", "/");
        path = path.replace(" ", "%20");
        //path = "file:/" + path;
        path = ClassLoader.getSystemResource("images/Question.PNG").toExternalForm();
        System.out.println(path);

        imagePane.setStyle("-fx-background-image: url(\"" + path + "\"); " +
                "-fx-background-position: center center; " +
                "-fx-background-repeat: stretch;");

    }

    private void repeatSongs(){                                     // Who am I?
        mediaView.getMediaPlayer().setOnRepeat(new Runnable() {     // I'm the new Runnable
            @Override
            public void run() {
                mediaView.getMediaPlayer().seek(Duration.ZERO);
            }
        });
        if(isAutoplay) {
            mediaView.getMediaPlayer().play();
            playIcon();
        }
        else return;
    }

    /* we have to stop it *~*  */
    private void pauseSong() {
        mediaView.getMediaPlayer().setAutoPlay(true);
        pauseButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (mediaView.getMediaPlayer().getStatus() == MediaPlayer.Status.PLAYING) {
                    mediaView.getMediaPlayer().pause();
                    pauseButton.setVisible(false);
                    pauseButton.setDisable(true);
                    playButton.setVisible(true);
                    playButton.setDisable(false);
                }
            }
        });
    }
}

