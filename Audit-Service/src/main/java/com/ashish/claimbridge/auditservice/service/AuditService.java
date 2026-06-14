package com.ashish.claimbridge.auditservice.service;

import com.ashish.claimbridge.auditservice.event.AuditEvent;
import com.ashish.claimbridge.auditservice.repository.AuditRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class AuditService {
    private final AuditRepository auditRepository;
    private final ObjectMapper objectMapper;
    @Autowired
    public AuditService(AuditRepository auditRepository, ObjectMapper objectMapper) {
        this.auditRepository = auditRepository;
        this.objectMapper = objectMapper;
    }
    public void saveAuditEvent(String payload, String topic) {
        try{
            JsonNode node = objectMapper.readTree(payload);
            AuditEvent auditEvent = new AuditEvent();
            auditEvent.setTopic(topic);
            auditEvent.setPayload(payload);
            auditEvent.setReceivedAt(LocalDateTime.now());
            auditEvent.setEventType(topic.toUpperCase().replace(".", "_"));

            if (node.has("claimId")) {
                auditEvent.setClaimId(node.get("claimId").asLong());
            }
            if (node.has("patientId")) {
                auditEvent.setPatientId(node.get("patientId").asLong());
            }
            if (node.has("hospitalId")) {
                auditEvent.setHospitalId(node.get("hospitalId").asText());
            }
            if (node.has("insurerId")) {
                auditEvent.setInsurerId(node.get("insurerId").asText());
            }

            auditRepository.save(auditEvent);
            System.out.println("Audit saved: " + topic +
                    " claimId: " + auditEvent.getClaimId());

        }catch(Exception e){
           System.out.println(e.getMessage()+"Unable to complete Request");
        }


    }

    public  List<AuditEvent> findByClaimId(Long claimId,String role, String tenantId)  {

        List<AuditEvent> events;
        if (role.equals("ROLE_HOSPITAL") || role.equals("ROLE_HOSPITAL_USER")) {
            events = auditRepository
                    .findByClaimIdAndHospitalId(claimId, tenantId);
        }
        else if(role.equals("ROLE_INSURER") || role.equals("ROLE_INSURER_USER")) {
            events = auditRepository
                    .findByClaimIdAndInsurerId(claimId, tenantId);
        }
        else{
            throw new AccessDeniedException("Unauthorized: Role [" + role + "] does not have permission to view audit logs.");
        }
        if (events.isEmpty()) {
            throw new EntityNotFoundException("No audit events found for tenant: " + tenantId);
        }
        return events;
    }


    public List<AuditEvent> findByPatientId(Long patientId, String role, String tenantId)  {
       List<AuditEvent> events;
       if(role.equals("ROLE_HOSPITAL_USER") || role.equals("ROLE_HOSPITAL")) {
           events = auditRepository.findByPatientIdAndHospitalId(patientId, tenantId);
       }
       else if (role.equals("ROLE_INSURER_USER") || role.equals("ROLE_INSURER")) {
           events = auditRepository.findByPatientIdAndInsurerId(patientId, tenantId);
       }
       else {
           throw new AccessDeniedException("not Allowed: Role [" + role + "] does not have permission to view audit logs.");
       }
       if (events.isEmpty()) {
           throw new EntityNotFoundException("No audit events found for tenant: " + tenantId);
       }
       return events;
    }

    public List<AuditEvent> findByHospitalId(String hospitalId, String role, String tenantId) {
        if(!"ROLE_HOSPITAL".equals(role) && !"ROLE_HOSPITAL_USER".equals(role)) {
            throw new AccessDeniedException("Not Authorized: ");
        }
        if(!hospitalId.equals(tenantId)) {
            throw new AccessDeniedException("hospital ID Dont match fpr this Hospital ");
        }
        List<AuditEvent> events = auditRepository.findByHospitalId(hospitalId);
        if (events.isEmpty()) {
            throw new EntityNotFoundException("No audit events found for Hospital   : " + tenantId);
        }
        return events;
    }

    public List<AuditEvent> findByInsurerId(String insurerId, String role, String tenantId) {
        if(!"ROLE_INSURER".equals(role) && !"ROLE_INSURER_USER".equals(role)) {
            throw new AccessDeniedException("Not Authorized: ");
        }
        if(!insurerId.equals(tenantId)) {
            throw new AccessDeniedException("Insurer ID Dont match for this Insurance Company ");
        }
        List<AuditEvent> events= auditRepository.findByInsurerId(insurerId);
        if (events.isEmpty()) {
            throw new EntityNotFoundException("No audit events found for Insurer   : " + tenantId);
        }
        return events;
    }


    public List<AuditEvent> findByEventType(String eventType, String role, String tenantId) {
       List<AuditEvent> events;
        if(role.equals("ROLE_HOSPITAL_USER") || role.equals("ROLE_HOSPITAL")) {
            events = auditRepository.findByEventTypeAndHospitalId(eventType, tenantId);
        }
        else if("ROLE_INSURER_USER".equals(role) ||  "ROLE_INSURER".equals(role)) {
            events = auditRepository.findByEventTypeAndInsurerId(eventType, tenantId);
        }
        else {
            throw new AccessDeniedException("Not Authorized: ");
        }


        if(events.isEmpty()){
            throw new EntityNotFoundException("No audit events found for eventType: " + eventType);
        }
        return events;

    }
}
