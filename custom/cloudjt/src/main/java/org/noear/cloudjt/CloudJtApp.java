package org.noear.cloudjt;

import org.noear.solon.XApp;
import org.noear.solonjt.SolonJT;
import org.noear.solonjt.dso.PluginUtil;
import org.noear.solonjt.utils.TextUtils;
import org.noear.weed.WeedConfig;

public class CloudJtApp {
    public static void main(String[] args) {
        WeedConfig.onException((cmd, err) -> {
            err.printStackTrace();
        });

//        WeedConfig.onExecuteBef((cmd) -> {
//            System.out.println(cmd.text);
//            System.out.println(cmd.paramMap());
//            return true;
//        });


        SolonJT.start(CloudJtApp.class, args, () -> {
            String add = XApp.cfg().argx().get("add");
            String home = XApp.cfg().argx().get("home");
            String title = XApp.cfg().argx().get("title");

            String init = XApp.cfg().argx().get("init");

            PluginUtil.add(add);

            //::1.初始化调用
            PluginUtil.initCall(init);

            //::2.
            if (TextUtils.isEmpty(home) == false) {
                PluginUtil.initCfg("upassport_jump_def", home);
            }

            if (TextUtils.isEmpty(title) == false) {
                PluginUtil.initCfg("_frm_admin_title", title + " of solonjt");
                PluginUtil.initCfg("ucenter__title", title);
            }
        });
    }
}
