package org.noear.solonjt.executor;

public interface IJtConfigAdapter {
    String cfgGet(String name, String def) throws Exception;
    boolean cfgSet(String name, String value) throws Exception;
}
