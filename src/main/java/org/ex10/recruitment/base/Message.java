package org.ex10.recruitment.base;

public record Message<T extends Record>(T event, long offset) {

    /*
    updates the underlying stream so that the offset of this message
    is now the tip of the stream (messages before this will not be replayed)
     */
    void acknowledge() {
    }

}
