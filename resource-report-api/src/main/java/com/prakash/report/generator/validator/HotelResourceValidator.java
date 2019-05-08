package com.prakash.report.generator.validator;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.prakash.report.generator.domain.HotelResourceInfo;

/**
 * Validater for Resource info from hotels
 * 
 * @author pprakash
 *
 */
@Service
public class HotelResourceValidator implements Validator {

    @Override
    public boolean supports(Class<?> arg0) {
        return HotelResourceInfo.class.equals(arg0);
    }

    @Override
    public void validate(Object object, Errors error) {
        HotelResourceInfo info = (HotelResourceInfo) object;
        if (null == info.getHotelId()) {
            error.rejectValue("hotelId", "invalid", "HotelId cannot empty");
        }
        if (null == info.getHotelName()) {
            error.rejectValue("HotelName", "invalid", "HotelName cannot be empty");
        }
        if (null == info.getTimeStamp()) {
            error.rejectValue("timeStamp", "invalid", "Date cannot be empty");
        }

        if (null == info.getResources() || info.getResources().size() <= 0) {
            error.rejectValue("Resources", "invalid", "Resources cannot be empty");
        }
        // TODO validate individual resources
    }

}
