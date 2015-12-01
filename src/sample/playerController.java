package sample;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;

public class playerController extends Main{

    Media media;
    MediaPlayer mediaPlayer;
    Duration duration;

    @FXML private Button playButton;
    @FXML private Button pauseButton;
    @FXML private HBox bottomHBox;
    @FXML private ToggleButton repeatButton;
    @FXML private ToolBar toolBar;
    @FXML private MenuBar menuBar;
    @FXML private Menu file;
    @FXML private MenuItem openFile;
    @FXML private Slider timeSlider;
    @FXML private MediaView mediaView;
    @FXML private BorderPane borderPane;
    @FXML private VBox vBoxTop;
    @FXML private Label mediaName;
    @FXML private TabPane tabPane;
    @FXML private Tab playerTab;
    @FXML private Label playTime;
    @FXML private VBox vBox;

    @FXML public void setOpenFile(){
        FileChooser chooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter =
                new FileChooser.ExtensionFilter("Media Files", "*.mp3", "*.mp4", "*.m4a", "*.wav");
        chooser.getExtensionFilters().add(extensionFilter);
        File file = chooser.showOpenDialog(new Stage());
        //bottomHBox.setHgrow(timeSlider, Priority.ALWAYS);
        //bottomHBox.setMaxWidth(Double.MAX_VALUE);

        if (file != null) {
            if (media != null) {
                mediaPlayer.stop();
            }
            String path = file.getAbsolutePath();
            path.replace("\\", "/");
            media = new Media((new File(path).toURI().toString()));
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setAutoPlay(true);
            mediaView.setMediaPlayer(mediaPlayer);

            String fileName = file.getName();
            String fileExtension = fileName.substring(fileName.indexOf(".") + 1, file.getName().length());
            mediaName.setText(fileName);

            mediaPlayer.setOnReady(new Runnable() {
                @Override
                public void run() {
                    duration = mediaPlayer.getMedia().getDuration();
                    updateTimeSliderValues();
                }
            });
            mediaPlayer.currentTimeProperty().addListener(new InvalidationListener() {
                @Override
                public void invalidated(Observable observable) {
                    updateTimeSliderValues();
                }
            });
            timeSlider.valueProperty().addListener(new InvalidationListener() {
                @Override
                public void invalidated(Observable observable) {
                    if (timeSlider.isValueChanging()){
                        if (duration != null) {
                            mediaPlayer.seek(duration.multiply(timeSlider.getValue() / 100.0));
                        }
                        updateTimeSliderValues();
                    }
                }
            });
            /* See parandab probleemi - kui avada muusika
             * fail, siis akna resizimisel kadus toolbar ara
             * AJUTINE
             */
            if (fileExtension.equals("mp4")) {
                setMediaView();
            }
        } else {
            System.out.println("No file was chosen");
        }
    }
    @FXML public void setPlayButton(ActionEvent event){
        if (media != null) {
            mediaPlayer.play();
            System.out.println("Playing");
        }
    }
    @FXML public void setPauseButton(ActionEvent event) {
        if (media != null) {
            mediaPlayer.pause();
        }
    }
    @FXML public void setRepeatButton(ActionEvent event){
        if (media != null) {
            mediaPlayer.setCycleCount(mediaPlayer.INDEFINITE);
        }
    }
    public void setMediaView() {
        borderPane.heightProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                mediaView.fitHeightProperty().bind(borderPane.getScene().heightProperty().subtract(150));
            }
        });
        borderPane.widthProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                mediaView.fitWidthProperty().bind(borderPane.getScene().widthProperty());
            }
        });
    }
    public void updateTimeSliderValues(){
        if (timeSlider != null && playTime != null) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Duration currentTime = mediaPlayer.getCurrentTime();
                    playTime.setText(formatTime(currentTime, duration));
                    timeSlider.setDisable(duration.isUnknown());
                    if (!timeSlider.isDisabled() && duration.greaterThan(Duration.ZERO) && !timeSlider.isValueChanging()) {
                        timeSlider.setValue(currentTime.divide(duration.toMillis()).toMillis() * 100.0);
                    }
                }
            });
        }
    }
    public static String formatTime(Duration elapsed, Duration duration) {
        int intElapsed = (int) Math.floor(elapsed.toSeconds());
        int elapsedHours = intElapsed / (60 * 60);
        if (elapsedHours > 0) {
            intElapsed -= elapsedHours * 60 * 60;
        }
        int elapsedMinutes = intElapsed / 60;
        int elapsedSeconds = intElapsed - elapsedHours * 60 * 60 - elapsedMinutes * 60;

        if (duration.greaterThan(Duration.ZERO)) {
            int intDuration = (int) Math.floor(duration.toSeconds());
            int durationHours = intDuration / (60 * 60);
            if (durationHours > 0) {
                intDuration -= durationHours * 60 * 60;
            }
            int durationMinutes = intDuration / 60;
            int durationSeconds = intDuration - durationHours * 60 * 60 - durationMinutes * 60;

            if (durationHours > 0) {
                return String.format("%d:%02d:%02d/%d:%02d:%02d",
                        elapsedHours, elapsedMinutes, elapsedSeconds,
                        durationHours, durationMinutes, durationSeconds);
            } else {
                return String.format("%02d:%02d/%02d:%02d",
                        elapsedMinutes, elapsedSeconds,
                        durationMinutes, durationSeconds);
            }
        } else {
            if (elapsedHours > 0) {
                return String.format("%d:%02d:%02d",
                        elapsedHours, elapsedMinutes, elapsedSeconds);
            } else {
                return String.format("%02d:%02d",
                        elapsedMinutes, elapsedSeconds);
            }
        }
    }
    public void doubleClickFullScreen(TabPane root, Stage primaryStage){
        root.setOnMousePressed(e -> {
            if (e.getClickCount() == 2) {
                primaryStage.setFullScreen(!primaryStage.isFullScreen());
            }
        });
    }
}
