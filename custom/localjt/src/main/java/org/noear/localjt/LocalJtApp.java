package org.noear.localjt;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.scene.Group;
import javafx.scene.Scene;
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

        final WebView browser = new WebView();
        final WebEngine webEngine = browser.getEngine();

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setContent(browser);

        webEngine.getLoadWorker().stateProperty()
                .addListener(new ChangeListener<State>() {
                    @Override
                    public void changed(ObservableValue ov, State oldState, State newState) {

//                        if (newState == Worker.State.SUCCEEDED) {
//                            stage.setTitle(webEngine.getLocation());
//                        }

                    }
                });
        webEngine.load(url);

        scene.setRoot(scrollPane);

        stage.setScene(scene);
        stage.setTitle("SolonFx");
        stage.show();
    }

    public static void main(String[] args) {

        WeedConfig.onException((cmd,err) -> {
            err.printStackTrace();
        });


        XApp app = SolonJT.start(LocalJtApp.class, args);
        app.onError((ctx,err)->{ err.printStackTrace(); });


        //url  = "http://jtx.noear.org/.admin/?_L0n5=81057AF6D4931710A5370514A4EE2DB5D2033055"; //

        url = "http://localhost:" + app.port() +"/.admin/?_L0n5=1CE24B1CF36B0C5B94AACE6263DBD947FFA53531";

        Application.launch(args);

//        if (java.awt.Desktop.isDesktopSupported()) {
//            try {
//                // 创建一个URI实例
//                java.net.URI uri = java.net.URI.create("http://localhost:" + port);
//                // 获取当前系统桌面扩展
//                java.awt.Desktop dp = java.awt.Desktop.getDesktop();
//                // 判断系统桌面是否支持要执行的功能
//                if (dp.isSupported(java.awt.Desktop.Action.BROWSE)) {
//                    // 获取系统默认浏览器打开链接
//                    dp.browse(uri);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
    }
}
