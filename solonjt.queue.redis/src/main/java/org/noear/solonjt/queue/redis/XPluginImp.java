package org.noear.solonjt.queue.redis;

import org.noear.solon.XApp;
import org.noear.solon.core.XPlugin;
import org.noear.solonjt.dso.JtConstants;

import java.util.Properties;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        Properties prop = app.prop().getProp("solonjt.queue.redis");

        if (prop != null && prop.size() == 5) {
            JtConstants.queueFactorySet((name) -> new RedisJtQueue(name, prop));
        }
    }
}
