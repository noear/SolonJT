package org.noear.localjt;

import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.noear.localjt.widget.JsDialog;

public class WebViewBuilder {

    private String url;

    public void setUrl(String url) {
        this.url = url;
    }

    public WebView build(){
        final WebView webView = new WebView();
        final WebEngine webEngine = webView.getEngine();

        webView.autosize();

        webEngine.setOnAlert((event)->{
            JsDialog.alert(event.getData());
        });

        webEngine.setConfirmHandler((txt)-> JsDialog.confirm(txt));

        webEngine.setPromptHandler((param -> JsDialog.prompt(param)));

//        webEngine.setCreatePopupHandler(param -> JsDialog.popup());

        webEngine.load(url);

        return webView;
    }


}
