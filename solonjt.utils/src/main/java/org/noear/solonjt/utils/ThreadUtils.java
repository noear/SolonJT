package org.noear.solonjt.utils;

import org.noear.snack.core.exts.ThData;

public class ThreadUtils {
    private static final ThData<StringBuilder> _tlBuilder = new ThData(new StringBuilder(1024*5));

    public static StringBuilder getStringBuilder(){
        StringBuilder tmp = _tlBuilder.get();
        tmp.setLength(0);

        return tmp;
    }
}
