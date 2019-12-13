package org.noear.solonjt.task;

public interface IJtTask {
    String getName();
    int getInterval();//秒为单位

    void exec() throws Exception;
}
