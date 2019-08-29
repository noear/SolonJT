package org.noear.solonjt.dso;

import java.util.Date;

/**
 * 任务运行工具
 * */
public class TaskUtil {
    public static void run(ITask task) {
        System.out.print("run::" + task.getName() + "\r\n");

        new Thread(() -> {
            doRun(task);
        }).start();
    }

    private static void doRun(ITask task){
        while (true) {
            try {
                Date time_start = new Date();
                System.out.print(task + "::time_start::" + time_start.toString() + "\r\n");

                task.exec();

                Date time_end = new Date();
                System.out.print(task + "::time_end::" + time_end.toString() + "\r\n");

                if(task.getInterval() == 0){
                    return;
                }

                if (time_end.getTime() - time_start.getTime() < task.getInterval()) {
                    Thread.sleep(task.getInterval());//0.5s
                }

            } catch (Exception ex) {
                ex.printStackTrace();

                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }
        }
    }


    public interface ITask {
        String getName();
        int getInterval();//秒为单位

        void exec() throws Exception;
    }
}
