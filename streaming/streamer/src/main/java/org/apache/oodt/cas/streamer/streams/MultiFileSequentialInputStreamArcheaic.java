package org.apache.oodt.cas.streamer.streams;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This stream reads multiple files in "lexographical" order and collapses
 * them into one notional stream.  New files are added.  Old files removed.
 * Files modified are changed, and the old stream is invalidated.
 *
 * @author starchmd
 */
public class MultiFileSequentialInputStreamArcheaic extends InputStream {

    final ConcurrentLinkedDeque<File> files = new ConcurrentLinkedDeque<File>();
    InputStream current = null;
    Thread watcher;
    Logger LOG = Logger.getLogger(MultiFileSequentialInputStreamArcheaic.class.getName());
    /**
     * Constructs this class to work on a directory.
     * @param dir - directory to read through emitting data.
     * @throws IOException on failure to setup directory watcher.
     */
    public MultiFileSequentialInputStreamArcheaic(String dir) throws IOException {
        LOG.log(Level.INFO,"Starting multi-item stream on directory: "+dir);
        watcher = new Thread(new DirectoryWatcher(new File(dir)));
        watcher.start();
    }
    //TODO: This could be made more efficient if we didn't completely depend on "read one byte".
    @Override
    public int read() throws IOException {
        while (true)
        {
            int i = -1;
            while ((current == null) || (i = current.read()) == -1)
            {
                if (current != null)
                    current.close();
                if (files.isEmpty()) {
                    current = null;
                    return -1;
                }
                File newf = files.pop();
                current = new FileInputStream(newf);
            }
            return i;
        }
    }
    /**
     * Iterates through the list and returns the next InputStream with available data.
     * Will wait for data.
     * @return - input stream
     * @throws IOException thrown on exception in constituate input streams
     */
    /*private InputStream getCurrentInputStream() throws IOException {
        synchronized (files) {
            Iterator<InputStream> it = files.values().iterator();
            while(it.hasNext()) {
                InputStream is = null;
                if ((is = it.next()).available() > 0)
                    return is;
            }
        }
        return null;
    }*/

    /**
     * Watches directory and adds files to map in parent class.
     * @author starchmd
     */
    private class DirectoryWatcher implements Runnable {
        File dir;
        List<File> list = new LinkedList<File>();
        Logger LOG = Logger.getLogger(DirectoryWatcher.class.getName());
        /**
         * Watch the given path object for changes.
         * @param dir - path representing directory to watch.
         * @throws IOException thrown on problem.
         */
        public DirectoryWatcher(File dir) throws IOException {
            LOG.log(Level.INFO,"Starting directory watcher on: "+dir);
            this.dir = dir;
            update();
        }
        /**
         * Note: Is not robust against files disappearing.
         */
        @Override
        public void run() {
            //Loop until told to stop or exception kills you
            while(true) {
                update();
                try {
                    Thread.sleep(30);
                } catch (InterruptedException ie)
                {
                    //Set InterruptedStatus again
                    Thread.currentThread().interrupt();
                }
            }
        }
        /**
         * List our directory and update.
         */
        private void update() {
            File[] listing = this.dir.listFiles();
            LOG.log(Level.INFO,"Updating directory listing on: "+this.dir);
            for (File file : listing)
            {
                LOG.log(Level.FINE,"Investigating file: "+file);
                //New files
                if (!list.contains(file))
                {
                    LOG.log(Level.INFO,"Found new file: "+file);
                    list.add(file);
                    files.addLast(file);
                }
            }
        }
    }
}
