package org.noear.localjt;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import org.noear.solon.XApp;
import org.noear.solonjt.SolonJT;
import org.noear.weed.WeedConfig;


public class LocalJtApp  extends Application {

    private static String url;

    @Override
    public void start(Stage stage) throws Exception {
        stage.setMinWidth(800);
        stage.setMinHeight(500);

        Scene scene = new Scene(new Group());

        final WebView webBrowser = new WebView();
        final WebEngine webEngine = webBrowser.getEngine();

        webBrowser.autosize();

//        webEngine.setJavaScriptEnabled(true);
//        webEngine.setOnAlert(event -> {
//            Alert alert = new Alert(Alert.AlertType.INFORMATION);
//            alert.setHeaderText("Alert!");
//            alert.setContentText(event.toString());
//            alert.showAndWait();
//        });


        webEngine.getLoadWorker().stateProperty()
                .addListener((ov, oldState, newState) -> {
                    if (newState == Worker.State.SUCCEEDED) {
                        stage.setTitle(webEngine.getLocation());
                    }
                });

        webEngine.load(url);

        scene.setRoot(webBrowser);

        stage.setScene(scene);
        stage.setTitle("LocalJt");
        stage.show();
    }

    public static void main(String[] args) {

        WeedConfig.onException((cmd, err) -> {
            err.printStackTrace();
        });


        XApp app = SolonJT.start(LocalJtApp.class, args);

        app.onError((ctx, err) -> {
            err.printStackTrace();
        });

        url = "http://localhost:" + app.port() + "/.admin/?_L0n5=1CE24B1CF36B0C5B94AACE6263DBD947FFA53531";

        Application.launch(args);
    }
}
