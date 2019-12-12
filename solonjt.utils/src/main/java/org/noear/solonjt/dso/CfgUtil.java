package org.noear.solonjt.dso;

import org.noear.snack.core.exts.ThData;
import org.noear.solon.core.Aop;
import org.noear.solonjt.model.AConfigModel;
import org.noear.solonjt.utils.PropUtils;
import org.noear.solonjt.utils.TextUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class CfgUtil {
    static ThData<Map<String,Object>> th_map= new ThData<>(()->new HashMap<>());

    //获取配置值
    public static String cfgGetValue(String name) throws Exception {
        AConfigModel tmp = JtBridgeEx.configAdapter().cfgGet(name);
        if (tmp == null) {
            return null;
        } else {
            return tmp.value;
        }
    }

    public static Properties cfgGetProp(String name) throws Exception{
        String tmp = cfgGetValue(name);

        if(TextUtils.isEmpty(tmp)){
            return null;
        }

        return PropUtils.getProp(tmp);
    }
}
