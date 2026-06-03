package com.ashish.claimbridge.auditservice.controller;

import com.ashish.claimbridge.auditservice.event.AuditEvent;
import com.ashish.claimbridge.auditservice.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/audit")
public class AuditController {

    private final AuditService auditService;
    @Autowired
    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }


    @GetMapping("claims/{claimId}")
    public ResponseEntity<List<AuditEvent>> getClaimHistory(
            @PathVariable("claimId") Long claimId, @RequestHeader("role")String role,
            @RequestHeader("tenantId")String tenantId ){
        return ResponseEntity.ok(auditService.findByClaimId(claimId,role, tenantId));
    }

    @GetMapping("claim/{hospitalId}")
    public ResponseEntity<List<AuditEvent>> getClaimHistoryForHospital(
            @PathVariable String hospitalId, @RequestHeader("role") String role,
            @RequestHeader("tenantId") String tenantId){
        return ResponseEntity.ok(auditService.findByHospitalId(hospitalId,role,tenantId));
    }

    @GetMapping("claim/{patientId}")
    public ResponseEntity<List<AuditEvent>> getClaimHistoryForPatient(
            @PathVariable Long patientId, @RequestHeader("role") String role,
            @RequestHeader("tenantId") String tenantId){
        return ResponseEntity.ok(auditService.findByPatientId(patientId,role,tenantId));
    }

    @GetMapping("/events/{eventType}")
    public ResponseEntity<List<AuditEvent>> getByEventType(
            @PathVariable String eventType, @RequestHeader("role") String role,
            @RequestHeader("tenantId") String tenantId){
        return ResponseEntity.ok(auditService.findByEventType(eventType,role,tenantId));

    }

    @GetMapping("claim/{insurerId}")
    public ResponseEntity<List<AuditEvent>> getByInsurerId(
            @PathVariable String insurerId, @RequestHeader("role") String role,
            @RequestHeader("tenantId") String tenantId){
        return ResponseEntity.ok(auditService.findByInsurerId(insurerId,role,tenantId));
    }






}
