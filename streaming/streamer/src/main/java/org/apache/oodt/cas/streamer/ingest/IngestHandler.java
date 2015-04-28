package org.apache.oodt.cas.streamer.ingest;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.oodt.cas.filemgr.structs.Product;
import org.apache.oodt.cas.filemgr.structs.Reference;
import org.apache.oodt.cas.filemgr.structs.exceptions.ConnectionException;
import org.apache.oodt.cas.filemgr.system.XmlRpcFileManagerClient;
import org.apache.oodt.cas.metadata.Metadata;

public class IngestHandler {
	
    private XmlRpcFileManagerClient client;
    private String url;
    
    /* our log stream */
    private static final Logger LOG = Logger.getLogger(IngestHandler.class.getName());
    /**
     * Handles ingest into the file manager
     * @param url
     */
    public IngestHandler(String url) {
    	this.url = url;
    }
    /**
     * Setup the file manager
     * @throws ConnectionException
     */
    public void setup() throws ConnectionException {
        try {
            LOG.log(Level.INFO,"Attempting to connect to filemanager at: "+url);
            this.client = new XmlRpcFileManagerClient(new URL(url));
        } catch (MalformedURLException e) {
            LOG.log(Level.SEVERE,"Cannot connect to file manager: "+e.getMessage());
            throw new ConnectionException(e.getMessage());
        } catch(ConnectionException e) {
            LOG.log(Level.SEVERE,"Cannot connect to file manager: "+e.getMessage());
            throw e;
        }
    }
    
    /**
     * Ingest stream
     * @param handle
     * @throws Exception
     */
    public void ingest(String handle) throws Exception {
        //Using defaults for now
        Metadata met = new Metadata();
        Product prod = new Product();
        //Extraction goes here
        prod.setProductStructure(Product.STRUCTURE_STREAM);
        prod.setProductType(client.getProductTypeByName("GenericStream"));
        prod.setProductName(handle);
        List<Reference> refs = new LinkedList<Reference>();
        Reference ref = new Reference();
        ref.setOrigReference("stream://"+handle);
        refs.add(ref);
        prod.setProductReferences(refs);
        client.ingestProduct(prod, met, false);
    }
}
