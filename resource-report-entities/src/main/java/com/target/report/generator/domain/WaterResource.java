package com.target.report.generator.domain;

public class WaterResource extends HotelResource {

    private int waterConsumed;

    public WaterResource() {
        setResourceType(ResourceType.WATER);
        setUnits("Liters");
    }

    public int getWaterConsumed() {
        return waterConsumed;
    }

    public void setWaterConsumed(int waterConsumed) {
        this.waterConsumed = waterConsumed;
    }

}
