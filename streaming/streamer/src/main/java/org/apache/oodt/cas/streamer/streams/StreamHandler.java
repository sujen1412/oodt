package org.apache.oodt.cas.streamer.streams;

//JDK imports
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
//OODT imports
import org.apache.oodt.cas.streamer.publisher.Publisher;
import org.apache.oodt.cas.streamer.reader.InputStreamReader;
import org.apache.oodt.cas.streamer.reader.StreamEmptyException;

/**
 * A thread subclass that reads streaming data and pushes it into Kafka.
 * 
 * @author starchmd
 */
public class StreamHandler extends Thread {

    InputStreamReader reader;
    Publisher publisher;
    int rest = 30;
    int MAX_EXCEPTIONS = 100;

    private static final Logger LOG = Logger.getLogger(StreamHandler.class.getName());
    /**
     * ctor
     * @param reader - the reader of the streaming data
     * @param publisher - publisher to the log manager (usually Kafka)
     */
    public StreamHandler(InputStreamReader reader,Publisher publisher) {
        this.reader = reader;
        this.publisher = publisher;
    }
    /**
     * ctor
     * @param reader - the reader of the streaming data
     * @param publisher - publisher to the log manager (usually Kafka)
     * @param rest - time to rest (milliseconds)
     */
    public StreamHandler(InputStreamReader reader,Publisher publisher,int rest) {
        this(reader,publisher);
        this.rest = rest;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Thread#run()
     */
    public void run() {
        boolean running = true;
        int counter = 0;
        //Keep reading bytes and stuffing them into Kafka
        try {
            while(running) {
                try {
                    byte[] bytes = reader.read();
                    publisher.publish(bytes);
                } catch(StreamEmptyException e) {
                    try {
                        LOG.log(Level.INFO,"Stream empty. Waiting "+this.rest+" milliseconds.");
                        Thread.sleep(this.rest);
                    } catch (InterruptedException ie) {
                        //Set InterruptedStatus again
                        LOG.log(Level.WARNING,"Sleep interrupted. Ignoring interuption and waking up.");
                        Thread.currentThread().interrupt();
                    }
                    counter = 0;
                } catch (IOException e) {
                    counter++;
                    if (counter > MAX_EXCEPTIONS)
                        throw e;
                    LOG.log(Level.WARNING,"Input/Output exception detected("+counter+"): "+e.getMessage());
                }
            }
        } catch (IOException deathBlow) {
            LOG.log(Level.SEVERE,"Too many exceptions ("+counter+") encountered. Exiting. "+deathBlow.getMessage());
        } finally {
            reader.close();
            publisher.close();
        }
    }
}
