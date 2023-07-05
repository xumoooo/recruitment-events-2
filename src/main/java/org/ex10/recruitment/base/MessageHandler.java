package org.ex10.recruitment.base;

import org.ex10.recruitment.base.Message;

public abstract class MessageHandler<T extends Record> {
    void resetTo(long offset) {
    }

    void start() {
    }

    public abstract void handleMessage(Message<T> message);
}
