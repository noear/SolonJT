package org.noear.solonjt.event.message;

import org.noear.solon.XApp;
import org.noear.solon.core.XPlugin;
import org.noear.solonjt.dso.JtFun;
import org.noear.solonjt.task.TaskFactory;
import org.noear.solonjt.event.message.controller.MessageTask;
import org.noear.solonjt.event.message.dso.DbMsgApi;
import org.noear.weed.DbContext;
import org.noear.weed.cache.ICacheServiceEx;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        JtFun.g.set("xbus_publish","发布消息#topic,content,delay?",1, DbMsgApi::msgPublish);
        JtFun.g.set("xbus_forward","转发消息，多级主题层层递进#topic,content,topic_source,delay?",1,DbMsgApi::msgRorward);

        app.sharedGet("db", (DbContext db)->{
            Config.db = db;
        });

        app.sharedGet("cache", (ICacheServiceEx cache)->{
            Config.cache = cache;
        });

        TaskFactory.register(new MessageTask());
    }
}
