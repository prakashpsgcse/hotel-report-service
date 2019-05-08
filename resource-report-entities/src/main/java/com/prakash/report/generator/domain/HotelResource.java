package com.prakash.report.generator.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "resourceType", visible = true)
@JsonSubTypes({
        @Type(value = WaterResource.class, name = "WATER"),
        @Type(value = ElectricityResource.class, name = "ELECTRICITY"),
        @Type(value = WasteResource.class, name = "WASTE"),
})
public abstract class HotelResource {

    private ResourceType resourceType;

    private String units;

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public ResourceType getResourceType() {
        return this.resourceType;
    }

}
