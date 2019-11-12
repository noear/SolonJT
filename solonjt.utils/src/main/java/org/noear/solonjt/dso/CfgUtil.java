package org.noear.solonjt.dso;

import org.noear.snack.core.exts.ThData;
import org.noear.solon.core.Aop;
import org.noear.solonjt.utils.PropUtils;
import org.noear.solonjt.utils.RunUtil;
import org.noear.solonjt.utils.TextUtils;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class CfgUtil {
    static ThData<Map<String,Object>> th_map= new ThData<>(()->new HashMap<>());

    //获取配置（一行记录）
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

    //获取配置值
    public static String cfgGetValue(String name) throws Exception{
        Map<String, Object> tmp = cfgGet(name);
        if(tmp==null){
            return null;
        }else{
            return (String) tmp.get("value");
        }
    }

    public static Properties cfgGetProp(String name) throws Exception{
        String tmp = cfgGetValue(name);

        if(TextUtils.isEmpty(tmp)){
            return null;
        }

        return PropUtils.getProp(tmp);
    }

    private static String _nodeId;
    public static String nodeId(){
        if(_nodeId == null) {
            _nodeId = Aop.prop().get("solonjt.node", "");
        }

        return _nodeId;
    }
}
