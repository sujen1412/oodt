package org.apache.oodt.cas.streamer.reader;

import java.io.IOException;

/**
 * TODO: This is just wrong.  Please fix.
 * This interface exposes the necessary functions required to read a stream.
 *
 * @author starchmd
 */
public interface Reader {
    /**
     * Open a reader.
     */
    public void open();
    /**
     * Read from the stream.
     * @return - bytes read.
     * @throws IOException
     */
    public byte[] read() throws IOException,StreamEmptyException;
    /**
     * Close stream.
     */
    public void close();
}
