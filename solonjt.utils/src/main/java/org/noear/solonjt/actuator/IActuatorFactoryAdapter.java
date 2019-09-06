package org.noear.solonjt.actuator;

import org.noear.solonjt.model.AFileModel;

/**
 * 执行器适配器
 * */
public interface IActuatorFactoryAdapter {

    void errorLog(AFileModel file,String msg, Exception err);

    AFileModel fileGet(String path) throws Exception;

    String defaultActuator();
}
