package org.noear.solonjt.dso;

import org.noear.solon.ext.Fun1;
import org.noear.solonjt.executor.IJtExecutorAdapter;
import org.noear.solonjt.model.AFileModel;

import java.util.HashMap;
import java.util.Map;

public class JtBridge {
    private static IJtExecutorAdapter _executorAdapter;
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
     * 获取锁服务
     */
    public static IJtLock lock() {
        return _lock;
    }


    /**
     * 设置执行适配器
     */
    public static void executorAdapterSet(IJtExecutorAdapter executorAdapter) {
        _executorAdapter = executorAdapter;
    }

    /**
     * 获取执行适配器
     */
    public static IJtExecutorAdapter executorAdapter() {
        return _executorAdapter;
    }

    //获取文件
    public static AFileModel fileGet(String path) throws Exception{
        return executorAdapter().fileGet(path);
    }

    /**
     * 获取节点I
     * */
    public static String nodeId(){
        return executorAdapter().nodeId();
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
