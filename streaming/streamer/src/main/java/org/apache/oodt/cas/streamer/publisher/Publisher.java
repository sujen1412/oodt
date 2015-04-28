package org.apache.oodt.cas.streamer.publisher;

/**
 * @author starchmd
 *
 * Publisher, used to publish data to some streaming system.
 */
public interface Publisher {

    /**
     * Open a stream based on the given handle.
     * @param handle - name of string
     */
    public void open(String handle);
    /**
     * Stream the given bytes.
     * @param bytes - bytes to strean
     * @return successful?
     */
    public boolean publish(byte[] bytes);
    /**
     * Close the stream
     */
    public void close();
}
