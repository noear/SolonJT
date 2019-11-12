package org.noear.solonjt.lock.redis;

import org.noear.solonjt.dso.CfgUtil;
import org.noear.solonjt.dso.IJtLock;
import org.noear.solonjt.dso.XLock;
import org.noear.solonjt.utils.PropUtils;

import java.util.Properties;

public class RedisJtLock implements IJtLock {
    private RedisX _redisX;

    public RedisJtLock(Properties prop){
        _redisX = new RedisX(prop);
    }

    public static void init(String cfg) throws Exception{
        Properties prop = null;
        if(cfg.startsWith("@")){
            prop = CfgUtil.cfgGetProp(cfg.substring(1));
        }else{
            prop = PropUtils.getProp(cfg);
        }

        XLock.global = new RedisJtLock(prop);
    }

    @Override
    public boolean tryLock(String group, String key, int inSeconds, String inMaster) {
        String key2 = group + ".lk." + key;

        return _redisX.open1((ru) -> {
            if (ru.key(key2).exists() == false) {
                ru.key(key2).expire(inSeconds).lock(inMaster);
            }

            return inMaster.equals(ru.key(key2).get());
        });
    }

    @Override
    public boolean tryLock(String group, String key, int inSeconds) {
        String key2 = group + ".lk." + key;

        return _redisX.open1((ru) -> {
            if (ru.key(key2).exists() == false) {
                return ru.key(key2).expire(inSeconds).lock("_");
            }else{
                return false;
            }
        });
    }

    @Override
    public boolean tryLock(String group, String key) {
        return tryLock(group, key, 3);
    }

    @Override
    public boolean isLocked(String group, String key) {
        String key2 = group + ".lk." + key;

        return _redisX.open1((ru) -> ru.key(key2).exists());
    }

    @Override
    public void unLock(String group, String key) {
        String key2 = group+".lk." + key;

        _redisX.open0((ru) -> {
            ru.key(key2).delete();
        });
    }
}
