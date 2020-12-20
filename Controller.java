package sample;

import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    private int position = 0;
    @FXML
    private TextField txt;
    @FXML
    private Button find;
    @FXML
    private WebView webView;
    private List<String> history = new ArrayList<>();
    @FXML
    private Button back;
    @FXML
    private Button forward;
    @FXML
    private Label status;
    private WebEngine webEngine;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        webEngine = webView.getEngine();
        Worker<Void> worker = webEngine.getLoadWorker();
        worker.stateProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == Worker.State.SUCCEEDED) {
                    status.setText("Successful");
                } else status.setText("Error");
            }
        );
        status.setText("Hello, world!");
        EventHandler<ActionEvent> enter = event -> {
            String str;
            if (txt.getText().startsWith("http://") || txt.getText().startsWith("https://")) str = txt.getText();
            else str = "https://" + txt.getText();
            //else str = "http://" + txt.getText();
            webEngine.load(str);
            history.add(webEngine.getLocation());
            position++;
        };
        txt.setOnAction(enter);
        find.setOnAction(enter);

        back.setOnAction(event -> {
           if (position > 0) {
               position--;
               String str = history.get(position);
               txt.setText(str);
               webEngine.load(str);
           } else status.setText("Вы не можете вернуться назад");
        });

        forward.setOnAction(event -> {
            if (position < history.size()) {
                position++;
                String str = history.get(position);
                txt.setText(str);
                webEngine.load(str);
            } else status.setText("Вы не можете перейти вперёд");
        });

    }

    public void openHistory() {
        VBox root = new VBox();
        for (int i = 0; i < history.size(); i++) {
                Label label = new Label(history.get(i));
                label.setOnMouseClicked(event1 -> {
                    txt.setText(label.getText());
                    webEngine.load(label.getText());
                    position = history.lastIndexOf(label.getText());
                });
                root.getChildren().add(label);
            }
        Stage stage = new Stage();
        stage.setTitle("История");
        stage.setScene(new Scene(root, 250, 300));
        stage.show();
    }

}
