package org.noear.solonjt.executor;

import org.noear.solonjt.model.AFileModel;

/**
 * 执行器适配器
 * */
public interface IExecutorFactoryAdapter {
    //记录异常
    void errorLog(AFileModel file,String msg, Throwable err);
    //获取文件
    AFileModel fileGet(String path) throws Exception;
    //默认执行器代号
    String defaultActuator();
}
