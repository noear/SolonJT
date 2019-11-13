package org.noear.solonjt.dso;

import org.noear.solonjt.utils.ext.Fun0;

public class JtConstants {
    private static IJtAdapter _adapter;
    private static IJtLock _lock = new DefaultJtLock();
    private static Fun0<IJtQueue> _queueFactory = ()-> new DefaultJtQueue();


    /**
     * 设置队列工厂
     * */
    public static void queueFactorySet(Fun0<IJtQueue> queueFactory){
        _queueFactory = queueFactory;
    }

    /**
     * 设置锁服务
     * */
    public static void lockSet(IJtLock lock){
        _lock = lock;
    }

    /**
     * 设置数据适配器
     * */
    public static void adapterSet(IJtAdapter adapter) {
        _adapter = adapter;
    }

    /**
     * 锁服务
     * */
    public static IJtLock lock(){
        return _lock;
    }

    /**
     * 适配器
     * */
    public static IJtAdapter adapter() {
        return _adapter;
    }

    /*
    * 新队列
    * */
    public static IJtQueue queueNew(){
        return _queueFactory.run();
    }
}
