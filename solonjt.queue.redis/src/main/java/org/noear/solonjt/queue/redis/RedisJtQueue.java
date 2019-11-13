package org.noear.solonjt.queue.redis;

import org.noear.solonjt.dso.*;
import org.noear.solonjt.utils.PropUtils;

import java.util.Properties;

public class RedisJtQueue implements IJtQueue {
    private RedisX _redisX;
    private String _name;

    public RedisJtQueue(String name, Properties prop) {
        _name = name;
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

        JtConstants.queueFactorySet((name) -> new RedisJtQueue(name, prop2));
    }


    @Override
    public String name() {
        return _name;
    }

    @Override
    public void add(String item) {
        if (item != null) {
            _redisX.open0((rs) -> {
                rs.key(name()).listAdd(item);
            });
        }
    }

    @Override
    public void addAll(Iterable<String> items) {
        _redisX.open0((rs) -> {
            for (String item : items) {
                if (item != null) {
                    rs.key(name()).listAdd(item);
                }
            }
        });
    }

    @Override
    public String peek() {
        return _redisX.open1( (rs) -> rs.key(name()).listGet(-1));
    }

    @Override
    public String poll() {
        return _redisX.open1((rs) -> rs.key(name()).listPop());
    }

    @Override
    public void remove() {
        _redisX.open0((rs) -> rs.key(name()).listPop());
    }
}
