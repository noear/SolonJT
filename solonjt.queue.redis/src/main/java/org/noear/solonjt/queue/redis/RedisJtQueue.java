package org.noear.solonjt.queue.redis;

import org.noear.solonjt.dso.*;
import org.noear.solonjt.utils.PropUtils;

import java.util.Properties;

public class RedisJtQueue implements IJtQueue {
    private RedisX _redisX;

    public RedisJtQueue(Properties prop){
        _redisX = new RedisX(prop);
    }

    public static void init(String cfg) throws Exception {
        if (cfg == null) {
            return;
        }

        Properties prop = null;
        if (cfg.startsWith("@")) {
            prop = CfgUtil.cfgGetProp(cfg.substring(1));
        } else {
            prop = PropUtils.getProp(cfg);
        }

        Properties prop2 = prop;

        JtConstants.queueFactorySet(() -> new RedisJtQueue(prop2));
    }

    @Override
    public void add(Object item) {

    }

    @Override
    public Object peek() {
        return null;
    }

    @Override
    public Object poll() {
        return null;
    }

    @Override
    public void remove() {

    }
}
