package com.demo.photopicker.model;

import android.os.Bundle;

import java.io.Serializable;

/**
 * Created by bjhl on 2018/5/31.
 */

public class PhotoEvent {
    public int eventType;
    private Bundle bundle;

    public PhotoEvent(int eventType) {
        this.eventType = eventType;
    }

    public int getEventType() {
        return eventType;
    }

    public void setData(Bundle bundle) {
        getBundle().putAll(bundle);
    }

    public Bundle getData() {
        return bundle;
    }

    public void writeInt(String key, int value) {
        getBundle().putInt(key, value);
    }

    public int readInt(String key) {
        if (bundle != null) {
            return bundle.getInt(key);
        }
        return 0;
    }

    public void writeLong(String key, long value) {
        getBundle().putLong(key, value);
    }

    public long readLong(String key) {
        if (bundle != null) {
            return bundle.getLong(key);
        }
        return 0l;
    }

    public void writeFloat(String key, float value) {
        getBundle().putFloat(key, value);
    }

    public float readFloat(String key) {
        if (bundle != null) {
            return bundle.getFloat(key);
        }
        return 0f;
    }

    public void writeDouble(String key, double value) {
        getBundle().putDouble(key, value);
    }

    public double readDouble(String key) {
        if (bundle != null) {
            return bundle.getDouble(key);
        }
        return 0d;
    }

    public void writeString(String key, String value) {
        getBundle().putString(key, value);
    }

    public String readString(String key) {
        if (bundle != null) {
            return bundle.getString(key);
        }
        return null;
    }

    public void writeSerializable(String key, Serializable value) {
        getBundle().putSerializable(key, value);
    }

    public Serializable readSerializable(String key) {
        if (bundle != null) {
            return bundle.getSerializable(key);
        }
        return null;
    }

    private synchronized Bundle getBundle() {
        if (bundle == null) {
            bundle = new Bundle();
        }
        return bundle;
    }
}
