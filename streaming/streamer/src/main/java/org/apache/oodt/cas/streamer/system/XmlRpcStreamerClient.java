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
import org.apache.xmlrpc.CommonsXmlRpcTransport;
import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcClientException;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcTransport;
import org.apache.xmlrpc.XmlRpcTransportFactory;
//JDK imports
import java.net.URL;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;
//OODT imports
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodRetryHandler;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.oodt.cas.cli.CmdLineUtility;
import org.apache.oodt.cas.filemgr.structs.exceptions.ConnectionException;

/**
 * @author starchmd
 * @version $Revision$
 * 
 * <p>
 * The XML RPC based streamer client.
 * </p>
 * 
 */
public class XmlRpcStreamerClient {

    /* our xml rpc client */
    private XmlRpcClient client = null;

    /* our log stream */
    private static Logger LOG = Logger.getLogger(XmlRpcStreamerClient.class.getName());

    /* streamer url */
    private URL url = null;

    public XmlRpcStreamerClient(final URL url) throws ConnectionException {
       this(url, true);
    }

    /**
     * <p>
     * Constructs a new XmlRpcFileManagerClient with the given <code>url</code>.
     * </p>
     * 
     * @param url
     *            The url pointer to the xml rpc file manager service.
     * @param testConnection
     *            Whether or not to check if server at given url is alive.
     */
    public XmlRpcStreamerClient(final URL url, boolean testConnection)
          throws ConnectionException {
        // set up the configuration, if there is any
        if (System.getProperty("org.apache.oodt.cas.streamer.properties") != null) {
            String configFile = System.getProperty("org.apache.oodt.cas.streamer.properties");
            LOG.log(Level.INFO,"Loading Streamer  Configuration Properties from: ["+ configFile + "]");
            try {
                System.getProperties().load(new FileInputStream(new File(configFile)));
            } catch (Exception e) {
                LOG.log(Level.INFO,"Error loading configuration properties from: ["+ configFile + "]");
            }
        }

        XmlRpcTransportFactory transportFactory = new XmlRpcTransportFactory()
        {
            public XmlRpcTransport createTransport() throws XmlRpcClientException {
                HttpClient client = new HttpClient();
                client.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,new HttpMethodRetryHandler()
                        {
                            public boolean retryMethod(HttpMethod method, IOException e, int count) {
                                if (count < Integer.getInteger("org.apache.oodt.cas.streamer.system.xmlrpc.connection.retries",3).intValue()) {
                                    try {
                                        Thread.sleep(Integer.getInteger("org.apache.oodt.cas.streamer.system.xmlrpc.connection.retry.interval.seconds",0).intValue() * 1000);
                                        return true;
                                    } catch (Exception e1) {
                                    }
                                }
                                return false;
                            }
                        });
                CommonsXmlRpcTransport transport = new CommonsXmlRpcTransport(url, client);
                transport.setConnectionTimeout(Integer.getInteger("org.apache.oodt.cas.streamer.system.xmlrpc.connectionTimeout.minutes",20).intValue() * 60 * 1000);
                transport.setTimeout(Integer.getInteger("org.apache.oodt.cas.streamer.system.xmlrpc.requestTimeout.minutes",60).intValue() * 60 * 1000);
                return transport;
            }

            public void setProperty(String arg0, Object arg1) {
            }

        };

        client = new XmlRpcClient(url, transportFactory);
        this.url = url;
    }
    
    public boolean stream(String handle) {
        boolean success = false;
        Vector<Object> argList = new Vector<Object>();
        argList.add(handle);
        try {
            return success = ((Boolean) client.execute("streamer.stream",argList)).booleanValue();
        } catch (XmlRpcException e) {
            LOG.log(Level.WARNING, "XmlRpcException when connecting to streamer: [" + this.url + "]");
            success = false;
        } catch (IOException e) {
            LOG.log(Level.WARNING, "IOException when connecting to streamer: [" + this.url + "]");
            success = false;
        }
        return success;
    }


    public static void main(String[] args) {
       CmdLineUtility cmdLineUtility = new CmdLineUtility();
       cmdLineUtility.run(args);
    }
}
