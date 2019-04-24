package com.target.report.generator.service;

import java.util.List;

import com.target.report.generator.domain.HotelResourceInfo;
import com.target.report.generator.domain.HotelUser;
import com.target.report.generator.domain.ResourceReport;

public interface ReportService {
    public void sendResourceDetails(HotelResourceInfo resource);

    public ResourceReport getConsolidatedResourceReport(String hotelId, Long startDate, Long endDate);

    public List<HotelResourceInfo> getDetailedResourceReport(String hotelId, Long startDate, Long endDate);

    public void validateUserHotel(HotelUser user, String hotelId);

}
