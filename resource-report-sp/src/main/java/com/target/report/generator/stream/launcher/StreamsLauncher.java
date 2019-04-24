package com.target.report.generator.stream.launcher;

/*
 * Interface should be implemented by Streams launcher.Now it supports KafkaStreamslauncer .
 * To support other streaming platform(kinesis etc) implements this interface  
 */
public interface StreamsLauncher {
    public void launch();

}
