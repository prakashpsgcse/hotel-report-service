package com.target.report.generator.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.target.report.generator.ReportGeneratorApplication;
import com.target.report.generator.utils.Constants;

@RestController
public class LoginController {
    public static final Logger LOGGER = LoggerFactory.getLogger(ReportGeneratorApplication.class);

    @RequestMapping(value = "/login", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<String> loginApplication(
            @AuthenticationPrincipal Authentication authentication) {
        LOGGER.debug("login success for user {} ", authentication);
        return new ResponseEntity<String>(Constants.LOGIN_SUCCESS, HttpStatus.OK);

    }

}
