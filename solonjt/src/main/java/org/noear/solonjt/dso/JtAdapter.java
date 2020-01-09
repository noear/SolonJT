package org.noear.solonjt.dso;

import org.noear.solon.XApp;
import org.noear.solon.core.Aop;
import org.noear.solonjt.executor.IJtConfigAdapter;
import org.noear.solonjt.executor.IJtExecutorAdapter;
import org.noear.solonjt.model.AFileModel;

import java.util.Map;

/**
 * 执行工厂适配器
 * */
public class JtAdapter implements IJtExecutorAdapter, IJtConfigAdapter {
    public static JtAdapter global = new JtAdapter();

    private String _defaultExecutor = "freemarker";

    public JtAdapter() {
    }

    @Override
    public void log(Map<String, Object> data) {
        DbApi.log(data);
    }

    @Override
    public void logError(AFileModel file, String msg, Throwable err) {
        LogUtil.log("_file", file.tag, file.path, 0, "", msg);
    }

    @Override
    public AFileModel fileGet(String path) throws Exception {
        return AFileUtil.get(path);
    }

    public AFileModel fileGetByLocal(String path) throws Exception{
        return null;
    }

    private static String _nodeId;

    @Override
    public String nodeId() {
        if (_nodeId == null) {
            _nodeId = XApp.cfg().get("solonjt.node", "");
        }

        return _nodeId;
    }

    @Override
    public String defaultExecutor() {
        return _defaultExecutor;
    }

    public void defaultExecutorSet(String defaultExecutor) {
        _defaultExecutor = defaultExecutor;
    }


    @Override
    public String cfgGet(String name, String def) throws Exception {
        return DbApi.cfgGet(name, def);
    }

    @Override
    public boolean cfgSet(String name, String value) throws Exception {
        return DbApi.cfgSet(name, value, null);
    }
}
