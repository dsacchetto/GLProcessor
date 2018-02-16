package com.spireon.core.kafka;

public interface UpdateListener {

    public boolean update(String topic,String message);
}
