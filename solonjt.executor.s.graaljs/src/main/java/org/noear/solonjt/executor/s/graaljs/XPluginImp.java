package org.noear.solonjt.executor.s.graaljs;

import org.noear.solon.XApp;
import org.noear.solon.core.XPlugin;
import org.noear.solonjt.executor.ExecutorFactory;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        ExecutorFactory.register(GraaljsJtExecutor.singleton());
        //不能替代（不能识别，java的多态）
        //ExecutorFactory.register("javascript",GraaljsJtExecutor.singleton(),1);
    }
}
