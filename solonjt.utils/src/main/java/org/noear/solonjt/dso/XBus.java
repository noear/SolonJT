package org.noear.solonjt.dso;

import org.noear.snack.core.exts.ThData;
import org.noear.solon.annotation.XNote;

import java.util.HashMap;
import java.util.Map;

@XNote("消息总线接口")
public class XBus {
    public static final XBus g  = new XBus();

    static ThData<Map<String,Object>> th_map= new ThData<>(new HashMap<>());

    @XNote("发布消息")
    public boolean publish(String topic, String content) throws Exception{
        Map<String,Object> data = th_map.get();

        data.clear();
        data.put("topic",topic);
        data.put("content",content);

        return XFun.g.call("xbus_publish", data) != null;
    }

    @XNote("发布消息")
    public boolean publish(Map<String,Object> data) throws Exception{
        return XFun.g.call("xbus_publish", data) != null;
    }
}
