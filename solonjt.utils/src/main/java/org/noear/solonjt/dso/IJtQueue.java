package org.noear.solonjt.dso;

public interface IJtQueue {
    /**
     * 推入到尾部
     */
    void add(Object item);

    /*
     * 预览头部元素
     * */
    Object peek();

    /**
     * 拉取头部元素（同时移除）
     */
    Object poll();

    /**
     * 移除头部元素
     * */
    void remove();
}
