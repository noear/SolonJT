package org.noear.thinkjt;

import org.noear.solon.XApp;
import org.noear.solonjt.SolonJT;
import org.noear.solonjt.dso.PluginUtil;
import org.noear.solonjt.executor.ExecutorFactory;
import org.noear.weed.WeedConfig;

import java.util.HashMap;
import java.util.Map;

public class ThinkJtApp {
    public static void main(String[] args) {
        WeedConfig.onExecuteBef((cmd) -> {
            System.out.println(cmd.text);
            System.out.println(cmd.paramMap());
            return true;
        });


        SolonJT.start(ThinkJtApp.class, args, () -> {
            PluginUtil.add(XApp.cfg().argx().get("add"));
        });


        try{
            Map<String,Object> map = new HashMap<>();
            map.put("aaa",12);

           Object tmp =  ExecutorFactory.exec("javascript","Datetime.Now()",map);
           if(tmp == null){
               return;
           }
        }catch (Throwable ex){
            ex.printStackTrace();
        }
    }
}
