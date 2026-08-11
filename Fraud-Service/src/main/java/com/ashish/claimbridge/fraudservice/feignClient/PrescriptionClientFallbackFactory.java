package com.ashish.claimbridge.fraudservice.feignClient;

import com.ashish.claimbridge.fraudservice.dto.PrescriptionItemDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PrescriptionClientFallbackFactory implements FallbackFactory<PrescriptionClient> {
    private static final Logger log = LoggerFactory.getLogger(PrescriptionClientFallbackFactory.class);
    @Override
    public PrescriptionClient create(Throwable cause) {
        return (patientId, tenantId, role) -> {
            log.warn("Connection to the Patient Service Failed"+cause.getMessage());
           List<PrescriptionItemDto> dtos= new ArrayList<>();
           return  new ResponseEntity<>(dtos, HttpStatus.OK);
        };


    }
}
