package org.apache.oodt.cas.streamer.reader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * @author starchmd
 *
 */
public class InputStreamReader implements Reader {
    InputStream input = null;
    public static final int BLOCK_SIZE = 4096;
    public void setInputStream(InputStream is) {
        this.input = is;
    }
    @Override
    public void open() {}
    @Override
    public byte[] read() throws IOException, StreamEmptyException {
        byte[] bytes = new byte[BLOCK_SIZE];
        int i = input.read(bytes);
        if (i == -1)
            throw new StreamEmptyException();
        return Arrays.copyOf(bytes, i);
    }
    @Override
    public void close() {
        try {
        input.close();
        }catch(IOException e){}
    }
}