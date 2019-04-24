package com.target.report.generator.domain;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "HotelResourceInfo")
public class HotelResourceInfo {

    @Id
    private String id;
    private String hotelId;
    private String hotelName;
    private Long timeStamp;
    private List<HotelResource> resources;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public List<HotelResource> getResources() {
        return resources;
    }

    public void setResources(List<HotelResource> resources) {
        this.resources = resources;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((hotelId == null) ? 0 : hotelId.hashCode());
        result = prime * result + ((timeStamp == null) ? 0 : timeStamp.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        HotelResourceInfo other = (HotelResourceInfo) obj;
        if (hotelId == null) {
            if (other.hotelId != null) return false;
        }
        else if (!hotelId.equals(other.hotelId)) return false;
        if (timeStamp == null) {
            if (other.timeStamp != null) return false;
        }
        else if (!timeStamp.equals(other.timeStamp)) return false;
        return true;
    }

    @Override
    public String toString() {
        return "HotelResourceInfo [id=" + id + ", hotelId=" + hotelId + ", hotelName=" + hotelName + ", timeStamp=" + timeStamp
                + ", resources=" + resources + "]";
    }

}
