package com.target.report.generator.processors;

import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorSupplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.target.report.generator.domain.HotelResourceInfo;

/**
 * This class is Last/sink processor in streams topology .This will get
 * HotelResource object and transforms to byte[] sends to kafka topic .
 * 
 * @author pprakash
 *
 */
@Component
public class ResourceTransformerPostProcessorSupplier implements ProcessorSupplier<String, HotelResourceInfo> {
    @Autowired
    ResourceTransformerPostProcessor ResourceTransformerPostProcessor;

    public Processor<String, HotelResourceInfo> get() {
        return ResourceTransformerPostProcessor;
    }

}