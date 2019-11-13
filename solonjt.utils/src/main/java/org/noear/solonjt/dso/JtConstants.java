package org.noear.solonjt.dso;

public class JtConstants {
    private static IJtAdapter _adapter;

    public static void init(IJtAdapter adapter) {
        _adapter = adapter;
    }

    public static IJtAdapter adapter() {
        return _adapter;
    }
}
