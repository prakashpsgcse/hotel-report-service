package com.target.report.generator.processors;

import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorSupplier;
import org.springframework.beans.factory.annotation.Autowired;
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

    public Processor<byte[], byte[]> get() {
        return ResourceTransformerPreProcessor;
    }

}