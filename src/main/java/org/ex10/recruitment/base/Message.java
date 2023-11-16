package org.ex10.recruitment.base;

public record Message<T extends Record>(T event, long offset) {

    /*
    updates the underlying stream tip to the offset this message
    so that messages before this will not be replayed to this consumer on reconnection
     */
    public void acknowledge() {
    }

}
