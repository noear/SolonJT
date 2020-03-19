package org.noear.cloudjt;

import org.noear.solon.XApp;
import org.noear.solonjt.SolonJT;
import org.noear.solonjt.dso.PluginUtil;
import org.noear.weed.WeedConfig;

public class CloudJtApp {
    public static void main(String[] args) {
        WeedConfig.onExecuteBef((cmd) -> {
            System.out.println(cmd.text);
            System.out.println(cmd.paramMap());
            return true;
        });


        SolonJT.start(CloudJtApp.class, args, () -> {
            PluginUtil.add(XApp.cfg().argx().get("add"));
        });
    }
}
