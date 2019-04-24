package com.target.report.generator.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.target.report.generator.domain.HotelUser;

public interface HotelUserDAO extends MongoRepository<HotelUser, String> {

}
