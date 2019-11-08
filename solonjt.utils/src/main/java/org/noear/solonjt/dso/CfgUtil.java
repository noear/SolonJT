package org.noear.solonjt.dso;

import org.noear.snack.core.exts.ThData;
import org.noear.solon.core.Aop;

import java.util.HashMap;
import java.util.Map;

public class CfgUtil {
    static ThData<Map<String,Object>> th_map= new ThData<>(()->new HashMap<>());

    //获取配置（一行记录）
    public static Map<String,Object> cfgGetMap(String name) throws Exception {
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

    //获取配置值
    public static String cfgGet(String name) throws Exception{
        Map<String, Object> tmp = cfgGetMap(name);
        if(tmp==null){
            return null;
        }else{
            return (String) tmp.get("value");
        }
    }

    private static String _nodeId;
    public static String nodeId(){
        if(_nodeId == null) {
            _nodeId = Aop.prop().get("solonjt.node", "");
        }

        return _nodeId;
    }
}
