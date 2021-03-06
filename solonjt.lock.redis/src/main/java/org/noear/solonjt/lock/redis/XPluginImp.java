package org.noear.solonjt.lock.redis;

import org.noear.solon.XApp;
import org.noear.solon.core.XPlugin;
import org.noear.solonjt.dso.JtBridge;

import java.util.Properties;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        Properties prop = app.prop().getProp("solonjt.lock.redis");

        if (prop != null && prop.size() >= 5) {
            JtBridge.lockSet(new RedisJtLock(prop));
        }
    }
}
