package com.prakash.report.generator.processors;

import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorSupplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * This class is First/source processor in streams topology .This will get
 * byte[] from kafka and transforms byte[] to HotelResource object and forwards
 * to next processor
 * 
 * @author pprakash
 *
 */
@Component
public class ResourceTransformerPreProcessorSupplier implements ProcessorSupplier<byte[], byte[]> {
    @Autowired
    ResourceTransformerPreProcessor ResourceTransformerPreProcessor;

    @Autowired
    ApplicationContext ctx;

    /**
     * This should return new Instance .Number of Objects created should be
     * equal to number of stream thread. Toplology will be created for each
     * stream thread.
     */
    public Processor<byte[], byte[]> get() {
        return ctx.getBean(ResourceTransformerPreProcessor.class);
    }

}