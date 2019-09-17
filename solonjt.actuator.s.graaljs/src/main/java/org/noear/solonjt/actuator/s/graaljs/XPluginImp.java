package org.noear.solonjt.actuator.s.graaljs;

import org.noear.solon.XApp;
import org.noear.solon.core.XPlugin;
import org.noear.solonjt.actuator.ActuatorFactory;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        ActuatorFactory.register(GraaljsJtActuator.singleton());
    }
}
