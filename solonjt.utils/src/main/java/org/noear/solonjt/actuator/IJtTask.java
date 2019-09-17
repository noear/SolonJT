package org.noear.solonjt.actuator;

public interface IJtTask {
    String getName();
    int getInterval();//秒为单位

    void exec() throws Exception;
}
