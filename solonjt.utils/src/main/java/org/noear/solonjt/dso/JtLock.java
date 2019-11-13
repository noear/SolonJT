package org.noear.solonjt.dso;

import org.noear.solon.annotation.XNote;

/** 引擎锁管理器 */
public final class JtLock implements IJtLock {
    public static JtLock g = new JtLock();

    @XNote("尝试获取锁")
    @Override
    public boolean tryLock(String group, String key, int inSeconds, String inMaster) {
        return JtConstants.lock().tryLock(group, key, inSeconds, inMaster);
    }

    @XNote("尝试获取锁")
    @Override
    public boolean tryLock(String group, String key, int inSeconds) {
        return JtConstants.lock().tryLock(group, key, inSeconds);
    }

    @XNote("尝试获取锁，锁3秒")
    @Override
    public boolean tryLock(String group, String key) {
        return JtConstants.lock().tryLock(group, key);
    }

    @XNote("是否已锁")
    @Override
    public boolean isLocked(String group, String key) {
        return JtConstants.lock().isLocked(group, key);
    }

    @XNote("取消锁")
    @Override
    public void unLock(String group, String key) {
        JtConstants.lock().unLock(group, key);
    }
}