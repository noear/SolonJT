package org.noear.solonjt.dso;

import org.noear.solonjt.executor.IExecutorFactoryAdapter;
import org.noear.solonjt.model.AFileModel;

public class ActuatorFactoryAdapter implements IExecutorFactoryAdapter {

    private String _defaultActuator;
    public ActuatorFactoryAdapter(String defaultActuator){
        _defaultActuator = defaultActuator;
    }

    @Override
    public void errorLog(AFileModel file, String msg, Throwable err) {
        LogUtil.log("_file", file.tag, file.path, 0, "", msg);
    }

    @Override
    public AFileModel fileGet(String path) throws Exception {
        return AFileUtil.get(path);
    }

    @Override
    public String defaultActuator() {
        return _defaultActuator;
    }
}
