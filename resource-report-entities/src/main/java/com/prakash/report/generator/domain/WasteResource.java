package com.prakash.report.generator.domain;

public class WasteResource extends HotelResource {

    private int wasteProduced;

    public int getWasteProduced() {
        return wasteProduced;
    }

    public void setWasteProduced(int wasteProduced) {
        this.wasteProduced = wasteProduced;
    }

    public WasteResource() {
        setResourceType(ResourceType.WASTE);
        setUnits("KG");
    }
}
