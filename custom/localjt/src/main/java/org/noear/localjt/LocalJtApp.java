package org.noear.localjt;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import org.noear.solon.XApp;
import org.noear.solon.core.XMap;
import org.noear.solonjt.SolonJT;
import org.noear.solonjt.dso.PluginUtil;
import org.noear.solonjt.utils.TextUtils;
import org.noear.weed.WeedConfig;


public class LocalJtApp  extends Application {

    private static String home;
    private static String title;

    @Override
    public void start(Stage window) throws Exception {
        window.getIcons().clear();
        window.setMinWidth(getVisualScreenWidth() * 0.8);
        window.setMinHeight(getVisualScreenHeight() * 0.5);
        window.centerOnScreen();
        window.setOnCloseRequest(cl -> System.exit(0));

        Scene scene = new Scene(new Group());
        WebViewBuilder builder = new WebViewBuilder();
        builder.setUrl(home);

        scene.setRoot(builder.build());

        window.setScene(scene);
        window.setTitle(title);
        window.show();
    }

    public static void main(String[] args) {

        WeedConfig.onException((cmd, err) -> {
            err.printStackTrace();
        });


        XApp app = SolonJT.start(LocalJtApp.class, args);

        app.onError((ctx, err) -> {
            err.printStackTrace();
        });

        XMap argx = app.prop().argx();

        //添加插件
        PluginUtil.add(argx.get("add"));
        //添加插件
        PluginUtil.udp(argx.get("upd"));
        //移徐插件
        PluginUtil.rem(argx.get("rem"));

        //主页
        home = argx.get("home");

        if (TextUtils.isEmpty(home)) {
            home = "http://localhost:" + app.port() + "/.admin/?_L0n5=1CE24B1CF36B0C5B94AACE6263DBD947FFA53531";
        }else{
            home = "http://localhost:" + app.port() + home;
        }

        //标题
        title = argx.get("title");
        if(TextUtils.isEmpty(title)){
            title = "LocalJt";
        }

        launch(args);
    }

    public static double getVisualScreenWidth() {
        return Screen.getPrimary().getVisualBounds().getWidth();
    }

    public static double getVisualScreenHeight() {
        return Screen.getPrimary().getVisualBounds().getHeight();
    }

}
