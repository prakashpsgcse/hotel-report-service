package com.target.report.generator.domain;

import java.util.List;

public class ResourceReport {

    private String hotelId;
    private String hotelName;
    private Long startDate;
    private Long endDate;
    private List<HotelResource> resources;

    public String getHotelId() {
        return hotelId;
    }

    public void setHotelId(String hotelId) {
        this.hotelId = hotelId;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }

    public List<HotelResource> getResources() {
        return resources;
    }

    public void setResources(List<HotelResource> resources) {
        this.resources = resources;
    }

    @Override
    public String toString() {
        return "ResourceReport [hotelId=" + hotelId + ", hotelName=" + hotelName + ", startDate=" + startDate + ", endDate=" + endDate
                + ", resources=" + resources + "]";
    }

}
