package org.noear.solonjt.actuator.m.enjoy;

import org.noear.solon.XApp;
import org.noear.solon.core.XPlugin;
import org.noear.solonjt.actuator.ActuatorFactory;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        ActuatorFactory.register(EnjoyJtActuator.singleton());
    }
}
