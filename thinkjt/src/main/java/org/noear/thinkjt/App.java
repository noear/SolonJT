package org.noear.thinkjt;

import org.noear.solonjt.SolonJT;

public class App {
    public static void main(String[] args){
//        WeedConfig.onExecuteAft((cmd)->{
//            System.out.println(cmd.text);
//        });

        SolonJT.start(App.class,args,"freemarker");
    }
}
