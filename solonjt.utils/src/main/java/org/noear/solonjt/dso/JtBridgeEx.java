package org.noear.solonjt.dso;

import org.noear.solonjt.model.AConfigModel;
import org.noear.solonjt.model.AFileModel;

public class JtBridgeEx extends JtBridge {
    private static IJtConfigAdapter _configAdapter;

    /**
     * 设置执行适配器
     */
    public static void configAdapterSet(IJtConfigAdapter configAdapter) {
        _configAdapter = configAdapter;
    }

    /**
     * 获取执行适配器
     */
    public static IJtConfigAdapter configAdapter() {
        return _configAdapter;
    }

    public static AConfigModel cfgGet(String name) throws Exception{
        return configAdapter().cfgGet(name);
    }

    public static String cfgGetValue(String name) throws Exception{
        AConfigModel tmp = cfgGet(name);
        if(tmp == null){
            return null;
        }else{
            return tmp.value;
        }
    }
}
