package com.prakash.report.generator.processors;

import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prakash.report.generator.dao.ResourceDAO;
import com.prakash.report.generator.domain.HotelResourceInfo;

/**
 * This class is Last/sink processor in streams topology .This will get
 * HotelResource object and transforms to byte[] sends to kafka topic .
 * 
 * @author pprakash
 *
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ResourceTransformerPostProcessor implements Processor<String, HotelResourceInfo> {
    public static final Logger LOGGER = LoggerFactory.getLogger(ResourceTransformerPreProcessor.class);

    ObjectMapper maper = new ObjectMapper();

    ProcessorContext context;

    @Autowired
    private ResourceDAO resourceDAO;

    @Override
    public void close() {
        LOGGER.debug("Closing ResourceTransformerPostProcessor Streams");
    }

    @Override
    public void init(ProcessorContext context) {
        LOGGER.debug("ResourceTransformerPostProcessor Init called ");
        this.context = context;
    }

    /*
     * Transforms hotel id , HotelResourceInfo to byte[] and forwards to kafka
     * or commit offset etc .This case none.
     */
    @Override
    public void process(String hotelId, HotelResourceInfo value) {
        LOGGER.debug("Received hotelId: {}  and Resource details: {} ", hotelId, value);

    }
}
