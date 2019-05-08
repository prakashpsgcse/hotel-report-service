package com.prakash.report.generator.dao;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.prakash.report.generator.domain.HotelResourceInfo;

public interface ResourceDAO extends MongoRepository<HotelResourceInfo, String> {

    public List<HotelResourceInfo> findByHotelIdAndTimeStampBetween(String hotelId, long fromDate, long toDate);

}
