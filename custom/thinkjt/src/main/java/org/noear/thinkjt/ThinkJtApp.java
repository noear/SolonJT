package org.noear.thinkjt;

import org.noear.solon.XApp;
import org.noear.solon.core.XMap;
import org.noear.solonjt.SolonJT;
import org.noear.solonjt.dso.PluginUtil;
import org.noear.weed.WeedConfig;

public class ThinkJtApp {
    public static void main(String[] args) {
        WeedConfig.onExecuteBef((cmd)->{
            System.out.println(cmd.text);
            System.out.println(cmd.paramMap());
            return true;
        });


        SolonJT.start(ThinkJtApp.class, args);

        PluginUtil.add(XApp.cfg().get("add"));
    }
}
