package org.noear.solonjt.actuator;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class TaskFactory {
    private static Set<IJtTask> _taskSet = new HashSet<>();
    public static void register(IJtTask task){
        _taskSet.add(task);
    }

    public static Set<IJtTask> tasks(){
        return Collections.unmodifiableSet(_taskSet);
    }
}
