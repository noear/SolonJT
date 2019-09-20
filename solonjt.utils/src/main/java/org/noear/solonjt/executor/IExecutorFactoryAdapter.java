package org.noear.solonjt.executor;

import org.noear.solonjt.model.AFileModel;

/**
 * 执行器适配器
 * */
public interface IExecutorFactoryAdapter {

    void errorLog(AFileModel file,String msg, Throwable err);

    AFileModel fileGet(String path) throws Exception;

    String defaultActuator();
}
