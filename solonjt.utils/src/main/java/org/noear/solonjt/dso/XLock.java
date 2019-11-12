package org.noear.solonjt.dso;

import org.noear.solon.annotation.XNote;

/** 锁管理器 */
public final class XLock implements IJtLock {
    public static XLock g = new XLock();

    public static IJtLock global = new LocalJtLock();

    @XNote("尝试获取锁")
    @Override
    public boolean tryLock(String group, String key, int inSeconds, String inMaster) {
        return global.tryLock(group, key, inSeconds, inMaster);
    }

    @XNote("尝试获取锁")
    @Override
    public boolean tryLock(String group, String key, int inSeconds) {
        return global.tryLock(group, key, inSeconds);
    }

    @XNote("尝试获取锁，锁3秒")
    @Override
    public boolean tryLock(String group, String key) {
        return global.tryLock(group, key);
    }

    @XNote("是否已锁")
    @Override
    public boolean isLocked(String group, String key) {
        return global.isLocked(group, key);
    }

    @XNote("取消锁")
    @Override
    public void unLock(String group, String key) {
        global.unLock(group, key);
    }
}
