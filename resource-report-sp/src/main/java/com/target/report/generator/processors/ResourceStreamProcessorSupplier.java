package com.target.report.generator.processors;

import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorSupplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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

    @Autowired
    ApplicationContext ctx;

    /**
     * This should return new Instance .Number of Objects created should be
     * equal to number of stream thread. Toplology will be created for each
     * stream thread.
     */
    public Processor<String, HotelResourceInfo> get() {
        return ctx.getBean(ResourceStreamProcessor.class);
    }

}