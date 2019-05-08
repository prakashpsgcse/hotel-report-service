package com.prakash.report.generator.rest;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.prakash.report.generator.ReportGeneratorApplication;
import com.prakash.report.generator.dao.HotelUserDAO;
import com.prakash.report.generator.domain.HotelResourceInfo;
import com.prakash.report.generator.domain.ResourceReport;
import com.prakash.report.generator.security.HotelUserPrincipal;
import com.prakash.report.generator.service.ReportService;
import com.prakash.report.generator.validator.HotelResourceValidator;

@RestController
public class ReportController {
    public static final Logger LOGGER = LoggerFactory.getLogger(ReportGeneratorApplication.class);

    @Autowired
    private HotelResourceValidator hotelResourceValidator;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(hotelResourceValidator);
    }

    @Autowired
    private ReportService reportService;

    @Autowired
    HotelUserDAO HotelUserDAO;

    /**
     * Publish report to Kafka after successful validation
     * 
     * @param hotelId
     * @param resource
     * @param authentication
     * @return
     */
    @PostMapping(value = "/v1.0/hotel/resource/{hotelId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> publishReport(@PathVariable("hotelId") String hotelId,
            @RequestBody @Valid HotelResourceInfo resource,
            @AuthenticationPrincipal HotelUserPrincipal authentication) {
        LOGGER.debug("publishing report for hotel id: {} with data: {} ",
                hotelId, resource);
        reportService.validateUserHotel(authentication.getHotelUser(), hotelId);
        reportService.sendResourceDetails(resource);
        return new ResponseEntity<String>("Resource Published",
                HttpStatus.ACCEPTED);

    }

    /**
     * Generate Consolidated report after validation .sends single report with
     * Consolidated result
     * 
     * @param hotelId
     * @param startDate
     * @param endDate
     * @param authentication
     * @return
     */
    @GetMapping(value = "/v1.0/hotel/report/{hotelId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResourceReport> getConsolidatedReportWithDate(@PathVariable("hotelId") String hotelId,
            @RequestParam("startTimeStamp") Long startDate,
            @RequestParam("endTimeStamp") long endDate,
            @AuthenticationPrincipal HotelUserPrincipal authentication) {
        LOGGER.debug("generating report between {}  and {} for hotel id: ", startDate, endDate, hotelId);
        reportService.validateUserHotel(authentication.getHotelUser(), hotelId);
        ResourceReport reports = reportService.getConsolidatedResourceReport(hotelId, startDate, endDate);
        return new ResponseEntity<ResourceReport>(reports, HttpStatus.OK);
    }

    /**
     * Return all resource details published between dates.
     * 
     * @param hotelId
     * @param startDate
     * @param endDate
     * @param authentication
     * @return
     */

    @GetMapping(value = "/v1.0/hotel/report/all/{hotelId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<HotelResourceInfo>> getReportWithDate(@PathVariable("hotelId") String hotelId,
            @RequestParam("startTimeStamp") Long startDate,
            @RequestParam("endTimeStamp") long endDate,
            @AuthenticationPrincipal HotelUserPrincipal authentication) {
        LOGGER.debug("generating detailed report between {}  and {} for hotel id: ", startDate, endDate, hotelId);
        reportService.validateUserHotel(authentication.getHotelUser(), hotelId);
        List<HotelResourceInfo> reports = reportService.getDetailedResourceReport(hotelId, startDate, endDate);
        return new ResponseEntity<List<HotelResourceInfo>>(reports, HttpStatus.OK);
    }
}
