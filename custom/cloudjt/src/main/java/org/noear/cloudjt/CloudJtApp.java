package org.noear.cloudjt;

import org.noear.solon.XApp;
import org.noear.solonjt.SolonJT;
import org.noear.solonjt.dso.JtUtil;
import org.noear.solonjt.dso.PluginUtil;
import org.noear.solonjt.utils.TextUtils;
import org.noear.weed.WeedConfig;

public class CloudJtApp {
    public static void main(String[] args) {
        WeedConfig.onExecuteBef((cmd) -> {
            System.out.println(cmd.text);
            System.out.println(cmd.paramMap());
            return true;
        });


        SolonJT.start(CloudJtApp.class, args, () -> {
            String add = XApp.cfg().argx().get("add");
            String home = XApp.cfg().argx().get("home");
            String title = XApp.cfg().argx().get("title");

            PluginUtil.add(add);

            if (TextUtils.isEmpty(home) == false) {
                PluginUtil.cfgSet("upassport_jump_def", home);
            }

            if (TextUtils.isEmpty(title) == false) {
                PluginUtil.cfgSet("_frm_admin_title", title + " of solonjt");
            }
        });
    }
}
