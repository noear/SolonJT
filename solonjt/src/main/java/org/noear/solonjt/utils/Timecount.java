package org.noear.solonjt.utils;

public class Timecount {
    private long start_time;

    public Timecount start() {
        start_time = System.currentTimeMillis();
        return this;
    }

    public Timespan stop() {
        return new Timespan(System.currentTimeMillis(), start_time);
    }

    //@ref_second:参考少数
    public String stop(long ref_second) {
        double temp = (stop().milliseconds() / 10) / 100.00d;

        if (temp > ref_second) {
            return temp + "s******慢!!!";
        } else {
            return temp + "s";
        }
    }
}
