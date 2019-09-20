package org.noear.solonjt.executor.m.velocity;

import org.noear.solon.XApp;
import org.noear.solon.core.XPlugin;
import org.noear.solonjt.executor.ExecutorFactory;

public class XPluginImp implements XPlugin {

    @Override
    public void start(XApp app) {
        ExecutorFactory.register(VelocityJtExecutor.singleton());
    }
}
