package org.noear.solonjt.dso;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DefaultJtQueue implements IJtQueue {
    Queue _queue = new ConcurrentLinkedQueue();

    @Override
    public void add(Object item) {
        if (item != null) {
            _queue.add(item);
        }
    }

    @Override
    public Object peek() {
        return _queue.peek();
    }

    @Override
    public Object poll() {
        return _queue.poll();
    }

    @Override
    public void remove() {
        _queue.remove();
    }
}
