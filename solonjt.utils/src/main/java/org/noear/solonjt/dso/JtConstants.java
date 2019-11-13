package org.noear.solonjt.dso;

import org.noear.solon.ext.Fun1;

import java.util.HashMap;
import java.util.Map;

public class JtConstants {
    private static IJtAdapter _adapter;
    private static IJtLock _lock = new DefaultJtLock();
    private static Fun1<String, IJtQueue> _queueFactory = (name) -> new DefaultJtQueue(name);
    private static Map<String, IJtQueue> _queueMap = new HashMap<>();

    /**
     * 设置队列工厂
     */
    public static void queueFactorySet(Fun1<String, IJtQueue> queueFactory) {
        _queueFactory = queueFactory;
    }

    /**
     * 设置锁服务
     */
    public static void lockSet(IJtLock lock) {
        _lock = lock;
    }

    /**
     * 设置数据适配器
     */
    public static void adapterSet(IJtAdapter adapter) {
        _adapter = adapter;
    }

    /**
     * 锁服务
     */
    public static IJtLock lock() {
        return _lock;
    }

    /**
     * 适配器
     */
    public static IJtAdapter adapter() {
        return _adapter;
    }

    /*
     * 新队列
     * */
    public static IJtQueue queue(String name) {
        IJtQueue tmp = _queueMap.get(name);

        if (tmp == null) {
            synchronized (_queueMap) {
                tmp = _queueMap.get(name);
                if (tmp == null) {
                    tmp = _queueFactory.run(name);
                    _queueMap.put(name, tmp);
                }
            }
        }

        return tmp;
    }
}
