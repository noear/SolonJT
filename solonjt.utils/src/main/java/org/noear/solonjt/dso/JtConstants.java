package org.noear.solonjt.dso;

public class JtConstants {
    private static IJtAdapter _adapter;
    private static IJtLock _lock = new DefaultJtLock();

    public static void lockSet(IJtLock lock){
        _lock = lock;
    }

    public static void adapterSet(IJtAdapter adapter) {
        _adapter = adapter;
    }

    public static IJtLock lock(){
        return _lock;
    }

    public static IJtAdapter adapter() {
        return _adapter;
    }
}
