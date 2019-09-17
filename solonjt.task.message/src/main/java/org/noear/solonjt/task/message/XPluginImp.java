package org.noear.solonjt.task.message;

import org.noear.solon.XApp;
import org.noear.solon.core.XPlugin;
import org.noear.solonjt.actuator.TaskFactory;
import org.noear.solonjt.task.message.controller.MessageTask;
import org.noear.weed.DbContext;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        app.sharedGet("db", (DbContext db)->{
            Config.db = db;
        });

        TaskFactory.register(new MessageTask());
    }
}
