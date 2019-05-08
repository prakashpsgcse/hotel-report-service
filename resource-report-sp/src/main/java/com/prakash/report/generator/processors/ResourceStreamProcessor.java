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
 * This class is second stream processor in streams topology .This will get
 * HotelResource object from ResourceTransformerPreProcessor and saves
 * HotelResource event into mongoDb and forwards to next processor
 * 
 * @author pprakash
 *
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ResourceStreamProcessor implements Processor<String, HotelResourceInfo> {
    public static final Logger LOGGER = LoggerFactory.getLogger(ResourceTransformerPreProcessor.class);

    ObjectMapper maper = new ObjectMapper();

    ProcessorContext context;

    @Autowired
    private ResourceDAO resourceDAO;

    @Override
    public void close() {
        LOGGER.debug("Closing ResourceTransformerPreProcessor Streams");
    }

    @Override
    public void init(ProcessorContext context) {
        LOGGER.debug("ResourceTransformerPreProcessor Init called ");
        this.context = context;
    }

    /*
     * Transforms byte[] key ,value to hotel id , HotelResourceInfo and forwards
     * to next processor
     */
    @Override
    public void process(String hotelId, HotelResourceInfo value) {
        LOGGER.debug("Received hotelId: {}  and Resource details: {} ", hotelId, value);
        System.out.println(resourceDAO.count());
        HotelResourceInfo savedInfo = resourceDAO.save(value);
        LOGGER.debug("Saved Resource info with id {} ", savedInfo.getId());
        context.forward(hotelId, savedInfo);

    }
}
