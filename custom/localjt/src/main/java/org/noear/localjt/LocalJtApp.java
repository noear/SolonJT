package org.noear.localjt;

import org.noear.localjt.dso.WebShell;
import org.noear.solon.XApp;
import org.noear.solon.core.XMap;
import org.noear.solonjt.SolonJT;
import org.noear.solonjt.dso.PluginUtil;
import org.noear.solonjt.utils.TextUtils;
import org.noear.weed.WeedConfig;

public class LocalJtApp{

    public static String home;
    public static String title;
    public static String plugin_add;

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
        plugin_add = argx.get("add");
        PluginUtil.add(plugin_add);
        //添加插件
        PluginUtil.udp(argx.get("upd"));
        //移徐插件
        PluginUtil.rem(argx.get("rem"));

        //主页
        home = argx.get("home");

        if (TextUtils.isEmpty(home)) {
            home = "http://localhost:" + app.port() + "/.admin/?_L0n5=1CE24B1CF36B0C5B94AACE6263DBD947FFA53531";
        } else {
            home = "http://localhost:" + app.port() + home;
        }

        //标题
        title = argx.get("title");
        if (TextUtils.isEmpty(title)) {
            title = "LocalJt";
        }

        WebShell.start(args);
    }

}
