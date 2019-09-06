package org.noear.solonjt.engine.groovy;

import org.noear.solon.XApp;
import org.noear.solon.core.XPlugin;
import org.noear.solonjt.engine.EngineFactory;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        EngineFactory.register(GroovyJtEngine.singleton(),false);
    }
}
