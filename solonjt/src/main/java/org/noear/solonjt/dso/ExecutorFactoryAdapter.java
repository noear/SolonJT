package org.noear.solonjt.dso;

import org.noear.solonjt.executor.IExecutorFactoryAdapter;
import org.noear.solonjt.model.AFileModel;

/**
 * 执行工厂适配器
 * */
public class ExecutorFactoryAdapter implements IExecutorFactoryAdapter {

    private String _defaultExecutor = "freemarker";

    public ExecutorFactoryAdapter() {
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
    public String defaultExecutor() {
        return _defaultExecutor;
    }

    public void defaultExecutorSet(String defaultExecutor) {
        _defaultExecutor = defaultExecutor;
    }
}
