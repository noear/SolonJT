package org.noear.solonjt.dso;

import org.noear.solon.core.Aop;
import org.noear.solonjt.executor.IJtExecutorAdapter;
import org.noear.solonjt.model.AConfigModel;
import org.noear.solonjt.model.AFileModel;

import java.util.Map;

/**
 * 执行工厂适配器
 * */
public class JtAdapter implements IJtExecutorAdapter,IJtConfigAdapter {

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

    private static String _nodeId;
    @Override
    public String nodeId(){
        if(_nodeId == null) {
            _nodeId = Aop.prop().get("solonjt.node", "");
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
    public AConfigModel cfgGet(String name) throws Exception {
        return DbApi.cfgGetMod(name);
    }
}
