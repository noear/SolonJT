package org.noear.thinkjt;

import org.noear.solonjt.SolonJT;
import org.noear.weed.WeedConfig;

public class App {
    public static void main(String[] args) {
        WeedConfig.onExecuteBef((cmd)->{
            System.out.println(cmd.text);
            System.out.println(cmd.paramMap());
            return true;
        });


        SolonJT.start(App.class, args);
    }
}
