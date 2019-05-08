package com.prakash.report.generator.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prakash.report.generator.ReportGeneratorApplication;
import com.prakash.report.generator.dao.ResourceDAO;
import com.prakash.report.generator.domain.ElectricityResource;
import com.prakash.report.generator.domain.HotelResource;
import com.prakash.report.generator.domain.HotelResourceInfo;
import com.prakash.report.generator.domain.HotelUser;
import com.prakash.report.generator.domain.ResourceReport;
import com.prakash.report.generator.domain.ResourceType;
import com.prakash.report.generator.domain.WasteResource;
import com.prakash.report.generator.domain.WaterResource;

@Service
public class ReportServiceImpl implements ReportService {
    public static final Logger LOGGER = LoggerFactory.getLogger(ReportGeneratorApplication.class);

    public static final ObjectMapper maper = new ObjectMapper();

    @Value("${kafka.report.topic}")
    private String topic;

    @Autowired
    private PublishService kafkaPublishService;

    @Autowired
    private ResourceDAO resourceDAO;

    @Override
    public void sendResourceDetails(HotelResourceInfo resource) {
        try {
            kafkaPublishService.publish(resource.getHotelId(), maper.writeValueAsString(resource), topic);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }

    @Override
    public ResourceReport getConsolidatedResourceReport(String hotelId, Long startDate, Long endDate) {

        ResourceReport report = new ResourceReport();
        report.setHotelId(hotelId);
        report.setEndDate(endDate);
        report.setStartDate(startDate);

        List<HotelResourceInfo> resources = resourceDAO.findByHotelIdAndTimeStampBetween(hotelId, startDate, endDate);
        if (resources.size() > 0) {
            report.setHotelName(resources.get(0).getHotelName());
            List<HotelResource> hotelResources = new ArrayList<HotelResource>();
            resources.forEach(hotelResource -> {
                hotelResources.addAll(hotelResource.getResources());
            });

            Map<ResourceType, List<HotelResource>> rs = hotelResources.stream().collect(Collectors.groupingBy(res -> {
                if (res.getResourceType().equals(ResourceType.ELECTRICITY)) {
                    return ResourceType.ELECTRICITY;
                }
                if (res.getResourceType().equals(ResourceType.WASTE)) {
                    return ResourceType.WASTE;
                }
                if (res.getResourceType().equals(ResourceType.WATER)) {
                    return ResourceType.WATER;
                }
                else {
                    return ResourceType.NONE;
                }
            }));
            ElectricityResource electricityResource = new ElectricityResource();
            WaterResource waterResource = new WaterResource();
            WasteResource wasteResource = new WasteResource();
            rs.get(ResourceType.ELECTRICITY).forEach(resource -> {
                electricityResource.setElectricityConsumed(
                        electricityResource.getElectricityConsumed() + ((ElectricityResource) resource).getElectricityConsumed());
            });

            rs.get(ResourceType.WATER).forEach(resource -> {
                waterResource.setWaterConsumed(
                        waterResource.getWaterConsumed() + ((WaterResource) resource).getWaterConsumed());
            });

            rs.get(ResourceType.WASTE).forEach(resource -> {
                wasteResource.setWasteProduced(
                        wasteResource.getWasteProduced() + ((WasteResource) resource).getWasteProduced());
            });

            ArrayList<HotelResource> list = new ArrayList<HotelResource>();
            list.add(wasteResource);
            list.add(electricityResource);
            list.add(waterResource);

            report.setResources(list);
        }
        return report;

    }

    @Override
    public List<HotelResourceInfo> getDetailedResourceReport(String hotelId, Long startDate, Long endDate) {

        List<HotelResourceInfo> resources = resourceDAO.findByHotelIdAndTimeStampBetween(hotelId, startDate, endDate);

        return resources;

    }

    /**
     * Validate that user details from JWT and the resource user wants to access
     * belongs to same
     */
    @Override
    public void validateUserHotel(HotelUser user, String hotelId) {
        if (!user.getHotelId().equals(hotelId)) {
            LOGGER.debug("User trying to access  resource of others . hotel id: {} with user : {} ",
                    hotelId, user);
            throw new AccessDeniedException("User Not Authorized to access resource");
        }

    }

}
