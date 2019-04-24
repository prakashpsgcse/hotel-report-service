package com.target.report.generator.processors;

import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorSupplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.target.report.generator.domain.HotelResourceInfo;

/**
 * This Stream processor saves HotelResource event into mongoDb
 * 
 * @author pprakash
 *
 */
@Component
public class ResourceStreamProcessorSupplier implements ProcessorSupplier<String, HotelResourceInfo> {
    @Autowired
    ResourceStreamProcessor resourceStreamProcessor;

    public Processor<String, HotelResourceInfo> get() {
        return resourceStreamProcessor;
    }

}