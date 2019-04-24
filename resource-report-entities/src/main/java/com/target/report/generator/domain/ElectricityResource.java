package com.target.report.generator.domain;

public class ElectricityResource extends HotelResource {
    private int electricityConsumed;

    public ElectricityResource() {
        setResourceType(ResourceType.ELECTRICITY);
        setUnits("KWH");
    }

    public int getElectricityConsumed() {
        return electricityConsumed;
    }

    public void setElectricityConsumed(int electricityConsumed) {
        this.electricityConsumed = electricityConsumed;
    }
}
