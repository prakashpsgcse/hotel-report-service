package com.target.report.generator.processors;

import java.io.IOException;

import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.target.report.generator.domain.HotelResourceInfo;

/**
 * This class is First/source processor in streams topology .This will get
 * byte[] from kafka and transforms byte[] to HotelResource object and forwards
 * to next processor
 * 
 * @author pprakash
 *
 */

@Component
public class ResourceTransformerPreProcessor implements Processor<byte[], byte[]> {
    public static final Logger LOGGER = LoggerFactory.getLogger(ResourceTransformerPreProcessor.class);

    ObjectMapper maper = new ObjectMapper();

    ProcessorContext context;

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
    public void process(byte[] key, byte[] value) {

        if (null == key || null == value) {
            LOGGER.error("Key or value cannot be null.key {} and value {}", key, value);
            throw new RuntimeException("Cannot process . Invalid Resource information ");
        }
        String hotelId = new String(key);
        String resourceData = new String(value);
        HotelResourceInfo info = null;
        try {
            info = maper.readValue(resourceData, HotelResourceInfo.class);
        } catch (IOException e) {
            LOGGER.error("Exceprion occured in transforming resource {} ", e);
            throw new RuntimeException("Cannot process . Invalid Resource information ", e);
        }
        LOGGER.debug("Received hotelId: {}  and Resource details: {} ", hotelId, info);
        context.forward(hotelId, info);

    }

}
