package org.noear.solonjt.dso;

import java.util.Map;

public interface XFunHandler {
    Object call(Map<String,Object> arg) throws Exception;
}
