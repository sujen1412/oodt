package org.apache.oodt.cas.streamer.examples;

import java.util.Random;

import org.apache.oodt.cas.streamer.system.StreamStub;

public class TimingStreamer {
	/**
	 * Times writes
	 * @param args
	 */
	public static void main(String args[]) {
		byte[] block = new byte[4096];
		Random rand = new Random();
		StreamStub stub = new StreamStub(args[0],args[1]);
		try {
			stub.setup();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}
		//
		while (true) {
			rand.nextBytes(block);
			double mills = System.nanoTime()/1000000.000;
			stub.publish(block);
			System.out.println("Time(ms): "+((System.nanoTime()/1000000.000)-mills));
		}
	}
}
