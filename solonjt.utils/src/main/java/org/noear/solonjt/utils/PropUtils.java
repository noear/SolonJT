package org.noear.solonjt.utils;

import java.io.StringReader;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class PropUtils {
    private static Map<String,Properties> _map = new ConcurrentHashMap<>();

    public static Properties getProp(String text) {
        if (TextUtils.isEmpty(text)) {
            return null;
        }

        Properties tmp = _map.get(text);

        if (tmp == null) {
            Properties _prop = new Properties();
            RunUtil.runActEx(() -> _prop.load(new StringReader(text)));
            tmp = _prop;

            _map.put(text, tmp);
        }

        return tmp;
    }
}
