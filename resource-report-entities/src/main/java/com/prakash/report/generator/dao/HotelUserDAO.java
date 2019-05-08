package com.prakash.report.generator.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.prakash.report.generator.domain.HotelUser;

public interface HotelUserDAO extends MongoRepository<HotelUser, String> {

}
