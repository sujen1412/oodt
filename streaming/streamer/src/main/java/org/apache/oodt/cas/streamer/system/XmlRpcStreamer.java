/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.oodt.cas.streamer.system;

//APACHE imports
import org.apache.xmlrpc.WebServer;

//OODT imports
import org.apache.oodt.cas.metadata.Metadata;
import org.apache.oodt.cas.streamer.ingest.IngestHandler;
import org.apache.oodt.cas.streamer.publisher.KafkaPublisher;
import org.apache.oodt.cas.streamer.publisher.Publisher;
import org.apache.oodt.cas.streamer.reader.InputStreamReader;
import org.apache.oodt.cas.streamer.streams.MultiFileSequentialInputStreamArcheaic;
import org.apache.oodt.cas.streamer.streams.StreamHandler;
import org.apache.oodt.cas.filemgr.structs.Product;
import org.apache.oodt.cas.filemgr.structs.Reference;
import org.apache.oodt.cas.filemgr.structs.exceptions.CatalogException;
import org.apache.oodt.cas.filemgr.structs.exceptions.ConnectionException;
import org.apache.oodt.cas.filemgr.system.XmlRpcFileManagerClient;





//JDK imports
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author starchmd
 * @version $Revision$
 * 
 * <p>
 * An XML RPC-based File manager.
 * </p>
 * 
 */
public class XmlRpcStreamer {

    /* the port to run the XML RPC web server on, default is 1999 */
    private int webServerPort = 1999;

    /* our log stream */
    private static final Logger LOG = Logger.getLogger(XmlRpcStreamer.class.getName());
    /* our xml rpc web server */
    private WebServer webServer = null;
    /* All the active streams */
    Map<String,Thread> streams = new HashMap<String,Thread>();

    /* Handles ingest to filemanager */
    private IngestHandler ingest;
  
    /**
     * <p>
     * Creates a new XmlRpcStreamer on the given port.
     * </p>
     * 
     * @param port - web server port to run XML Rpc server defaults to 1999.
     */
    public XmlRpcStreamer(int port) throws Exception {
        webServerPort = port;

        // start up the web server
        webServer = new WebServer(webServerPort);
        webServer.addHandler("streamer", this);
        webServer.start();

        this.loadConfiguration();
        LOG.log(Level.INFO, "Streamer started by " + System.getProperty("user.name", "unknown"));

    }
    /**
     * Stream XML-RPC entrance
     * @param hash - has of values
     * @return did we succeed?
     */
    public boolean stream(Hashtable<String, Object> hash) {
        String handle = (String) hash.get("handle");
        return stream(handle);
    }

    /**
     * Gets an input stream from a handle
     * @param handle
     * @return
     * @throws IOException
     */
    public InputStream getInputStream(String handle) throws IOException {
        return new MultiFileSequentialInputStreamArcheaic(handle);
    }
    /**
     * Ingest a stream and start up a thread to read and stuff it.
     * @param handle - handle to stream
     */
    public boolean stream(String handle)
    {
        try
        {
            //Ingest stream metadata
            this.ingest.ingest(handle);
            //Are we already streaming
            if (streams.get(handle) != null)
                return false;
            //Create a reader and load an input stream
            InputStreamReader streamReader = new InputStreamReader();
            streamReader.setInputStream(getInputStream(handle));
            //Publisher
            Publisher pub = new KafkaPublisher();
            pub.open(handle);
            Thread th = new StreamHandler(streamReader,pub);
            th.run();
            streams.put(handle,th);
            return true;
        } catch (IOException e) {
            LOG.log(Level.WARNING,"Input/Output exception upon streaming: "+e.getMessage());
        } catch (CatalogException e) {
            LOG.log(Level.WARNING,"Catalog exception upon streaming: "+e.getMessage());
        } catch (Exception e) {
            LOG.log(Level.WARNING,"Exception exception upon streaming: "+e.getMessage());
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            LOG.log(Level.FINEST,sw.toString());
        }
        return false;
    }
    /**
     * Load the configuration file.
     * @throws IllegalArgumentException
     * @throws FileNotFoundException
     * @throws ConnectionException
     * @throws IOException
     */
    private void loadConfiguration() throws IllegalArgumentException, FileNotFoundException, ConnectionException, IOException {
        //Load the configuration
        if (System.getProperty("org.apache.oodt.cas.streamer.properties") != null) {
            String configFile = System.getProperty("org.apache.oodt.cas.streamer.properties");
            LOG.log(Level.INFO,"Loading Streamer Configuration Properties from: ["+configFile+"]");
            System.getProperties().load(new FileInputStream(new File(configFile)));
        } else {
            throw new IllegalArgumentException("org.apache.oodt.cas.streamer.properties is required. Use: -Dorg.apache.oodt.cas.streamer.properties='XXX'");
        }
        String fmUrl = System.getProperty("streamer.fm.url","http://localhost:4000");
        this.ingest = new IngestHandler(fmUrl);
        this.ingest.setup();

    }
    /**
     * Main Program
     * @param args - args, looking for --port
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        int port = -1;
        String usage = "Streamer --port <xml rpc port>\n";
        //Look for port
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--port")) {
                port = Integer.parseInt(args[++i]);
            }
        }
        //No port
        if (port == -1) {
            System.err.println(usage);
            System.exit(1);
        }
        
        @SuppressWarnings("unused")
        XmlRpcStreamer manager = new XmlRpcStreamer(port);

        for (;;)
            try {
                Thread.currentThread().join();
            } catch (InterruptedException ignore) {}
    }
}
