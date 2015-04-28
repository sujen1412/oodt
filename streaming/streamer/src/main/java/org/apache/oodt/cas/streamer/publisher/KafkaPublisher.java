package org.apache.oodt.cas.streamer.publisher;

import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.ProducerConfig;
import kafka.producer.KeyedMessage;
import kafka.serializer.DefaultEncoder;
import kafka.serializer.StringEncoder;

/**
 * @author starchmd
 *
 * This "publisher" publishes byte-array based messages to Kafka.
 */
public class KafkaPublisher implements Publisher {
    Producer<String,byte[]> producer;
    String topic = null;
    
    public static final String KEY = "KEY";

    public KafkaPublisher() {
    }
    @Override
    /**
     * Open up a connection to Kafka
     */
    public void open(String handle) {
        this.topic = handle.replace("/", "-");

        // TODO fix these: none of this is setup to work, Kafka peices not installed.
        Properties props = new Properties();
        //props.put("broker.list", System.getProperty("streamer.kafka.broker.list","no-broker"));
        props.put("metadata.broker.list", System.getProperty("streamer.kafka.broker.list","no-broker"));
        props.put("key.serializer.class",StringEncoder.class.getName());
        props.put("serializer.class",DefaultEncoder.class.getName());
        props.put("zk.connect",System.getProperty("streamer.kafka.zk.connect","localhost:2181"));
        ProducerConfig config = new ProducerConfig(props);
        producer = new Producer<String,byte[]>(config);
    }

    @Override
    public boolean publish(byte[] bytes) {
        KeyedMessage<String,byte[]> msg = new KeyedMessage<String,byte[]>(this.topic,KEY,bytes);
        producer.send(msg);
        return true;
    }

    @Override
    public void close() {
        producer.close();
    }

}
