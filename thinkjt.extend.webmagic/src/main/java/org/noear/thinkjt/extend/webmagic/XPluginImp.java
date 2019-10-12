package org.noear.thinkjt.extend.webmagic;

import org.noear.solon.XApp;
import org.noear.solon.core.XPlugin;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        app.sharedAdd("eWebmagic",new eWebmagic());
    }
}
