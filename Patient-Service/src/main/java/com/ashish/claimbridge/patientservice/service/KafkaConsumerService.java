package com.ashish.claimbridge.patientservice.service;

import com.ashish.claimbridge.patientservice.event.HospitalCreateEvent;
import com.ashish.claimbridge.patientservice.event.InsurerCreateEvent;
import com.ashish.claimbridge.patientservice.model.Hospital;
import com.ashish.claimbridge.patientservice.model.Insurer;
import com.ashish.claimbridge.patientservice.repository.HospitalRepository;

import com.ashish.claimbridge.patientservice.repository.InsurerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class KafkaConsumerService {
    private final HospitalRepository hospitalRepository;
    private final InsurerRepository insurerRepository;

    @Autowired
    public KafkaConsumerService(HospitalRepository hospitalRepository, InsurerRepository insurerRepository) {
        this.hospitalRepository = hospitalRepository;
        this.insurerRepository = insurerRepository;
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



}
