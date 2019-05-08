package com.prakash.report.generator.processors;

import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorSupplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.prakash.report.generator.domain.HotelResourceInfo;

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

    @Autowired
    ApplicationContext ctx;

    /**
     * This should return new Instance .Number of Objects created should be
     * equal to number of stream thread. Toplology will be created for each
     * stream thread.
     */
    public Processor<String, HotelResourceInfo> get() {
        return ctx.getBean(ResourceTransformerPostProcessor.class);
    }

}