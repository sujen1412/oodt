package org.apache.oodt.cas.streamer.reader;

public class StreamEmptyException extends Exception {
    public StreamEmptyException()
    {
        super("Stream currently has no data.");
    }
}
