package org.noear.solonjt;

import org.noear.solonjt.dso.*;
import org.noear.solon.XApp;
import org.noear.solon.core.*;
import org.noear.solonjt.executor.ExecutorFactory;

public class SolonJT {
    public static void start(Class<?> source, String[] args, String defActuator) {

        //0.构建参数
        XMap xarg = XMap.from(args);

        //1.获取扩展目录
        String extend = InitUtil.tryInitExtend(xarg);

        if (extend == null) {
            throw new RuntimeException("Please enter an 'extend' parameter!");
        }

        //2.加载扩展目录（包括：配置、jar）
        ExtendUtil.init(extend);
        ExtendLoader.load(extend, xarg);

        xarg.put("extend",extend);

        XFun.g.set("log","记录日志#tag,tag1?,tag2?,tag3?,tag4?,level?,summary?,content?",DbApi::log);
        XFun.g.set("xbus_publish","发布消息#topic,content",1,DbApi::msgAdd);
        XFun.g.set("cfg_get","获取配置#name#{}",1,DbApi::cfgGetMap);

        //3.初始化执行器工厂
        ExecutorFactory.init(new ActuatorFactoryAdapter(defActuator));

        //4.启动服务
        XApp app = XApp.start(source, xarg,(x)->{
            x.sharedAdd("XFun", XFun.g);
            x.sharedAdd("XBus", XBus.g);
            x.sharedAdd("XUtil", XUtil.g);
        });

        //4.1.加载自己的bean
        app.loadBean(SolonJT.class);

        //5.初始化功能
        if (xarg.size() < 4) {
            //5.1.如果没有DB配置；则启动配置服务
            AppUtil.runAsInit(app,extend);
        } else {
            //5.2.如果有DB配置了；则启动工作服务
            AppUtil.init(xarg, true);

            AppUtil.runAsWork(app);
        }
    }
}
