package org.noear.localjt;

import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class WebViewBuilder {

    private String url;

    public void setUrl(String url) {
        this.url = url;
    }

    public WebView build(){
        final WebView webView = new WebView();
        final WebEngine webEngine = webView.getEngine();


        webView.setContextMenuEnabled(true);
        webView.autosize();




        webEngine.load(url);


        //        webEngine.setJavaScriptEnabled(true);
//        webEngine.setOnAlert(event -> {
//            Alert alert = new Alert(Alert.AlertType.INFORMATION);
//            alert.setHeaderText("Alert!");
//            alert.setContentText(event.toString());
//            alert.showAndWait();
//        });


//        webEngine.getLoadWorker().stateProperty()
//                .addListener((ov, oldState, newState) -> {
//                    if (newState == Worker.State.SUCCEEDED) {
//                        stage.setTitle(webEngine.getLocation());
//                    }
//                });


        return webView;
    }
}
