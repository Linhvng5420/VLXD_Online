package com.tdc.vlxdonline.Model;

public class ModelTTLock {
    private String locktime, locktype;
    private boolean lock, online;

    public ModelTTLock() {
        locktime = "";
        locktype = "chuaduyet";
        lock = false;
        online = false;
    }

    public String getLocktime() {
        return locktime;
    }

    public void setLocktime(String locktime) {
        this.locktime = locktime;
    }

    public String getLocktype() {
        return locktype;
    }

    public void setLocktype(String locktype) {
        this.locktype = locktype;
    }

    public boolean isLock() {
        return lock;
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
}
