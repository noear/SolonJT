package org.noear.solonjt.dso;

import org.noear.solonjt.dso.IJtQueue;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DefaultJtQueue implements IJtQueue {
    Queue<String> _queue = new ConcurrentLinkedQueue<>();
    String _name;


    public DefaultJtQueue(String name){
        _name = name;
    }

    @Override
    public String name() {
        return _name;
    }

    @Override
    public void add(String item) {
        if (item != null) {
            _queue.add(item);
        }
    }

    @Override
    public void addAll(Iterable<String> items) {
        for (String item : items) {
            add(item);
        }
    }

    @Override
    public String peek() {
        return _queue.peek();
    }

    @Override
    public String poll() {
        return _queue.poll();
    }

    @Override
    public void remove() {
        _queue.remove();
    }
}
