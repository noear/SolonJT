package org.noear.solonjt.utils;

import org.noear.weed.ext.Fun0;

public class ThreadData<T> extends ThreadLocal<T> {
    private Fun0<T> _def;
    public ThreadData(Fun0<T> def){
        _def = def;
    }

    @Override
    protected T initialValue() {
        return _def.run();
    }
}

