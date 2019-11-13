package org.noear.solonjt;

import org.noear.solonjt.dso.*;
import org.noear.solon.XApp;
import org.noear.solon.core.*;
import org.noear.solonjt.utils.TextUtils;
import org.noear.weed.xml.XmlSqlLoader;

public class SolonJT {
    public static void start(Class<?> source, String[] args) {

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
        JtAdapter jtAdapter = new JtAdapter();
        JtConstants.adapterSet(jtAdapter);

        //4.启动服务
        XApp app = XApp.start(source, xarg, (x) -> {

            String def_exec = x.prop().get("solonjt.executor.default");
            if(TextUtils.isEmpty(def_exec) == false){
                jtAdapter.defaultExecutorSet(def_exec);
            }

            x.sharedAdd("XFun", JtFun.g);
            x.sharedAdd("XBus", JtMsg.g);
            x.sharedAdd("XUtil", JtUtil.g);
            x.sharedAdd("XLock", JtLock.g);

            x.sharedAdd("JtFun", JtFun.g);
            x.sharedAdd("JtMsg", JtMsg.g);
            x.sharedAdd("JtUtil", JtUtil.g);
            x.sharedAdd("JtLock", JtLock.g);
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
    }
}
