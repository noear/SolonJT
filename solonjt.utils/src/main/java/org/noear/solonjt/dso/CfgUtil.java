package org.noear.solonjt.dso;

import org.noear.snack.core.exts.ThData;

import java.util.HashMap;
import java.util.Map;

public class CfgUtil {
    static ThData<Map<String,Object>> th_map= new ThData<>(new HashMap<>());

    public static Map<String,Object> cfgGet(String name) throws Exception {
        Map<String, Object> map = th_map.get();

        map.clear();
        map.put("name", name);

        Object tmp = XFun.g.call("cfg_get", map);

        if (tmp == null) {
            return null;
        } else {
            return (Map<String, Object>) tmp;
        }
    }
}
