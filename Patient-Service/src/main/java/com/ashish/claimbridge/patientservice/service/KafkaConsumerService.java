package com.ashish.claimbridge.patientservice.service;

import com.ashish.claimbridge.patientservice.event.ClaimEvent;
import com.ashish.claimbridge.patientservice.event.HospitalCreateEvent;
import com.ashish.claimbridge.patientservice.event.InsurerCreateEvent;
import com.ashish.claimbridge.patientservice.event.OrganizationUpdateEvent;
import com.ashish.claimbridge.patientservice.model.Hospital;
import com.ashish.claimbridge.patientservice.model.InsurancePolicy;
import com.ashish.claimbridge.patientservice.model.Insurer;
import com.ashish.claimbridge.patientservice.model.PolicyStatus;
import com.ashish.claimbridge.patientservice.repository.HospitalRepository;

import com.ashish.claimbridge.patientservice.repository.InsurancePolicyRepository;
import com.ashish.claimbridge.patientservice.repository.InsurerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class KafkaConsumerService {
    private final HospitalRepository hospitalRepository;
    private final InsurerRepository insurerRepository;
    private final InsurancePolicyRepository insurancePolicyRepository;

    @Autowired
    public KafkaConsumerService(HospitalRepository hospitalRepository, InsurerRepository insurerRepository, InsurancePolicyRepository insurancePolicyRepository) {
        this.hospitalRepository = hospitalRepository;
        this.insurerRepository = insurerRepository;
        this.insurancePolicyRepository = insurancePolicyRepository;
    }
    @KafkaListener(topics = "hospital-created",groupId = "patient-service-group")
    public void consumeHospitalCreated(String message) {
       try {
            ObjectMapper mapper = new ObjectMapper();
            HospitalCreateEvent event = mapper.readValue(message, HospitalCreateEvent.class);

            Hospital hospital = new Hospital();
            hospital.setHospitalZip(event.getZipCode());
            hospital.setHospitalCity(event.getCity());
            hospital.setHospitalName(event.getHospitalName());
            hospital.setHospitalState(event.getState());
            hospital.setPhoneNumber(event.getPhone());
            hospital.setEmail(event.getAdminEmail());
            hospital.setHospitalAddress(event.getAddress());
            hospital.setTenantId(event.getTenantId());
            hospitalRepository.save(hospital);
        }catch (Exception e){
           e.printStackTrace();
       }
    }

    @KafkaListener(topics = "insurer-created",groupId = "patient-service-group")
    public void consumeInsurerCreated(String message) {

        try{
            ObjectMapper mapper = new ObjectMapper();
            InsurerCreateEvent event = mapper.readValue(message, InsurerCreateEvent.class);
            Insurer insurer = new Insurer();
            insurer.setInsurerName(event.getInsurerName());
            insurer.setInsurerCity(event.getCity());
            insurer.setInsurerState(event.getState());
            insurer.setInsurerZip(event.getZipCode());
            insurer.setInsurerAddress(event.getAddress());
            insurer.setTenantId(event.getTenantId());
            insurer.setInsurerPhone(event.getPhone());
            insurer.setInsurerEmail(event.getAdminEmail());
            insurerRepository.save(insurer);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @KafkaListener(topics= "organization-updated",groupId ="patient-service-group")
    public void consumeOrganizationUpdated(String message) {
        try{
            ObjectMapper mapper = new ObjectMapper();
            OrganizationUpdateEvent event = mapper.readValue(message, OrganizationUpdateEvent.class);
            if(event.getType().equals("HOSPITAL")){
                Hospital hospital= hospitalRepository.findByTenantId(event.getTenantId())
                        .orElseThrow(()-> new RuntimeException("hospital not found"));
                hospital.setHospitalZip(event.getZipCode());
                hospital.setHospitalCity(event.getCity());
                hospital.setHospitalState(event.getState());
                hospital.setPhoneNumber(event.getPhone());
                hospital.setHospitalAddress(event.getAddress());
                hospitalRepository.save(hospital);
            }
            else{
                Insurer insurer = insurerRepository.findByTenantId(event.getTenantId())
                        .orElseThrow(()-> new RuntimeException("insurer not found"));
                insurer.setInsurerAddress(event.getAddress());
                insurer.setInsurerCity(event.getCity());
                insurer.setInsurerPhone(event.getPhone());
                insurer.setInsurerState(event.getState());
                insurer.setInsurerZip(event.getZipCode());
                insurerRepository.save(insurer);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    @KafkaListener(topics= "claim-approved",groupId="patient-service-group")
        public void consumeClaimApprovedEvent(String message) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ClaimEvent event = mapper.readValue(message, ClaimEvent.class);
           InsurancePolicy policy= insurancePolicyRepository.findById(event.getInsurancePolicyId())
                   .orElseThrow(()-> new RuntimeException("insurance policy not found"));
           policy.setUsedAmount(policy.getUsedAmount()+event.getApprovedAmount());
           if(policy.getUsedAmount()>=policy.getCoverageLimit()){
               policy.setStatus(PolicyStatus.INACTIVE);
           }
           insurancePolicyRepository.save(policy);
        }catch (Exception e){
            e.printStackTrace();
        }
        }



}
