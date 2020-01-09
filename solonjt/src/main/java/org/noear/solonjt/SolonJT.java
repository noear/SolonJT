package org.noear.solonjt;

import org.noear.solonjt.dso.*;
import org.noear.solon.XApp;
import org.noear.solon.core.*;
import org.noear.solonjt.utils.TextUtils;
import org.noear.weed.xml.XmlSqlLoader;

public class SolonJT {
    public static XApp start(Class<?> source, String[] args) {

        XmlSqlLoader.tryLoad();

        //0.构建参数
        XMap xarg = XMap.from(args);

        //1.获取扩展目录
        String extend = InitUtil.tryInitExtend(xarg);

        if (extend == null) {
            throw new RuntimeException("Please enter an 'extend' parameter!");
        }

        //2.初始化扩展目录（包括：配置、jar）
        ExtendUtil.init(extend);
        xarg.put("extend", extend);

        InitXfunUtil.init();

        //3.初始化执行器工厂
        JtBridge.executorAdapterSet(JtAdapter.global);
        JtBridge.configAdapterSet(JtAdapter.global);

        //4.启动服务
        XApp app = XApp.start(source, xarg, (x) -> {

            String def_exec = x.prop().get("solonjt.executor.default");
            if(TextUtils.isEmpty(def_exec) == false){
                JtAdapter.global.defaultExecutorSet(def_exec);
            }

            x.sharedAdd("XFun", JtFun.g);
            x.sharedAdd("XMsg", JtMsg.g);
            x.sharedAdd("XUtil", JtUtilEx.g2);
            x.sharedAdd("XLock", JtLock.g);

            //不再支持
            x.sharedAdd("XBus", JtMsg.g);//为兼容旧版本
        });

        //4.1.加载自己的bean
        app.loadBean(SolonJT.class);

        //5.初始化功能
        if (app.prop().size() < 4) {
            //5.1.如果没有DB配置；则启动配置服务
            AppUtil.runAsInit(app, extend);
        } else {
            //5.2.如果有DB配置了；则启动工作服务
            AppUtil.init(app,true);

            AppUtil.runAsWork(app);
        }

        return app;
    }
}
