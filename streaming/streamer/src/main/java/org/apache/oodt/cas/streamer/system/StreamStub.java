package org.apache.oodt.cas.streamer.system;

import org.apache.oodt.cas.streamer.ingest.IngestHandler;
import org.apache.oodt.cas.streamer.publisher.KafkaPublisher;
import org.apache.oodt.cas.streamer.publisher.Publisher;

public class StreamStub {
	
	private String url;
	private String handel;

	private IngestHandler ingest;
	private Publisher pub;
	
	public StreamStub(String url, String handel) {
		this.url = url;
		this.handel = handel;
	}
	
	public void setup() throws Exception {
		this.ingest = new IngestHandler(this.url);
        this.pub = new KafkaPublisher();
        pub.open(this.handel);
	}
	
	public void publish(byte[] data) {
		this.pub.publish(data);
	}
}
