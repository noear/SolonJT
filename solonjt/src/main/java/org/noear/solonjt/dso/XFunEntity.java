package org.noear.solonjt.dso;

import java.util.Map;

public class XFunEntity implements XFunHandler {
    public XFunHandler handler;
    public String note;
    public int priority;
    public long lastModified;

    public XFunEntity(XFunHandler handler, int priority , String note) {
        this.handler = handler;
        this.priority = priority;
        this.note = note;
        this.lastModified = System.currentTimeMillis();
    }

    @Override
    public Object call(Map<String, Object> arg) throws Exception {
        return handler.call(arg);
    }
}
