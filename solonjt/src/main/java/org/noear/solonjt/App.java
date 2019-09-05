package org.noear.solonjt;

import org.noear.solonjt.dso.*;
import org.noear.solon.XApp;
import org.noear.solon.core.*;
import org.noear.solonjt.engine.EngineFactory;

public class App {

    public static void main(String[] args) {

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

        //3.初始化引擎工厂
        EngineFactory.init((file,err)->{
            LogUtil.log("_file", file.tag, file.path, 0, "", err);
        },AFileUtil::get);

        //4.启动服务
        XApp app = XApp.start(App.class, xarg,(x)->{});
        app.sharedAdd("XFun", XFun.g);
        app.sharedAdd("XUtil", XUtil.g);


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
