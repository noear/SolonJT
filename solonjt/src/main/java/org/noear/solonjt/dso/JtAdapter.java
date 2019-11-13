package org.noear.solonjt.dso;

import org.noear.solonjt.model.AConfigModel;
import org.noear.solonjt.model.AFileModel;

/**
 * 执行工厂适配器
 * */
public class JtAdapter implements IJtAdapter {

    private String _defaultExecutor = "freemarker";

    public JtAdapter() {
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
    public AConfigModel cfgGet(String name) throws Exception {
        return DbApi.cfgGetMod(name);
    }

    @Override
    public String defaultExecutor() {
        return _defaultExecutor;
    }

    public void defaultExecutorSet(String defaultExecutor) {
        _defaultExecutor = defaultExecutor;
    }
}
